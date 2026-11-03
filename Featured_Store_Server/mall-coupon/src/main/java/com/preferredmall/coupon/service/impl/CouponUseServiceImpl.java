package com.preferredmall.coupon.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.preferredmall.common.exception.BusinessException;
import com.preferredmall.common.result.ResultCode;
import com.preferredmall.coupon.entity.*;
import com.preferredmall.coupon.mapper.*;
import com.preferredmall.coupon.service.CouponUseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 券锁/核/回滚：核心数据结构是 coupon_order_rel（占用记录）
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CouponUseServiceImpl implements CouponUseService {

    private final CouponUserMapper couponUserMapper;
    private final CouponMapper couponMapper;
    private final CouponOrderRelMapper couponOrderRelMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BigDecimal calcAndLock(Long userCouponId, Long userId, BigDecimal orderAmount, List<Long> productIds, String orderNo) {
        CouponUser uc = couponUserMapper.selectById(userCouponId);
        if (uc == null || !uc.getUserId().equals(userId)) {
            throw new BusinessException(ResultCode.COUPON_NOT_EXIST);
        }
        if (uc.getUseStatus() != 1) {
            throw new BusinessException(ResultCode.COUPON_USED);
        }
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(uc.getValidStart()) || now.isAfter(uc.getValidEnd())) {
            throw new BusinessException(ResultCode.COUPON_EXPIRED);
        }
        Coupon coupon = couponMapper.selectById(uc.getCouponId());
        if (coupon == null || coupon.getStatus() != 1) {
            throw new BusinessException(ResultCode.COUPON_NOT_EXIST);
        }
        if (coupon.getMinAmount().compareTo(orderAmount) > 0) {
            throw new BusinessException(ResultCode.COUPON_CONDITION_NOT_MET);
        }

        BigDecimal discount = computeDiscount(coupon, orderAmount);

        // 锁定记录：rel_status=1，写订单号供核销/回滚索引
        CouponOrderRel rel = new CouponOrderRel();
        rel.setOrderNo(orderNo);
        rel.setUserCouponId(userCouponId);
        rel.setActivityId(coupon.getId());
        rel.setDiscountAmount(discount);
        rel.setRelStatus(1);
        couponOrderRelMapper.insert(rel);

        return discount;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void confirmUse(String orderNo) {
        List<CouponOrderRel> rels = couponOrderRelMapper.selectList(
                new LambdaQueryWrapper<CouponOrderRel>()
                        .eq(CouponOrderRel::getOrderNo, orderNo)
                        .eq(CouponOrderRel::getRelStatus, 1));
        LocalDateTime now = LocalDateTime.now();
        for (CouponOrderRel rel : rels) {
            // 条件更新保证幂等：只有仍处于锁定态的记录才会被核销
            int rows = couponOrderRelMapper.update(null, new LambdaUpdateWrapper<CouponOrderRel>()
                    .eq(CouponOrderRel::getId, rel.getId())
                    .eq(CouponOrderRel::getRelStatus, 1)
                    .set(CouponOrderRel::getRelStatus, 2)
                    .set(CouponOrderRel::getUseTime, now));
            if (rows == 0) {
                continue;
            }
            CouponUser uc = couponUserMapper.selectById(rel.getUserCouponId());
            if (uc != null && uc.getUseStatus() == 1) {
                couponUserMapper.update(null, new LambdaUpdateWrapper<CouponUser>()
                        .eq(CouponUser::getId, uc.getId())
                        .set(CouponUser::getUseStatus, 2)
                        .set(CouponUser::getUseTime, now));
            }
            // 模板 used_quota + 1
            couponMapper.update(null, new LambdaUpdateWrapper<Coupon>()
                    .eq(Coupon::getId, rel.getActivityId())
                    .setSql("used_quota = used_quota + 1"));
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void rollbackByOrderNo(String orderNo) {
        List<CouponOrderRel> rels = couponOrderRelMapper.selectList(
                new LambdaQueryWrapper<CouponOrderRel>()
                        .eq(CouponOrderRel::getOrderNo, orderNo)
                        .in(CouponOrderRel::getRelStatus, 1, 2));
        for (CouponOrderRel rel : rels) {
            boolean wasConfirmed = rel.getRelStatus() == 2;
            int rows = couponOrderRelMapper.update(null, new LambdaUpdateWrapper<CouponOrderRel>()
                    .eq(CouponOrderRel::getId, rel.getId())
                    .in(CouponOrderRel::getRelStatus, 1, 2)
                    .set(CouponOrderRel::getRelStatus, 3)
                    .set(CouponOrderRel::getRollbackTime, LocalDateTime.now()));
            if (rows == 0) {
                continue;
            }
            // 仅已核销的券需要恢复持券状态与模板用量；锁定态从未核销过，直接作废锁定即可
            if (wasConfirmed) {
                CouponUser uc = couponUserMapper.selectById(rel.getUserCouponId());
                if (uc != null && uc.getUseStatus() == 2) {
                    couponUserMapper.update(null, new LambdaUpdateWrapper<CouponUser>()
                            .eq(CouponUser::getId, uc.getId())
                            .set(CouponUser::getUseStatus, 1)
                            .set(CouponUser::getUseTime, null));
                }
                couponMapper.update(null, new LambdaUpdateWrapper<Coupon>()
                        .eq(Coupon::getId, rel.getActivityId())
                        .setSql("used_quota = used_quota - 1"));
            }
        }
    }

    /**
     * 计算抵扣金额（简化版，scope 与互斥组校验留给 calcAndLock 外部调用或后续迭代）
     */
    private BigDecimal computeDiscount(Coupon coupon, BigDecimal orderAmount) {
        return switch (coupon.getCouponType()) {
            case 1 -> coupon.getFaceValue().min(orderAmount); // 满减
            case 2 -> orderAmount.multiply(BigDecimal.ONE.subtract(coupon.getDiscountRate()))
                    .setScale(2, RoundingMode.HALF_UP); // 折扣
            case 3 -> orderAmount; // 无门槛全抵（金额不超过订单）
            case 4 -> coupon.getFaceValue().min(orderAmount); // 单品同满减
            default -> throw new BusinessException(ResultCode.COUPON_AMOUNT_EXCEED, "未知券类型");
        };
    }
}
