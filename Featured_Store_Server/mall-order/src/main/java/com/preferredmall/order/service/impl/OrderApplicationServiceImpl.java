package com.preferredmall.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.preferredmall.common.event.PaidOrderCancelledEvent;
import com.preferredmall.common.exception.BusinessException;
import com.preferredmall.common.result.ResultCode;
import com.preferredmall.common.util.SnowflakeIdGenerator;
import com.preferredmall.coupon.service.CouponUseService;
import com.preferredmall.order.dto.OrderCreateDTO;
import com.preferredmall.order.entity.*;
import com.preferredmall.order.enums.OrderStatusEnum;
import com.preferredmall.order.mapper.*;
import com.preferredmall.order.service.OrderApplicationService;
import com.preferredmall.product.entity.Product;
import com.preferredmall.product.entity.ProductSpec;
import com.preferredmall.product.service.ProductService;
import com.preferredmall.user.entity.Address;
import com.preferredmall.user.service.AddressService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 下单编排服务：跨 mall-user / mall-product / (可选) mall-coupon 共用事务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderApplicationServiceImpl implements OrderApplicationService {

    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final CartMapper cartMapper;

    private final ProductService productService;
    private final AddressService addressService;
    private final ApplicationEventPublisher eventPublisher;

    @Autowired(required = false)
    private CouponUseService couponUseService; // mall-coupon 依赖可选

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Order create(Long userId, OrderCreateDTO dto) {
        // 1. 取地址快照
        Address addr = addressService.getById(dto.getAddressId());
        if (addr == null || !addr.getUserId().equals(userId)) {
            throw new BusinessException(ResultCode.VALIDATE_FAILED, "收货地址不存在或无权使用");
        }

        // 2. 组装订单项
        List<OrderItem> items = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;
        if (dto.getCartIds() != null && !dto.getCartIds().isEmpty()) {
            List<Cart> carts = cartMapper.selectBatchIds(dto.getCartIds());
            if (carts.size() != dto.getCartIds().size()) {
                throw new BusinessException(ResultCode.VALIDATE_FAILED, "部分购物车项不存在，请刷新后重试");
            }
            for (Cart cart : carts) {
                if (!cart.getUserId().equals(userId)) {
                    throw new BusinessException(ResultCode.VALIDATE_FAILED, "购物车项不属于当前用户");
                }
                items.add(buildItem(cart.getProductId(), cart.getSpecId(), cart.getQuantity()));
                totalAmount = totalAmount.add(items.get(items.size() - 1).getSubtotal());
            }
        } else if (dto.getItems() != null && !dto.getItems().isEmpty()) {
            for (OrderCreateDTO.OrderItemDTO it : dto.getItems()) {
                ProductSpec spec = productService.getSpecById(it.getSpecId());
                items.add(buildItem(spec.getProductId(), spec.getId(), it.getQuantity()));
                totalAmount = totalAmount.add(items.get(items.size() - 1).getSubtotal());
            }
        } else {
            throw new BusinessException(ResultCode.VALIDATE_FAILED, "未提供商品项");
        }

        // 3. 扣库存（同一事务）
        for (OrderItem it : items) {
            productService.confirmStock(it.getSpecId(), it.getQuantity());
        }

        // 先生成订单号：锁券记录需要按 orderNo 关联，供支付成功后核销 / 取消时回滚
        String orderNo = String.valueOf(SnowflakeIdGenerator.nextId());

        // 4. 优惠券试算 & 锁券（可选）
        BigDecimal discount = BigDecimal.ZERO;
        if (couponUseService != null && dto.getUserCouponId() != null) {
            discount = couponUseService.calcAndLock(dto.getUserCouponId(), userId, totalAmount,
                    items.stream().map(OrderItem::getProductId).toList(), orderNo);
        }

        // 5. 写 order（快照字段都从地址复制）
        BigDecimal payAmount = totalAmount.subtract(discount).max(BigDecimal.ZERO);
        Order order = new Order();
        order.setOrderNo(orderNo);
        order.setUserId(userId);
        order.setAddressId(addr.getId());
        order.setReceiver(addr.getReceiver());
        order.setPhone(addr.getPhone());
        order.setProvinceName(addr.getProvinceName());
        order.setCityName(addr.getCityName());
        order.setDistrictName(addr.getDistrictName());
        order.setDetailAddress(addr.getDetailAddress());
        order.setTotalAmount(totalAmount);
        order.setDiscountAmount(discount);
        order.setFreightAmount(BigDecimal.ZERO);
        order.setPayAmount(payAmount);
        order.setPayType(dto.getPayType() == null ? 1 : dto.getPayType());
        order.setStatus(OrderStatusEnum.PENDING_PAYMENT.getCode());
        order.setRemark(dto.getRemark());
        order.setIsDeleted(0);
        orderMapper.insert(order);

        // 6. 写 order_item
        for (OrderItem it : items) {
            it.setOrderId(order.getId());
            orderItemMapper.insert(it);
        }

        // 7. 清购物车选中项
        if (dto.getCartIds() != null && !dto.getCartIds().isEmpty()) {
            cartMapper.deleteBatchIds(dto.getCartIds());
        }
        return order;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void paySuccess(Long orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) throw new BusinessException(ResultCode.ORDER_NOT_EXIST);
        if (order.getStatus() != OrderStatusEnum.PENDING_PAYMENT.getCode()) {
            throw new BusinessException(ResultCode.ORDER_STATUS_ERROR, "当前状态不允许支付成功");
        }
        orderMapper.update(null, new LambdaUpdateWrapper<Order>()
                .eq(Order::getId, orderId)
                .set(Order::getStatus, OrderStatusEnum.PAID.getCode())
                .set(Order::getPayTime, LocalDateTime.now()));
        // 核销锁定的优惠券
        if (couponUseService != null) {
            couponUseService.confirmUse(order.getOrderNo());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void paySuccessByOrderNo(String orderNo) {
        Order order = getByOrderNo(orderNo);
        if (order.getStatus() == OrderStatusEnum.PAID.getCode()) {
            log.info("[Order] 订单已是已支付状态，幂等跳过: orderNo={}", orderNo);
            return;
        }
        paySuccess(order.getId());
    }

    @Override
    public Order getByOrderNo(String orderNo) {
        Order order = orderMapper.selectOne(new LambdaQueryWrapper<Order>()
                .eq(Order::getOrderNo, orderNo).last("LIMIT 1"));
        if (order == null) throw new BusinessException(ResultCode.ORDER_NOT_EXIST);
        return order;
    }

    @Override
    public Order detail(Long userId, Long orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) throw new BusinessException(ResultCode.ORDER_NOT_EXIST);
        if (!order.getUserId().equals(userId)) {
            throw new BusinessException(ResultCode.FORBIDDEN, "无权查看该订单");
        }
        return order;
    }

    @Override
    public List<Order> listByUser(Long userId, Integer status) {
        LambdaQueryWrapper<Order> w = new LambdaQueryWrapper<Order>()
                .eq(Order::getUserId, userId)
                .orderByDesc(Order::getId);
        if (status != null) {
            w.eq(Order::getStatus, status);
        }
        return orderMapper.selectList(w);
    }

    @Override
    public void ship(Long orderId, String logisticsCompany, String logisticsNo) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) throw new BusinessException(ResultCode.ORDER_NOT_EXIST);
        if (order.getStatus() != OrderStatusEnum.PAID.getCode()) {
            throw new BusinessException(ResultCode.ORDER_STATUS_ERROR, "当前状态不允许发货");
        }
        orderMapper.update(null, new LambdaUpdateWrapper<Order>()
                .eq(Order::getId, orderId)
                .set(Order::getStatus, OrderStatusEnum.SHIPPED.getCode())
                .set(Order::getLogisticsCompany, logisticsCompany)
                .set(Order::getLogisticsNo, logisticsNo)
                .set(Order::getShipTime, LocalDateTime.now()));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void finish(Long orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) throw new BusinessException(ResultCode.ORDER_NOT_EXIST);
        if (order.getStatus() != OrderStatusEnum.SHIPPED.getCode()) {
            throw new BusinessException(ResultCode.ORDER_STATUS_ERROR, "当前状态不允许确认完成");
        }
        orderMapper.update(null, new LambdaUpdateWrapper<Order>()
                .eq(Order::getId, orderId)
                .set(Order::getStatus, OrderStatusEnum.COMPLETED.getCode())
                .set(Order::getFinishTime, LocalDateTime.now()));
        // 累积商品销量
        List<OrderItem> items = orderItemMapper.selectList(new LambdaQueryWrapper<OrderItem>()
                .eq(OrderItem::getOrderId, orderId));
        for (OrderItem it : items) {
            productService.incrSales(it.getProductId(), it.getQuantity());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancel(Long userId, Long orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) throw new BusinessException(ResultCode.ORDER_NOT_EXIST);
        if (!order.getUserId().equals(userId)) throw new BusinessException(403, "无权取消");
        boolean wasPaid = order.getStatus() == OrderStatusEnum.PAID.getCode();
        if (order.getStatus() != OrderStatusEnum.PENDING_PAYMENT.getCode() && !wasPaid) {
            throw new BusinessException(ResultCode.ORDER_STATUS_ERROR, "当前状态不允许取消");
        }
        // 释放库存
        List<OrderItem> items = orderItemMapper.selectList(new LambdaQueryWrapper<OrderItem>()
                .eq(OrderItem::getOrderId, orderId));
        for (OrderItem it : items) {
            productService.releaseStock(it.getSpecId(), it.getQuantity());
        }
        // 券回滚（可选）
        if (couponUseService != null) {
            couponUseService.rollbackByOrderNo(order.getOrderNo());
        }
        orderMapper.update(null, new LambdaUpdateWrapper<Order>()
                .eq(Order::getId, orderId)
                .set(Order::getStatus, OrderStatusEnum.CANCELLED.getCode())
                .set(Order::getCancelTime, LocalDateTime.now()));
        // 已支付订单：发布退款事件，由 mall-payment 执行退款，避免资金/库存账实不符
        if (wasPaid) {
            eventPublisher.publishEvent(new PaidOrderCancelledEvent(order.getOrderNo(), order.getPayAmount()));
        }
    }

    private OrderItem buildItem(Long productId, Long specId, Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new BusinessException(ResultCode.VALIDATE_FAILED, "商品数量必须大于 0");
        }
        ProductSpec spec = productService.getSpecById(specId);
        Product product = productService.getById(productId);
        if (!spec.getProductId().equals(productId)) {
            throw new BusinessException(ResultCode.VALIDATE_FAILED, "规格与商品不匹配");
        }
        if (product.getStatus() == null || product.getStatus() != 1) {
            throw new BusinessException(ResultCode.PRODUCT_OFFLINE, "商品「" + product.getName() + "」已下架");
        }
        OrderItem it = new OrderItem();
        it.setProductId(productId);
        it.setSpecId(specId);
        it.setProductName(product.getName());
        it.setCoverImage(product.getCoverImage());
        it.setSpecText(spec.getSpecText());
        it.setPrice(spec.getPrice());
        it.setQuantity(quantity);
        it.setSubtotal(spec.getPrice().multiply(BigDecimal.valueOf(quantity)));
        return it;
    }
}
