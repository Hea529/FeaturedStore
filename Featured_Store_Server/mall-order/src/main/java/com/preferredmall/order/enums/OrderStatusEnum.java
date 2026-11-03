package com.preferredmall.order.enums;

import lombok.Getter;

/**
 * 订单状态机
 */
@Getter
public enum OrderStatusEnum {

    PENDING_PAYMENT(1, "待支付"),
    PAID(2, "已支付"),
    SHIPPED(3, "已发货"),
    COMPLETED(4, "已完成"),
    CANCELLED(5, "已取消"),
    REFUND(6, "售后处理中");

    private final int code;
    private final String desc;

    OrderStatusEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static OrderStatusEnum of(int code) {
        for (OrderStatusEnum e : values()) {
            if (e.code == code) return e;
        }
        throw new IllegalArgumentException("未知订单状态: " + code);
    }
}
