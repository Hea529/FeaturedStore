package com.preferredmall.security;

import com.preferredmall.infra.property.JwtProperties;
import com.preferredmall.security.token.JwtToken;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWT 签发与解析。使用 jjwt 0.12.x（新 API）。
 */
@Component
public class JwtUtils {

    private final JwtProperties props;
    private final SecretKey accessKey;

    public JwtUtils(JwtProperties props) {
        this.props = props;
        this.accessKey = Keys.hmacShaKeyFor(props.getAccessSecret().getBytes(StandardCharsets.UTF_8));
    }

    public String generate(Long userId, Integer userType, String username) {
        long now = System.currentTimeMillis() / 1000;
        Date exp = new Date((now + props.getAccessExpireSeconds()) * 1000);

        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("ut", userType)
                .claim("un", username == null ? "" : username)
                .issuedAt(new Date(now * 1000))
                .expiration(exp)
                .signWith(accessKey)
                .compact();
    }

    /**
     * 解析并校验；签名错误、过期、结构异常都抛 JwtException
     */
    public JwtToken parse(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(accessKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return JwtToken.builder()
                .userId(Long.valueOf(claims.getSubject()))
                .userType(((Number) claims.get("ut")).intValue())
                .username((String) claims.get("un"))
                .iat(claims.getIssuedAt().getTime() / 1000)
                .exp(claims.getExpiration().getTime() / 1000)
                .build();
    }

    public boolean validate(String token) {
        try {
            parse(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public JwtProperties properties() {
        return props;
    }
}
