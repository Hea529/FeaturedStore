package com.preferredmall.product.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 商品分类：parent_id=0 为一级分类，只有一级分类配置图标
 */
@Data
@TableName("category")
public class Category implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 0 顶级 */
    @TableField("parent_id")
    private Long parentId;

    private String name;
    /** 仅一级分类有 */
    @TableField("icon_url")
    private String iconUrl;

    private Integer sort;
    /** 1 启用 / 0 禁用 */
    private Integer status;
}
