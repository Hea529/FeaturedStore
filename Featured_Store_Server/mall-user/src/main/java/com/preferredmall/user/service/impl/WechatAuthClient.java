package com.preferredmall.user.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.preferredmall.common.exception.BusinessException;
import com.preferredmall.common.result.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 微信登录凭证校验：用客户端提交的 js_code 在服务端换取 openid，
 * 绝不直接信任客户端传入的 openid（防伪造登录他人账号）。
 *
 * 未配置 mall.wechat.appid/secret 时使用开发桩：以 code 的 SHA-256 派生伪 openid。
 * 开发桩下 openid 无法被反推出 code，因此无法冒用他人账号；但 code 真实性未校验，
 * 仅限本地开发，生产环境必须配置 WECHAT_APPID / WECHAT_SECRET。
 */
@Slf4j
@Component
public class WechatAuthClient {

    private static final String JSCODE2SESSION_URL =
            "https://api.weixin.qq.com/sns/jscode2session?appid={appid}&secret={secret}&js_code={code}&grant_type=authorization_code";

    private final RestClient restClient = RestClient.create();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${mall.wechat.appid:}")
    private String appid;

    @Value("${mall.wechat.secret:}")
    private String secret;

    /**
     * 用 js_code 换取 openid
     *
     * @param code 小程序 wx.login() 下发的 js_code
     * @return 微信 openid（开发桩模式下为派生伪 openid）
     */
    public String fetchOpenid(String code) {
        if (appid == null || appid.isBlank() || secret == null || secret.isBlank()) {
            log.warn("[WechatAuth] mall.wechat.appid/secret 未配置，使用开发桩 code->openid，禁止生产环境使用");
            return "dev_" + sha256Hex(code).substring(0, 28);
        }
        try {
            String body = restClient.get()
                    .uri(JSCODE2SESSION_URL, appid, secret, code)
                    .retrieve()
                    .body(String.class);
            JsonNode node = objectMapper.readTree(body);
            String openid = node.path("openid").asText(null);
            if (openid == null || openid.isBlank()) {
                log.warn("[WechatAuth] jscode2session 失败: errcode={} errmsg={}",
                        node.path("errcode").asInt(), node.path("errmsg").asText());
                throw new BusinessException(ResultCode.LOGIN_ERROR, "微信登录凭证无效或已过期");
            }
            return openid;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("[WechatAuth] 调用微信 jscode2session 异常", e);
            throw new BusinessException(ResultCode.LOGIN_ERROR, "微信登录服务暂不可用");
        }
    }

    private static String sha256Hex(String text) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(text.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(digest.length * 2);
            for (byte b : digest) {
                sb.append(Character.forDigit((b >> 4) & 0xF, 16));
                sb.append(Character.forDigit(b & 0xF, 16));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}
