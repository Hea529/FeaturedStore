package com.preferredmall.order.vo;

import com.preferredmall.order.entity.Order;
import com.preferredmall.order.entity.OrderItem;
import lombok.Data;

import java.util.List;

@Data
public class OrderDetailVO {

    private Order order;
    private List<OrderItem> items;
}
