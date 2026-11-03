package com.preferredmall.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.preferredmall.user.entity.SysPermission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SysPermissionMapper extends BaseMapper<SysPermission> {

    /**
     * 查一个用户全部的接口权限（perm_type=2），用于方法注解鉴权
     */
    @Select("SELECT DISTINCT p.perm_code FROM sys_permission p " +
            "INNER JOIN sys_role_permission rp ON rp.perm_id = p.id " +
            "INNER JOIN sys_user_role ur ON ur.role_id = rp.role_id " +
            "WHERE ur.user_id = #{userId} AND p.perm_type = 2 AND p.is_enable = 1")
    List<String> selectPermCodesByUserId(@Param("userId") Long userId);

    /**
     * 查一个用户全部的权限对象（含菜单），用于前端渲染菜单 / Shiro Realm 授权
     */
    @Select("SELECT DISTINCT p.* FROM sys_permission p " +
            "INNER JOIN sys_role_permission rp ON rp.perm_id = p.id " +
            "INNER JOIN sys_user_role ur ON ur.role_id = rp.role_id " +
            "WHERE ur.user_id = #{userId} AND p.is_enable = 1 ORDER BY p.sort")
    List<SysPermission> selectAllByUserId(@Param("userId") Long userId);
}
