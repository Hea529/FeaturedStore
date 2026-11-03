package com.preferredmall.order.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单主表：下单时快照地址、商品、券、物流信息，保证历史订单不受商品改价影响
 * 状态：1 待支付 / 2 已支付 / 3 已发货 / 4 已完成 / 5 已取消
 */
@Data
@TableName("`order`")
public class Order implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 业务订单号（雪花 ID 生成，唯一） */
    @TableField("order_no")
    private String orderNo;

    @TableField("user_id")
    private Long userId;

    /** 快照：地址 */
    @TableField("address_id")
    private Long addressId;
    private String receiver;
    private String phone;
    @TableField("province_name")
    private String provinceName;
    @TableField("city_name")
    private String cityName;
    @TableField("district_name")
    private String districtName;
    @TableField("detail_address")
    private String detailAddress;

    /** 快照：金额拆分 */
    @TableField("total_amount")
    private BigDecimal totalAmount;
    @TableField("discount_amount")
    private BigDecimal discountAmount;
    @TableField("freight_amount")
    private BigDecimal freightAmount;
    @TableField("pay_amount")
    private BigDecimal payAmount;

    /** 支付方式：1 微信小程序 / 2 余额 */
    @TableField("pay_type")
    private Integer payType;

    /** 状态：1 待支付 / 2 已支付 / 3 已发货 / 4 已完成 / 5 已取消 / 6 售后 */
    private Integer status;

    /** 物流信息（下单后回填） */
    @TableField("logistics_company")
    private String logisticsCompany;
    @TableField("logistics_no")
    private String logisticsNo;

    /** 各阶段时间戳 */
    @TableField("pay_time")
    private LocalDateTime payTime;
    @TableField("ship_time")
    private LocalDateTime shipTime;
    @TableField("finish_time")
    private LocalDateTime finishTime;
    @TableField("cancel_time")
    private LocalDateTime cancelTime;

    @TableField("remark")
    private String remark;

    @TableLogic
    @TableField("is_deleted")
    private Integer isDeleted;

    @TableField(fill = com.baomidou.mybatisplus.annotation.FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = com.baomidou.mybatisplus.annotation.FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
