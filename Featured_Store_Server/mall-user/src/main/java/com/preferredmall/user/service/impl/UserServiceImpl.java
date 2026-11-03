package com.preferredmall.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.preferredmall.common.exception.BusinessException;
import com.preferredmall.common.result.ResultCode;
import com.preferredmall.user.entity.SysUser;
import com.preferredmall.user.mapper.SysUserMapper;
import com.preferredmall.user.service.UserService;
import com.preferredmall.user.vo.UserDetailVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final SysUserMapper userMapper;

    @Override
    public SysUser getById(Long id) {
        SysUser u = userMapper.selectById(id);
        if (u == null) {
            throw new BusinessException(ResultCode.USER_NOT_EXIST);
        }
        return u;
    }

    @Override
    public SysUser getByUsername(String username) {
        SysUser u = userMapper.selectByUsername(username);
        if (u == null) {
            throw new BusinessException(ResultCode.USER_NOT_EXIST);
        }
        return u;
    }

    @Override
    public SysUser getByOpenid(String openid) {
        SysUser u = userMapper.selectByOpenid(openid);
        if (u == null) {
            throw new BusinessException(ResultCode.USER_NOT_EXIST);
        }
        return u;
    }

    @Override
    public UserDetailVO getDetail(Long userId) {
        SysUser u = getById(userId);
        UserDetailVO vo = new UserDetailVO();
        vo.setUserId(u.getId());
        vo.setUserType(u.getUserType());
        vo.setUsername(u.getUsername());
        vo.setNickname(u.getNickname());
        vo.setAvatar(u.getAvatar());
        vo.setPhone(u.getPhone());
        vo.setShopAuthStatus(u.getShopAuthStatus());
        return vo;
    }

    @Override
    public List<SysUser> listByIds(List<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return Collections.emptyList();
        }
        return userMapper.selectBatchIds(userIds);
    }

    @Override
    public boolean isEnterpriseAuthenticated(Long userId) {
        SysUser u = userMapper.selectById(userId);
        return u != null && u.getShopAuthStatus() != null && u.getShopAuthStatus() == 1;
    }
}
