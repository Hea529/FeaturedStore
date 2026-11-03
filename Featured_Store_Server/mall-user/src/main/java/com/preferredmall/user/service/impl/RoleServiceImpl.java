package com.preferredmall.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.preferredmall.user.entity.SysPermission;
import com.preferredmall.user.entity.SysRole;
import com.preferredmall.user.entity.SysRolePermission;
import com.preferredmall.user.entity.SysUserRole;
import com.preferredmall.user.mapper.SysPermissionMapper;
import com.preferredmall.user.mapper.SysRoleMapper;
import com.preferredmall.user.mapper.SysRolePermissionMapper;
import com.preferredmall.user.mapper.SysUserRoleMapper;
import com.preferredmall.user.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final SysUserRoleMapper userRoleMapper;
    private final SysRolePermissionMapper rolePermissionMapper;
    private final SysRoleMapper roleMapper;
    private final SysPermissionMapper permissionMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignRoles(Long userId, List<Long> roleIds) {
        userRoleMapper.delete(new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId, userId));
        if (roleIds != null && !roleIds.isEmpty()) {
            for (Long roleId : roleIds) {
                SysUserRole ur = new SysUserRole();
                ur.setUserId(userId);
                ur.setRoleId(roleId);
                userRoleMapper.insert(ur);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignPermissions(Long roleId, List<Long> permIds) {
        rolePermissionMapper.delete(new LambdaQueryWrapper<SysRolePermission>().eq(SysRolePermission::getRoleId, roleId));
        if (permIds != null && !permIds.isEmpty()) {
            for (Long permId : permIds) {
                SysRolePermission rp = new SysRolePermission();
                rp.setRoleId(roleId);
                rp.setPermId(permId);
                rolePermissionMapper.insert(rp);
            }
        }
    }

    @Override
    public List<SysRole> listUserRoles(Long userId) {
        List<SysRole> roles = roleMapper.selectByUserId(userId);
        return roles == null ? Collections.emptyList() : roles;
    }

    @Override
    public List<SysPermission> listUserPermissions(Long userId) {
        List<SysPermission> perms = permissionMapper.selectAllByUserId(userId);
        return perms == null ? Collections.emptyList() : perms;
    }

    @Override
    public List<String> listPermCodes(Long userId) {
        List<String> codes = permissionMapper.selectPermCodesByUserId(userId);
        return codes == null ? new ArrayList<>() : codes;
    }
}
