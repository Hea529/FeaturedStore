package com.preferredmall.security.token;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * JWT 载荷：业务侧实际关心的字段
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JwtToken {
    private Long userId;
    /** 1 系统管理员 / 2 普通管理员 / 3 普通买家 */
    private Integer userType;
    private String username;
    /** 签发时间（秒） */
    private long iat;
    /** 过期时间（秒） */
    private long exp;
}
