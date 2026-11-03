package com.preferredmall.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 账号密码注册（买家）
 */
@Data
public class RegisterDTO {

    @NotBlank(message = "用户名必填")
    @Size(min = 3, max = 32, message = "用户名长度 3-32")
    private String username;

    @NotBlank(message = "密码必填")
    @Size(min = 6, max = 64, message = "密码长度 6-64")
    private String password;

    /** 昵称（可选，默认用用户名） */
    private String nickname;
}
