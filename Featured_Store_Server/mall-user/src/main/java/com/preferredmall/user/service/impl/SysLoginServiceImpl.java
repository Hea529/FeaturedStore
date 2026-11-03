package com.preferredmall.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.preferredmall.common.exception.BusinessException;
import com.preferredmall.common.result.CommonStatus;
import com.preferredmall.common.result.ResultCode;
import com.preferredmall.infra.property.JwtProperties;
import com.preferredmall.security.JwtUtils;
import com.preferredmall.user.dto.LoginDTO;
import com.preferredmall.user.dto.RegisterDTO;
import com.preferredmall.user.entity.SysUser;
import com.preferredmall.user.entity.SysUserRole;
import com.preferredmall.user.mapper.SysUserMapper;
import com.preferredmall.user.mapper.SysUserRoleMapper;
import com.preferredmall.user.service.SysLoginService;
import com.preferredmall.user.vo.LoginVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 登录服务实现：账号密码登录 + 微信登录 + JWT 签发
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SysLoginServiceImpl implements SysLoginService {

    private final SysUserMapper userMapper;
    private final SysUserRoleMapper userRoleMapper;
    private final JwtUtils jwtUtils;
    private final JwtProperties jwtProperties;
    private final WechatAuthClient wechatAuthClient;

    private static final BCryptPasswordEncoder PASSWORD_ENCODER = new BCryptPasswordEncoder();
    /** 买家角色（sys_role id=2 ROLE_USER） */
    private static final long BUYER_ROLE_ID = 2L;

    @Override
    @org.springframework.transaction.annotation.Transactional(rollbackFor = Exception.class)
    public LoginVO register(RegisterDTO dto) {
        if (userMapper.selectByUsername(dto.getUsername()) != null) {
            throw new BusinessException(ResultCode.VALIDATE_FAILED, "用户名已存在");
        }
        SysUser user = SysUser.builder()
                .username(dto.getUsername())
                .password(PASSWORD_ENCODER.encode(dto.getPassword()))
                .nickname(dto.getNickname() == null || dto.getNickname().isBlank() ? dto.getUsername() : dto.getNickname())
                .avatar("")
                .userType(3)
                .isEnable(CommonStatus.ACTIVE)
                .shopAuthStatus(0)
                .build();
        userMapper.insert(user);
        SysUserRole ur = new SysUserRole();
        ur.setUserId(user.getId());
        ur.setRoleId(BUYER_ROLE_ID);
        userRoleMapper.insert(ur);
        touchLastLogin(user.getId(), true);
        return buildLoginVO(user);
    }

    @Override
    public LoginVO loginByPassword(LoginDTO dto) {
        SysUser user = userMapper.selectByUsername(dto.getUsername());
        if (user == null) {
            throw new BusinessException(ResultCode.LOGIN_ERROR, "用户名或密码错误");
        }
        if (!CommonStatus.ACTIVE.equals(user.getIsEnable())) {
            throw new BusinessException(ResultCode.USER_DISABLED);
        }
        if (!PASSWORD_ENCODER.matches(dto.getPassword(), user.getPassword())) {
            throw new BusinessException(ResultCode.LOGIN_ERROR, "用户名或密码错误");
        }
        touchLastLogin(user.getId(), user.getFirstLoginTime() == null);
        return buildLoginVO(user);
    }

    @Override
    public LoginVO loginByWechat(String code, String nickname, String avatar) {
        if (code == null || code.isBlank()) {
            throw new BusinessException(ResultCode.VALIDATE_FAILED, "微信登录 code 必填");
        }
        // 服务端用 code 换 openid，绝不信任客户端直传的 openid
        String openid = wechatAuthClient.fetchOpenid(code);
        SysUser user = userMapper.selectByOpenid(openid);
        if (user == null) {
            // 首次登录自动注册买家账号
            user = SysUser.builder()
                    .openid(openid)
                    .nickname(nickname == null || nickname.isEmpty() ? "微信用户" : nickname)
                    .avatar(avatar)
                    .userType(3)
                    .isEnable(CommonStatus.ACTIVE)
                    .shopAuthStatus(0)
                    .build();
            userMapper.insert(user);
            touchLastLogin(user.getId(), true);
        } else {
            if (!CommonStatus.ACTIVE.equals(user.getIsEnable())) {
                throw new BusinessException(ResultCode.USER_DISABLED);
            }
            touchLastLogin(user.getId(), false);
        }
        return buildLoginVO(user);
    }

    @Override
    public String issueToken(SysUser user) {
        return jwtUtils.generate(user.getId(), user.getUserType(),
                user.getNickname() == null ? user.getUsername() : user.getNickname());
    }

    private LoginVO buildLoginVO(SysUser user) {
        String token = issueToken(user);

        LoginVO.UserSimpleVO u = new LoginVO.UserSimpleVO();
        u.setUserId(user.getId());
        u.setUserType(user.getUserType());
        u.setUsername(user.getUsername());
        u.setNickname(user.getNickname());
        u.setAvatar(user.getAvatar());
        u.setPhone(user.getPhone());

        LoginVO vo = new LoginVO();
        vo.setAccessToken(token);
        vo.setExpiresIn(jwtProperties.getAccessExpireSeconds());
        vo.setUser(u);
        return vo;
    }

    private void touchLastLogin(Long userId, boolean firstLogin) {
        LambdaUpdateWrapper<SysUser> w = new LambdaUpdateWrapper<>();
        w.eq(SysUser::getId, userId)
                .set(SysUser::getLastLoginTime, LocalDateTime.now());
        if (firstLogin) {
            w.set(SysUser::getFirstLoginTime, LocalDateTime.now());
        }
        userMapper.update(null, w);
    }
}
