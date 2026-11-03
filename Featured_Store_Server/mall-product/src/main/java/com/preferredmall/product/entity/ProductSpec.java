package com.preferredmall.product.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 商品规格 SKU：一个 SPU 可多个规格，各自独立价格和库存
 */
@Data
@TableName("product_spec")
public class ProductSpec implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("product_id")
    private Long productId;

    /** 规格文字，如 "白色 / L 码" */
    @TableField("spec_text")
    private String specText;

    private BigDecimal price;

    @TableField("enterprise_price")
    private BigDecimal enterprisePrice;

    private Integer stock;
}
