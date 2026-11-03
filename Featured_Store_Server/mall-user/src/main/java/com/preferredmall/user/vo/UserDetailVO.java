package com.preferredmall.user.vo;

import lombok.Data;

/**
 * 用户详情 VO（C 端用：基本资料 + 是否已认证）
 */
@Data
public class UserDetailVO {

    private Long userId;
    private Integer userType;
    private String username;
    private String nickname;
    private String avatar;
    private String phone;
    /** 0 未认证 / 1 已认证 */
    private Integer shopAuthStatus;
}
