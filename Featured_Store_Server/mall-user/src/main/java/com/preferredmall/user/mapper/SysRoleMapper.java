package com.preferredmall.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.preferredmall.user.entity.SysRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SysRoleMapper extends BaseMapper<SysRole> {

    @Select("SELECT r.* FROM sys_role r " +
            "INNER JOIN sys_user_role ur ON ur.role_id = r.id " +
            "WHERE ur.user_id = #{userId} AND r.is_enable = 1")
    List<SysRole> selectByUserId(@Param("userId") Long userId);
}
