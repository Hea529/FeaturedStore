package com.preferredmall.common.event;

import java.math.BigDecimal;

/**
 * 支付成功事件：mall-payment 发布，mall-order 监听并推进订单状态。
 * 放在 common 以避免 payment ↔ order 模块循环依赖。
 */
public record PaymentSuccessEvent(String orderNo, String transactionId, BigDecimal amount) {
}
