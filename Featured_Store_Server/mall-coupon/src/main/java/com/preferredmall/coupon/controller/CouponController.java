package com.preferredmall.coupon.controller;

import com.preferredmall.common.context.BaseContext;
import com.preferredmall.common.exception.BusinessException;
import com.preferredmall.common.result.Result;
import com.preferredmall.common.result.ResultCode;
import com.preferredmall.coupon.entity.Coupon;
import com.preferredmall.coupon.entity.CouponUser;
import com.preferredmall.coupon.service.CouponService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * C 端：可领券列表 / 领取 / 我的券
 */
@Tag(name = "优惠券")
@RestController
@RequestMapping("/api/coupon")
@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponService;

    private Long uid() {
        Long u = BaseContext.currentUserId();
        if (u == null) throw new BusinessException(ResultCode.UNAUTHORIZED);
        return u;
    }

    @Operation(summary = "获取可领券列表")
    @GetMapping("/list")
    public Result<List<Coupon>> list() {
        return Result.success(couponService.listReceiveable());
    }

    @Operation(summary = "领取优惠券")
    @PostMapping("/receive/{couponId}")
    public Result<CouponUser> receive(@PathVariable Long couponId) {
        return Result.success(couponService.receive(uid(), couponId));
    }

    @Operation(summary = "我的优惠券列表")
    @GetMapping("/my")
    public Result<List<CouponUser>> my() {
        return Result.success(couponService.listMine(uid()));
    }
}
