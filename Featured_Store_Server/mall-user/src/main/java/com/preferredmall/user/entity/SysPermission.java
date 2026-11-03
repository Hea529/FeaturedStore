package com.preferredmall.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 权限项表：perm_type 区分菜单权限（1）与接口权限（2）
 */
@Data
@TableName("sys_permission")
public class SysPermission implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 父权限 ID，0 表示顶级 */
    @TableField("parent_id")
    private Long parentId;

    @TableField("perm_code")
    private String permCode;

    @TableField("perm_name")
    private String permName;

    /** 1 菜单 / 2 接口 */
    @TableField("perm_type")
    private Integer permType;

    /** 菜单 URL 或接口路径（如 /api/admin/product/list） */
    private String path;

    /** 菜单图标 */
    private String icon;

    private Integer sort;
    /** 1 启用 / 0 禁用 */
    @TableField("is_enable")
    private Integer isEnable;

    @TableField(fill = com.baomidou.mybatisplus.annotation.FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = com.baomidou.mybatisplus.annotation.FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
