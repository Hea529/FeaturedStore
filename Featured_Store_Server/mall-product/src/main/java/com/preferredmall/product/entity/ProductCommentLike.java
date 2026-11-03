package com.preferredmall.product.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 商品评论点赞：comment_id + user_id 联合唯一
 */
@Data
@TableName("product_comment_like")
public class ProductCommentLike implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableField("comment_id")
    private Long commentId;

    @TableField("user_id")
    private Long userId;

    /** 1 已点赞 / 0 取消 */
    private Integer status;

    @TableField("update_time")
    private java.time.LocalDateTime updateTime;
}
