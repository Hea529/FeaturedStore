package com.preferredmall.coupon.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 优惠券互斥配置组：同组券不可叠加使用
 */
@Data
@TableName("coupon_mutex_group")
public class CouponMutexGroup implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("group_code")
    private String groupCode;

    @TableField("group_name")
    private String groupName;

    private String remark;
}
