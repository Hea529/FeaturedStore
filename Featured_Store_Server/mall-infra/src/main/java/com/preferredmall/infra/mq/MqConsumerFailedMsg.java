package com.preferredmall.infra.mq;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * RocketMQ 消费失败落库表
 */
@Data
@TableName("mq_consumer_failed_msg")
public class MqConsumerFailedMsg {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** MQ msgId */
    private String msgId;
    /** 业务 ID（订单号等） */
    private String bizId;
    private String topic;
    private String tag;
    /** 原始消息体（JSON） */
    private String body;
    /** 消费类名 */
    private String consumerClass;
    /** 最后一次异常堆栈 */
    private String errorMsg;
    /** 状态：0 待处理 / 1 已处理 / 2 无需处理 */
    private Integer status;
    /** 已重试次数 */
    private Integer retryCount;
    @TableField(fill = com.baomidou.mybatisplus.annotation.FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = com.baomidou.mybatisplus.annotation.FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
