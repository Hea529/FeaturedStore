package com.preferredmall.payment.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 支付流水表：原库缺失，需建表 DDL 见 SQL 初始化脚本
 */
@Data
@TableName("payment_record")
public class PaymentRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("order_no")
    private String orderNo;

    @TableField("transaction_id")
    private String transactionId;

    /** 1 微信小程序 / 2 余额 */
    @TableField("pay_type")
    private Integer payType;

    private BigDecimal amount;

    /** 0 待支付 / 1 支付成功 / 2 失败 / 3 已退款 */
    private Integer status;

    /** 原始回调报文（JSON） */
    @TableField("notify_raw")
    private String notifyRaw;

    private LocalDateTime payTime;
    @TableField("create_time")
    private LocalDateTime createTime;
}
