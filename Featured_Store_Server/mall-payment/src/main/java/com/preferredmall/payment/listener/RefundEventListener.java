package com.preferredmall.payment.listener;

import com.preferredmall.common.event.PaidOrderCancelledEvent;
import com.preferredmall.payment.service.PayService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * 监听「已支付订单取消」事件（mall-order 发布），执行退款。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RefundEventListener {

    private final PayService payService;

    @EventListener
    public void onPaidOrderCancelled(PaidOrderCancelledEvent event) {
        log.info("[Pay] 收到已支付订单取消事件，发起退款: orderNo={} amount={}", event.orderNo(), event.amount());
        payService.refund(event.orderNo(), event.amount(), "订单取消自动退款");
    }
}
