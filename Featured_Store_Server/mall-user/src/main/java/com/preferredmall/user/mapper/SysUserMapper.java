package com.preferredmall.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.preferredmall.user.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 用户 Mapper：按用户名 / openid / id 查询，关联角色与权限
 */
@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {

    @Select("SELECT * FROM sys_user WHERE username = #{username} LIMIT 1")
    SysUser selectByUsername(@Param("username") String username);

    @Select("SELECT * FROM sys_user WHERE openid = #{openid} LIMIT 1")
    SysUser selectByOpenid(@Param("openid") String openid);
}
