package com.preferredmall.product.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商品 SPU 主表
 */
@Data
@TableName("product")
public class Product implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;
    /** 副标题 / 卖点 */
    private String subtitle;
    private String coverImage;
    /** 详细介绍 HTML */
    private String detail;

    @TableField("category_id")
    private Long categoryId;

    /** 基准价（展示用，实际以 ProductSpec.price 为准） */
    private BigDecimal price;

    /** 企业团购价 */
    @TableField("enterprise_price")
    private BigDecimal enterprisePrice;

    /** 所有规格的库存之和（冗余聚合，下单扣减必须事务维护） */
    private Integer stock;

    /** 销量 */
    @TableField("sales_count")
    private Integer salesCount;

    /** 浏览量 */
    @TableField("view_count")
    private Integer viewCount;

    /** 状态：1 上架 / 0 下架 */
    private Integer status;

    @TableField(fill = com.baomidou.mybatisplus.annotation.FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = com.baomidou.mybatisplus.annotation.FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
