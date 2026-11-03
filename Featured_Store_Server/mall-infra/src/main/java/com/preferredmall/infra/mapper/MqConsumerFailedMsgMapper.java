package com.preferredmall.infra.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.preferredmall.infra.mq.MqConsumerFailedMsg;
import org.apache.ibatis.annotations.Mapper;

/**
 * MQ 消费失败消息 Mapper
 */
@Mapper
public interface MqConsumerFailedMsgMapper extends BaseMapper<MqConsumerFailedMsg> {
}
