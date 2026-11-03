package com.preferredmall.coupon.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 券的适用范围明细：限定商品或分类
 */
@Data
@TableName("coupon_scope_detail")
public class CouponScopeDetail implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("coupon_id")
    private Long couponId;

    /** 1 指定分类 / 2 指定商品 */
    @TableField("scope_type")
    private Integer scopeType;

    /** category_id 或 product_id */
    @TableField("target_id")
    private Long targetId;
}
