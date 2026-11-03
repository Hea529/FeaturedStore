package com.preferredmall.coupon.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 优惠券模板（活动）
 * coupon_type: 1 满减 / 2 折扣 / 3 无门槛 / 4 单品
 * valid_mode: 1 固定起止时间 / 2 领取后 N 天内有效
 * use_scope: 1 全店 / 2 分类 / 3 指定商品
 */
@Data
@TableName("coupon")
public class Coupon implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("coupon_no")
    private String couponNo;

    private String title;
    private String subtitle;
    private String description;

    /** 1 满减 / 2 折扣 / 3 无门槛 / 4 单品 */
    @TableField("coupon_type")
    private Integer couponType;

    /** 满减门槛 */
    @TableField("min_amount")
    private BigDecimal minAmount;

    /** 满减金额（coupon_type=1） */
    @TableField("face_value")
    private BigDecimal faceValue;

    /** 折扣率（coupon_type=2，如 0.8） */
    @TableField("discount_rate")
    private BigDecimal discountRate;

    /** 发行量 */
    @TableField("total_quota")
    private Integer totalQuota;
    /** 已领（冗余，需与 total_quota 并发维护） */
    @TableField("receive_quota")
    private Integer receiveQuota;
    /** 已用（冗余，需并发维护） */
    @TableField("used_quota")
    private Integer usedQuota;

    /** 每人限领数量 */
    @TableField("per_limit")
    private Integer perLimit;

    /** 有效期模式 */
    @TableField("valid_mode")
    private Integer validMode;

    /** valid_mode=1 固定时间 */
    @TableField("valid_start")
    private LocalDateTime validStart;
    @TableField("valid_end")
    private LocalDateTime validEnd;

    /** valid_mode=2 领取后 N 天 */
    @TableField("valid_days")
    private Integer validDays;

    /** 适用范围 */
    @TableField("use_scope")
    private Integer useScope;

    /** 互斥组 id（同组券不可叠加） */
    @TableField("mutex_group_id")
    private Long mutexGroupId;

    /** 状态：0 未上架 / 1 已上架 / 2 已下架 */
    private Integer status;

    @TableField(fill = com.baomidou.mybatisplus.annotation.FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = com.baomidou.mybatisplus.annotation.FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
