package com.preferredmall.user.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.preferredmall.common.result.CommonStatus;


import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 系统用户表：管理员与买家共用一张表，通过 user_type 区分
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("sys_user")
public class SysUser implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 登录账号（管理员用） */
    private String username;

    /** BCrypt 加密密码（管理员用） */
    private String password;

    /** 微信 openid（小程序登录用，唯一） */
    private String openid;

    private String phone;
    private String nickname;
    private String avatar;

    /** 1-系统管理员 2-普通管理员 3-普通买家 */
    @TableField("user_type")
    private Integer userType;

    /** 是否启用 */
    @TableField("is_enable")
    private CommonStatus isEnable;

    /** 企业认证状态：0 未认证 / 1 已认证 */
    @TableField("shop_auth_status")
    private Integer shopAuthStatus;

    @TableField("first_login_time")
    private LocalDateTime firstLoginTime;
    @TableField("last_login_time")
    private LocalDateTime lastLoginTime;
    @TableField(fill = com.baomidou.mybatisplus.annotation.FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = com.baomidou.mybatisplus.annotation.FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
