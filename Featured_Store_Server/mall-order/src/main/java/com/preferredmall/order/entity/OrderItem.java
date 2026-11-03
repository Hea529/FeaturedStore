package com.preferredmall.order.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 订单明细：下单时对商品做快照，后续商品改价/下架不影响历史订单
 */
@Data
@TableName("order_item")
public class OrderItem implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("order_id")
    private Long orderId;

    @TableField("product_id")
    private Long productId;
    @TableField("spec_id")
    private Long specId;

    /** 快照字段 */
    @TableField("product_name")
    private String productName;
    @TableField("cover_image")
    private String coverImage;
    @TableField("spec_text")
    private String specText;
    private BigDecimal price;
    private Integer quantity;
    private BigDecimal subtotal;
}
