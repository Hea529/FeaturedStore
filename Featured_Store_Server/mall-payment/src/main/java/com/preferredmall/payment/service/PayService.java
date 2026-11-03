package com.preferredmall.payment.service;

import com.preferredmall.payment.entity.PaymentRecord;

import java.math.BigDecimal;

/**
 * 支付服务：预下单 + 回调验签 + 退款
 */
public interface PayService {

    /**
     * 微信小程序预下单：返回前端所需的调起参数（stub，微信未配置返回占位数据）
     */
    String createPrepay(String orderNo, BigDecimal amount, String description);

    /**
     * 支付成功回调入口：验签 → 落 payment_record → 发布事件（mall-order 监听更新状态）
     */
    void handlePaymentSuccess(String transactionId, String orderNo, BigDecimal amount, String raw);

    /**
     * 退款（stub）
     */
    void refund(String orderNo, BigDecimal refundAmount, String reason);

    PaymentRecord getByOrderNo(String orderNo);
}
