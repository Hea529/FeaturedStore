package com.preferredmall.order.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.preferredmall.common.result.Result;
import com.preferredmall.order.entity.Order;
import com.preferredmall.order.mapper.OrderMapper;
import com.preferredmall.order.service.OrderApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 管理端：订单列表 / 发货 / 完成（角色校验由 /admin/** 拦截器统一处理）
 */
@Tag(name = "订单管理（后台）")
@RestController
@RequestMapping("/admin/order")
@RequiredArgsConstructor
public class OrderAdminController {

    private final OrderApplicationService orderService;
    private final OrderMapper orderMapper;

    @Operation(summary = "获取订单列表")
    @GetMapping("/list")
    public Result<List<Order>> list(@RequestParam(required = false) Integer status) {
        LambdaQueryWrapper<Order> w = new LambdaQueryWrapper<Order>().orderByDesc(Order::getId);
        if (status != null) {
            w.eq(Order::getStatus, status);
        }
        return Result.success(orderMapper.selectList(w));
    }

    @Operation(summary = "订单发货")
    @PostMapping("/{id}/ship")
    public Result<Void> ship(@PathVariable Long id,
                             @RequestParam String logisticsCompany,
                             @RequestParam String logisticsNo) {
        orderService.ship(id, logisticsCompany, logisticsNo);
        return Result.success();
    }

    @Operation(summary = "完成订单")
    @PostMapping("/{id}/finish")
    public Result<Void> finish(@PathVariable Long id) {
        orderService.finish(id);
        return Result.success();
    }
}
