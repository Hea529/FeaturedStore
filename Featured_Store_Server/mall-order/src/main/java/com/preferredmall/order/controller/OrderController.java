package com.preferredmall.order.controller;

import com.preferredmall.common.context.BaseContext;
import com.preferredmall.common.exception.BusinessException;
import com.preferredmall.common.result.Result;
import com.preferredmall.common.result.ResultCode;
import com.preferredmall.order.dto.OrderCreateDTO;
import com.preferredmall.order.entity.Order;
import com.preferredmall.order.mapper.OrderItemMapper;
import com.preferredmall.order.service.OrderApplicationService;
import com.preferredmall.order.vo.OrderDetailVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.preferredmall.order.entity.OrderItem;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * C 端：下单 / 取消 / 订单查询
 */
@Tag(name = "我的订单")
@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderApplicationService orderService;
    private final OrderItemMapper orderItemMapper;

    private Long uid() {
        Long u = BaseContext.currentUserId();
        if (u == null) throw new BusinessException(ResultCode.UNAUTHORIZED);
        return u;
    }

    @Operation(summary = "创建订单")
    @PostMapping
    public Result<Order> create(@RequestBody @Validated OrderCreateDTO dto) {
        return Result.success(orderService.create(uid(), dto));
    }

    @Operation(summary = "取消订单")
    @PostMapping("/{id}/cancel")
    public Result<Void> cancel(@PathVariable Long id) {
        orderService.cancel(uid(), id);
        return Result.success();
    }

    @Operation(summary = "获取订单详情")
    @GetMapping("/{id}")
    public Result<OrderDetailVO> detail(@PathVariable Long id) {
        Order order = orderService.detail(uid(), id);
        List<OrderItem> items = orderItemMapper.selectList(new LambdaQueryWrapper<OrderItem>()
                .eq(OrderItem::getOrderId, order.getId()));
        OrderDetailVO vo = new OrderDetailVO();
        vo.setOrder(order);
        vo.setItems(items);
        return Result.success(vo);
    }

    @Operation(summary = "获取我的订单列表")
    @GetMapping("/list")
    public Result<List<Order>> list(@RequestParam(required = false) Integer status) {
        return Result.success(orderService.listByUser(uid(), status));
    }
}
