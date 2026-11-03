package com.preferredmall.user.controller;

import com.preferredmall.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.preferredmall.user.dto.LoginDTO;
import com.preferredmall.user.dto.RegisterDTO;
import com.preferredmall.user.service.SysLoginService;
import com.preferredmall.user.vo.LoginVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 认证接口：登录（账号密码 / 微信 js_code）
 */
@Tag(name = "注册登录")
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class AuthController {

    // 注入登录服务
    private final SysLoginService loginService;

    /**
     * 账号密码注册（买家），注册成功直接返回 token
     */
    @Operation(summary = "用户注册")
    @PostMapping("/register")
    public Result<LoginVO> register(@Valid @RequestBody RegisterDTO dto) {
        return Result.success(loginService.register(dto));
    }

    /**
     * 统一登录入口：按 LoginDTO.type 分流到密码/微信
     */
    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public Result<LoginVO> login(@Valid @RequestBody LoginDTO dto) {
        LoginVO vo;
        if ("password".equalsIgnoreCase(dto.getType())) {
            vo = loginService.loginByPassword(dto);
        } else if ("wechat".equalsIgnoreCase(dto.getType())) {
            vo = loginService.loginByWechat(dto.getCode(), dto.getNickname(), dto.getAvatar());
        } else {
            return Result.fail(400, "不支持的登录方式: " + dto.getType());
        }
        return Result.success(vo);
    }
}
