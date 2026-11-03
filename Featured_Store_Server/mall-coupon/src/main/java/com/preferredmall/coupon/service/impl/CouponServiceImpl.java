package com.preferredmall.coupon.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.preferredmall.common.exception.BusinessException;
import com.preferredmall.common.result.ResultCode;
import com.preferredmall.coupon.entity.Coupon;
import com.preferredmall.coupon.entity.CouponUser;
import com.preferredmall.coupon.mapper.CouponMapper;
import com.preferredmall.coupon.mapper.CouponUserMapper;
import com.preferredmall.coupon.service.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CouponServiceImpl implements CouponService {

    private final CouponMapper couponMapper;
    private final CouponUserMapper couponUserMapper;

    @Override
    public List<Coupon> listReceiveable() {
        LocalDateTime now = LocalDateTime.now();
        return couponMapper.selectList(new LambdaQueryWrapper<Coupon>()
                .eq(Coupon::getStatus, 1)
                .and(w -> w.eq(Coupon::getValidMode, 2)
                        .or(o -> o.eq(Coupon::getValidMode, 1).gt(Coupon::getValidEnd, now)))
                .orderByDesc(Coupon::getId));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CouponUser receive(Long userId, Long couponId) {
        Coupon coupon = couponMapper.selectById(couponId);
        if (coupon == null || coupon.getStatus() != 1) {
            throw new BusinessException(ResultCode.COUPON_NOT_EXIST);
        }
        LocalDateTime now = LocalDateTime.now();
        if (coupon.getValidMode() == 1 && (now.isBefore(coupon.getValidStart()) || now.isAfter(coupon.getValidEnd()))) {
            throw new BusinessException(ResultCode.COUPON_EXPIRED);
        }
        // 每人限领
        if (coupon.getPerLimit() != null && coupon.getPerLimit() > 0) {
            long received = couponUserMapper.selectCount(new LambdaQueryWrapper<CouponUser>()
                    .eq(CouponUser::getCouponId, couponId)
                    .eq(CouponUser::getUserId, userId));
            if (received >= coupon.getPerLimit()) {
                throw new BusinessException(ResultCode.COUPON_RECEIVED);
            }
        }
        // 发行量扣减（条件更新，并发安全）
        int rows = couponMapper.update(null, new LambdaUpdateWrapper<Coupon>()
                .eq(Coupon::getId, couponId)
                .lt(Coupon::getReceiveQuota, coupon.getTotalQuota())
                .setSql("receive_quota = receive_quota + 1"));
        if (rows == 0) {
            throw new BusinessException(ResultCode.COUPON_STOCK_EMPTY);
        }
        // 写持券：valid_mode=2 领取后 N 天有效
        CouponUser uc = new CouponUser();
        uc.setUserId(userId);
        uc.setCouponId(couponId);
        if (coupon.getValidMode() == 2) {
            uc.setValidStart(now);
            uc.setValidEnd(now.plusDays(coupon.getValidDays() == null ? 0 : coupon.getValidDays()));
        } else {
            uc.setValidStart(coupon.getValidStart());
            uc.setValidEnd(coupon.getValidEnd());
        }
        uc.setUseStatus(1);
        uc.setReceiveTime(now);
        couponUserMapper.insert(uc);
        return uc;
    }

    @Override
    public List<CouponUser> listMine(Long userId) {
        return couponUserMapper.selectList(new LambdaQueryWrapper<CouponUser>()
                .eq(CouponUser::getUserId, userId)
                .orderByDesc(CouponUser::getId));
    }
}
