package com.preferredmall.payment.service.impl;

import com.preferredmall.common.event.PaymentSuccessEvent;
import com.preferredmall.payment.entity.PaymentRecord;
import com.preferredmall.payment.mapper.PaymentRecordMapper;
import com.preferredmall.payment.service.PayService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 支付服务 stub：微信 API 未配置时返回占位数据，核心落库逻辑正确。
 * 真正上线接入微信支付需要：WechatPayClient + 配置 appid/mch_id/apiv3_key + 验签工具。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PayServiceImpl implements PayService {

    private final PaymentRecordMapper paymentRecordMapper;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public String createPrepay(String orderNo, BigDecimal amount, String description) {
        // TODO: 接入微信支付统一下单 API
        log.info("[Pay] createPrepay stub: orderNo={} amount={}", orderNo, amount);
        return "{\"prepay_id\":\"stub_prepay_id_" + orderNo + "\"}";
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void handlePaymentSuccess(String transactionId, String orderNo, BigDecimal amount, String raw) {
        PaymentRecord existing = paymentRecordMapper.selectOne(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<PaymentRecord>()
                        .eq(PaymentRecord::getTransactionId, transactionId));
        if (existing != null) {
            // 幂等
            return;
        }
        PaymentRecord r = new PaymentRecord();
        r.setOrderNo(orderNo);
        r.setTransactionId(transactionId);
        r.setAmount(amount);
        r.setPayType(1);
        r.setStatus(1);
        r.setNotifyRaw(raw);
        r.setPayTime(LocalDateTime.now());
        paymentRecordMapper.insert(r);
        // 发布支付成功事件：mall-order 监听并推进订单状态（同步事件，与流水落库同事务）
        eventPublisher.publishEvent(new PaymentSuccessEvent(orderNo, transactionId, amount));
    }

    @Override
    public void refund(String orderNo, BigDecimal refundAmount, String reason) {
        log.warn("[Pay] refund stub: orderNo={} amount={} reason={}", orderNo, refundAmount, reason);
    }

    @Override
    public PaymentRecord getByOrderNo(String orderNo) {
        return paymentRecordMapper.selectOne(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<PaymentRecord>()
                        .eq(PaymentRecord::getOrderNo, orderNo)
                        .last("LIMIT 1"));
    }
}
