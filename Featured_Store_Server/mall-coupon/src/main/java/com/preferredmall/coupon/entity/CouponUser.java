package com.preferredmall.coupon.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户持券：领券后固化个人生效/过期时间，use_status 流转
 */
@Data
@TableName("coupon_user")
public class CouponUser implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("user_id")
    private Long userId;

    @TableField("coupon_id")
    private Long couponId;

    @TableField("valid_start")
    private LocalDateTime validStart;

    @TableField("valid_end")
    private LocalDateTime validEnd;

    /** 0 未开始 / 1 未用 / 2 已用 / 3 已过期 / 4 已回滚 */
    @TableField("use_status")
    private Integer useStatus;

    @TableField(fill = com.baomidou.mybatisplus.annotation.FieldFill.INSERT)
    private LocalDateTime receiveTime;

    /** 核销时间 */
    @TableField("use_time")
    private LocalDateTime useTime;
}
