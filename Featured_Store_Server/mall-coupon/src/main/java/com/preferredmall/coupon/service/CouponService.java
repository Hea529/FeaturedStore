package com.preferredmall.coupon.service;

import com.preferredmall.coupon.entity.Coupon;
import com.preferredmall.coupon.entity.CouponUser;

import java.util.List;

/**
 * 优惠券 C 端服务：可领列表 / 领取 / 我的券
 */
public interface CouponService {

    /**
     * 当前可领取的券模板（已上架且未过期）
     */
    List<Coupon> listReceiveable();

    /**
     * 领取：校验模板状态、发行量（并发安全）、每人限领，写 coupon_user
     */
    CouponUser receive(Long userId, Long couponId);

    /**
     * 我的券列表
     */
    List<CouponUser> listMine(Long userId);
}
