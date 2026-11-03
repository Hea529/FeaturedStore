package com.preferredmall.coupon.service;

import java.math.BigDecimal;
import java.util.List;

/**
 * 优惠券用券服务：供 mall-order 下单编排注入（订单 → 试算 → 锁券 → 核销 → 回滚）
 */
public interface CouponUseService {

    /**
     * 试算优惠金额，并在 coupon_order_rel 写一条 rel_status=1 的「锁定」记录（与下单同事务）
     * @param orderNo 订单号（下单编排先生成订单号再锁券，保证核销/回滚可按 orderNo 索引）
     * @return 本次抵扣金额
     */
    BigDecimal calcAndLock(Long userCouponId, Long userId, BigDecimal orderAmount, List<Long> productIds, String orderNo);

    /**
     * 支付成功后把 coupon_user.use_status 置为 2（已用），写 use_time
     */
    void confirmUse(String orderNo);

    /**
     * 订单取消：回滚券（coupon_order_rel.rel_status → 2，恢复 coupon_user.use_status → 1）
     */
    void rollbackByOrderNo(String orderNo);
}
