package com.preferredmall.product.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 搜索关键词词库：用于搜索页热搜 & 违规词屏蔽
 */
@Data
@TableName("product_search_keyword")
public class ProductSearchKeyword implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 唯一 */
    private String keyword;

    /** 使用次数 */
    private Long useCount;

    /** 1 是热搜 / 0 否 */
    private Integer isHot;

    /** 1 展示 / 0 隐藏 */
    private Integer isShow;

    private LocalDateTime updateTime;
}
