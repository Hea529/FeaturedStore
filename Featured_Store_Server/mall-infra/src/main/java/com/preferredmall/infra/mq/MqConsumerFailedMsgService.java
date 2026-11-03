package com.preferredmall.infra.mq;

public interface MqConsumerFailedMsgService {

    /**
     * 记录消费失败，状态初始化为 0 待处理
     */
    void recordFailed(String msgId, String bizId, String topic, String tag, String body,
                     String consumerClass, String errorMsg);

    /**
     * 标记为已处理
     */
    void markHandled(Long id);

    /**
     * 标记为无需处理
     */
    void markIgnored(Long id);

    /**
     * 自增重试次数
     */
    void incrRetry(Long id);
}
