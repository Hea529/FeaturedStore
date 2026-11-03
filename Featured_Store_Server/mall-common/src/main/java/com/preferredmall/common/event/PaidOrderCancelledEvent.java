package com.preferredmall.common.event;

import java.math.BigDecimal;

/**
 * 已支付订单取消事件：mall-order 发布，mall-payment 监听并执行退款。
 */
public record PaidOrderCancelledEvent(String orderNo, BigDecimal amount) {
}
