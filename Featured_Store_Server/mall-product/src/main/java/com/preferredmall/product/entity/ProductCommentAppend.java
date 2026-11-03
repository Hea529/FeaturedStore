package com.preferredmall.product.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 商品评论追评：每条评论（一级）只能有一条追评，comment_id 唯一
 */
@Data
@TableName("product_comment_append")
public class ProductCommentAppend implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 对应一级评论 id（唯一约束） */
    @TableField("comment_id")
    private Long commentId;

    private String content;
    /** 图片 URL JSON */
    @TableField("image_urls")
    private String imageUrls;

    /** 0 待审核 / 1 通过 / 2 驳回 */
    private Integer status;

    @TableField(fill = com.baomidou.mybatisplus.annotation.FieldFill.INSERT)
    private LocalDateTime createTime;
}
