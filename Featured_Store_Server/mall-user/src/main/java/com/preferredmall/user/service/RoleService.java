package com.preferredmall.user.service;

import com.preferredmall.user.entity.SysRole;
import com.preferredmall.user.entity.SysPermission;

import java.util.List;

/**
 * 角色权限服务（管理端）
 */
public interface RoleService {

    /**
     * 给用户分配角色
     */
    void assignRoles(Long userId, List<Long> roleIds);

    /**
     * 给角色分配权限
     */
    void assignPermissions(Long roleId, List<Long> permIds);

    /**
     * 查用户全部角色（含禁用）
     */
    List<SysRole> listUserRoles(Long userId);

    /**
     * 查用户全部权限（含菜单）
     */
    List<SysPermission> listUserPermissions(Long userId);

    /**
     * 查用户接口权限码（perm_type=2），用于方法注解鉴权
     */
    List<String> listPermCodes(Long userId);
}
