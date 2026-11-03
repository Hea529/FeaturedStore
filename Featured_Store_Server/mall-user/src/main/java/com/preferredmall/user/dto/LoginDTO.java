package com.preferredmall.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 登录请求
 */
@Data
public class LoginDTO {

    /** 登录方式：password / wechat */
    @NotBlank(message = "登录方式必填")
    private String type;

    /** type=password 时的账号 */
    private String username;

    /** type=password 时的密码（前端明文或 Base64，后端 BCrypt 匹配） */
    private String password;

    /** type=wechat 时的 js_code（wx.login() 下发，服务端用它换 openid，不信任客户端直传 openid） */
    private String code;

    /** type=wechat 时的昵称（可选，首次注册用） */
    private String nickname;

    /** type=wechat 时的头像（可选，首次注册用） */
    private String avatar;
}
