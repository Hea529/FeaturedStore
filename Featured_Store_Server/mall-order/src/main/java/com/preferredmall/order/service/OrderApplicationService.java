package com.preferredmall.order.service;

import com.preferredmall.order.dto.OrderCreateDTO;
import com.preferredmall.order.entity.Order;

import java.util.List;

/**
 * 订单应用服务：下单编排 + 状态机推进（核心业务流程集中在这里）
 */
public interface OrderApplicationService {

    /**
     * 创建订单：事务内编排地址快照、库存扣减、券锁、写入 order + order_item
     */
    Order create(Long userId, OrderCreateDTO dto);

    /**
     * 支付成功回调：推进状态 1→2，回写 payTime，核销优惠券
     */
    void paySuccess(Long orderId);

    /**
     * 按订单号处理支付成功（事件入口）；已支付时幂等跳过
     */
    void paySuccessByOrderNo(String orderNo);

    Order getByOrderNo(String orderNo);

    /**
     * 用户订单详情（校验归属）
     */
    Order detail(Long userId, Long orderId);

    /**
     * 用户订单列表，status 为 null 查全部
     */
    List<Order> listByUser(Long userId, Integer status);

    /**
     * 发货：2→3
     */
    void ship(Long orderId, String logisticsCompany, String logisticsNo);

    /**
     * 完成：3→4，累积商品销量
     */
    void finish(Long orderId);

    /**
     * 取消：1→5 或 2→5，释放库存 + 回滚券；已支付订单发布退款事件
     */
    void cancel(Long userId, Long orderId);
}
