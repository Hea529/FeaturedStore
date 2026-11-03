package com.preferredmall.user.service;

import com.preferredmall.user.dto.LoginDTO;
import com.preferredmall.user.dto.RegisterDTO;
import com.preferredmall.user.entity.SysUser;
import com.preferredmall.user.vo.LoginVO;

/**
 * 登录服务：账号密码注册/登录、微信登录、签发 JWT
 */
public interface SysLoginService {

    /**
     * 账号密码注册（买家），注册成功直接签发 token
     */
    LoginVO register(RegisterDTO dto);

    /**
     * 账号密码登录
     */
    LoginVO loginByPassword(LoginDTO dto);

    /**
     * 微信登录：客户端提交 js_code，服务端换取 openid 后登录；没有则自动创建买家账号
     */
    LoginVO loginByWechat(String code, String nickname, String avatar);

    /**
     * 根据用户信息直接签发 token（内部服务调用，例如刷新 token）
     */
    String issueToken(SysUser user);
}
