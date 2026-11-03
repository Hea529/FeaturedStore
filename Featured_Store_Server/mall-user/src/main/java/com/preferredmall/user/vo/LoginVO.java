package com.preferredmall.user.vo;

import lombok.Data;

/**
 * 登录成功返回：前端直接拿 token 存在 localStorage
 */
@Data
public class LoginVO {

    /** access token */
    private String accessToken;

    /** token 类型：Bearer */
    private String tokenType = "Bearer";

    /** access token 剩余有效期（秒） */
    private long expiresIn;

    /** 当前用户基本信息 */
    private UserSimpleVO user;

    @Data
    public static class UserSimpleVO {
        private Long userId;
        /** 1 系统管理员 2 普通管理员 3 普通买家 */
        private Integer userType;
        private String username;
        private String nickname;
        private String avatar;
        private String phone;
    }
}
