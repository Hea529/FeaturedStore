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
 * 订单-优惠券核销关联：记录抵扣金额与回滚时间
 * rel_status: 1 锁定（下单占用） / 2 已核销（支付成功） / 3 已回滚（订单取消）
 */
@Data
@TableName("coupon_order_rel")
public class CouponOrderRel implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("order_id")
    private Long orderId;

    @TableField("order_no")
    private String orderNo;

    @TableField("order_item_id")
    private Long orderItemId;

    /** 对应 coupon_user.id */
    @TableField("user_coupon_id")
    private Long userCouponId;

    @TableField("activity_id")
    private Long activityId;

    /** 本次抵扣金额 */
    @TableField("discount_amount")
    private BigDecimal discountAmount;

    @TableField("rel_status")
    private Integer relStatus;

    @TableField("use_time")
    private LocalDateTime useTime;

    @TableField("rollback_time")
    private LocalDateTime rollbackTime;
}
