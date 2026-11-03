package com.preferredmall.infra.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * JWT 配置：由 mall-security 消费，mall-infra 只做绑定
 */
@Data
@ConfigurationProperties(prefix = "mall.jwt")
public class JwtProperties {

    /** access token 密钥 (HMAC-SHA) */
    private String accessSecret = "preferredmall-access-secret-change-me-32ch";
    /** refresh token 密钥 */
    private String refreshSecret = "preferredmall-refresh-secret-change-me-32ch";
    /** access token 过期时间（秒），默认 2 小时 */
    private long accessExpireSeconds = 7200;
    /** refresh token 过期时间（秒），默认 7 天 */
    private long refreshExpireSeconds = 604800;
    /** Token 前缀 */
    private String headerPrefix = "Bearer ";
    /** 请求头名 */
    private String headerName = "Authorization";
}
