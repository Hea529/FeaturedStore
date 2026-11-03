package com.preferredmall.infra.mq;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.preferredmall.infra.mapper.MqConsumerFailedMsgMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MqConsumerFailedMsgServiceImpl implements MqConsumerFailedMsgService {

    private final MqConsumerFailedMsgMapper mapper;

    @Override
    public void recordFailed(String msgId, String bizId, String topic, String tag, String body,
                             String consumerClass, String errorMsg) {
        MqConsumerFailedMsg m = new MqConsumerFailedMsg();
        m.setMsgId(msgId);
        m.setBizId(bizId);
        m.setTopic(topic);
        m.setTag(tag);
        m.setBody(body);
        m.setConsumerClass(consumerClass);
        m.setErrorMsg(errorMsg == null ? null : errorMsg.length() > 2000 ? errorMsg.substring(0, 2000) : errorMsg);
        m.setStatus(0);
        m.setRetryCount(0);
        mapper.insert(m);
        log.error("[MQ] 消费失败已落库, msgId={} bizId={} topic={}", msgId, bizId, topic);
    }

    @Override
    public void markHandled(Long id) {
        mapper.update(null, new LambdaUpdateWrapper<MqConsumerFailedMsg>()
                .eq(MqConsumerFailedMsg::getId, id)
                .set(MqConsumerFailedMsg::getStatus, 1));
    }

    @Override
    public void markIgnored(Long id) {
        mapper.update(null, new LambdaUpdateWrapper<MqConsumerFailedMsg>()
                .eq(MqConsumerFailedMsg::getId, id)
                .set(MqConsumerFailedMsg::getStatus, 2));
    }

    @Override
    public void incrRetry(Long id) {
        mapper.update(null, new LambdaUpdateWrapper<MqConsumerFailedMsg>()
                .eq(MqConsumerFailedMsg::getId, id)
                .setSql("retry_count = retry_count + 1"));
    }
}
