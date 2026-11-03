package com.preferredmall.user.service;

import com.preferredmall.user.entity.SysUser;
import com.preferredmall.user.vo.UserDetailVO;

import java.util.List;

/**
 * 用户资料服务（对外：供其他模块注入获取用户信息）
 */
public interface UserService {

    SysUser getById(Long id);

    SysUser getByUsername(String username);

    SysUser getByOpenid(String openid);

    UserDetailVO getDetail(Long userId);

    /**
     * 批量查用户（供 IM 渲染会话列表昵称头像）
     */
    List<SysUser> listByIds(List<Long> userIds);

    /**
     * 判断用户是否已认证企业
     */
    boolean isEnterpriseAuthenticated(Long userId);
}
