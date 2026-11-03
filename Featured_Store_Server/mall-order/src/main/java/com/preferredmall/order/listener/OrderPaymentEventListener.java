package com.preferredmall.order.listener;

import com.preferredmall.common.event.PaymentSuccessEvent;
import com.preferredmall.order.service.OrderApplicationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * 监听支付成功事件（mall-payment 发布），推进订单状态。
 * 同步事件：与支付流水落库在同一事务，处理失败会一起回滚。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OrderPaymentEventListener {

    private final OrderApplicationService orderApplicationService;

    @EventListener
    public void onPaymentSuccess(PaymentSuccessEvent event) {
        log.info("[Order] 收到支付成功事件: orderNo={} transactionId={}", event.orderNo(), event.transactionId());
        orderApplicationService.paySuccessByOrderNo(event.orderNo());
    }
}
