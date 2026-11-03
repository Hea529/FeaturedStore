package com.preferredmall.common.result;

import lombok.Getter;

/**
 * 业务状态码枚举
 */
@Getter
public enum ResultCode {

    SUCCESS(200, "操作成功"),
    ERROR(500, "系统异常"),
    UNAUTHORIZED(401, "未授权"),
    FORBIDDEN(403, "禁止访问"),
    VALIDATE_FAILED(400, "参数检验失败"),

    // 1xxxx 鉴权
    NO_TOKEN(10001, "无 token"),
    TOKEN_EXPIRED(10002, "认证 token 过期"),
    REFRESH_TOKEN_EXPIRED(10003, "刷新 token 过期"),
    SIGNATURE_ERROR(10004, "认证签名错误/篡改"),

    // 2xxxx 用户域
    LOGIN_ERROR(20001, "用户名或密码错误"),
    USER_NOT_EXIST(20002, "用户不存在"),
    USER_EXIST(20003, "用户已存在"),
    USER_DISABLED(20004, "用户已禁用"),

    // 3xxxx 商品域
    PRODUCT_NOT_EXIST(30001, "商品不存在"),
    PRODUCT_OFFLINE(30002, "商品已下架"),
    STOCK_INSUFFICIENT(30003, "库存不足"),
    SPEC_NOT_EXIST(30004, "规格不存在"),

    // 4xxxx 订单域
    ORDER_NOT_EXIST(40001, "订单不存在"),
    ORDER_STATUS_ERROR(40002, "订单状态不允许该操作"),
    ORDER_CANCELLED(40003, "订单已取消"),

    // 5xxxx 支付域
    PAY_ERROR(50001, "支付失败"),
    PAY_NOT_FOUND(50002, "支付记录不存在"),

    // 6xxxx 优惠券域
    COUPON_NOT_EXIST(60001, "优惠券不存在"),
    COUPON_RECEIVED(60002, "您已领取过该优惠券"),
    COUPON_EXPIRED(60003, "优惠券已过期"),
    COUPON_USED(60004, "优惠券已使用"),
    COUPON_SCOPE_MISMATCH(60005, "优惠券不适用于当前商品"),
    COUPON_CONDITION_NOT_MET(60006, "未达到优惠券使用条件"),
    COUPON_AMOUNT_EXCEED(60007, "优惠券抵扣金额异常"),
    COUPON_STOCK_EMPTY(60008, "优惠券已领完");

    private final Integer code;
    private final String message;

    ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
