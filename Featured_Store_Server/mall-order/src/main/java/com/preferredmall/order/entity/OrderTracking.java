package com.preferredmall.order.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 物流轨迹：一个运单号对应多条节点。
 * 原库对 logistics_no 有唯一约束，需要改成普通索引以支持多节点（迁移时要先删旧约束再重建）。
 */
@Data
@TableName("order_tracking")
public class OrderTracking implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("order_id")
    private Long orderId;

    /** 注意：原库唯一约束要去掉，改成普通索引 */
    @TableField("logistics_no")
    private String logisticsNo;

    @TableField("logistics_company")
    private String logisticsCompany;

    /** 揽收/运输/派送/签收 等节点 */
    @TableField("logistics_status")
    private String logisticsStatus;

    /** 节点地点 */
    private String location;

    /** 节点描述 */
    private String description;

    private LocalDateTime time;
}
