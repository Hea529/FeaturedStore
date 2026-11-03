package com.preferredmall.user.controller;

import com.preferredmall.common.context.BaseContext;
import com.preferredmall.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.preferredmall.user.service.UserService;
import com.preferredmall.user.vo.UserDetailVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户资料接口（当前登录用户）
 */
@Tag(name = "用户中心")
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "获取当前用户信息")
    @GetMapping("/me")
    public Result<UserDetailVO> me() {
        Long uid = BaseContext.currentUserId();
        if (uid == null) {
            return Result.fail(401, "未登录");
        }
        return Result.success(userService.getDetail(uid));
    }
}
