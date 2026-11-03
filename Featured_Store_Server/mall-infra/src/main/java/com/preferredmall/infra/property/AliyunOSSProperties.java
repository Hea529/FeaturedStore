package com.preferredmall.infra.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 阿里云 OSS 配置
 */
@Data
@ConfigurationProperties(prefix = "mall.oss")
public class AliyunOSSProperties {

    private String endpoint;
    private String accessKeyId;
    private String accessKeySecret;
    private String bucketName;
    private String domain;
}
