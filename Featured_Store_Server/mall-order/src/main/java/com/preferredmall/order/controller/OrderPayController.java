package com.preferredmall.order.controller;

import com.preferredmall.common.context.BaseContext;
import com.preferredmall.common.exception.BusinessException;
import com.preferredmall.common.result.Result;
import com.preferredmall.common.result.ResultCode;
import com.preferredmall.order.entity.Order;
import com.preferredmall.order.enums.OrderStatusEnum;
import com.preferredmall.order.service.OrderApplicationService;
import com.preferredmall.payment.service.PayService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * C 端支付入口：预下单。金额以服务端订单为准，不信任客户端传参。
 * （支付回调在 mall-payment 的 PayNotifyController）
 */
@Tag(name = "订单支付")
@RestController
@RequestMapping("/api/pay")
@RequiredArgsConstructor
public class OrderPayController {

    private final OrderApplicationService orderService;
    private final PayService payService;

    @Operation(summary = "发起支付预下单")
    @PostMapping("/prepay/{orderNo}")
    public Result<String> prepay(@PathVariable String orderNo) {
        Long uid = BaseContext.currentUserId();
        if (uid == null) throw new BusinessException(ResultCode.UNAUTHORIZED);
        Order order = orderService.getByOrderNo(orderNo);
        if (!order.getUserId().equals(uid)) {
            throw new BusinessException(ResultCode.FORBIDDEN, "无权支付该订单");
        }
        if (order.getStatus() != OrderStatusEnum.PENDING_PAYMENT.getCode()) {
            throw new BusinessException(ResultCode.ORDER_STATUS_ERROR, "订单当前状态不可支付");
        }
        return Result.success(payService.createPrepay(order.getOrderNo(), order.getPayAmount(),
                "优选商城订单-" + order.getOrderNo()));
    }
}
