package com.preferredmall.payment.controller;

import com.preferredmall.common.result.Result;
import com.preferredmall.payment.service.PayService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

/**
 * 支付回调（匿名白名单 /api/pay/notify/**，无需登录）。
 * TODO: 接入微信支付后这里必须做验签，当前为 stub 直接按参数入账。
 */
@Tag(name = "支付回调")
@RestController
@RequestMapping("/api/pay/notify")
@RequiredArgsConstructor
public class PayNotifyController {

    private final PayService payService;

    @Operation(summary = "微信支付结果回调")
    @PostMapping("/wechat")
    public Result<Void> wechat(@RequestParam String transactionId,
                               @RequestParam String orderNo,
                               @RequestParam BigDecimal amount,
                               @RequestBody(required = false) String raw) {
        payService.handlePaymentSuccess(transactionId, orderNo, amount, raw);
        return Result.success();
    }
}
