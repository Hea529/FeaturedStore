package com.preferredmall.product.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 商品评论主表：parent_id=0 为一级评论，>0 为对某条一级评论的回复（两级楼中楼）
 */
@Data
@TableName("product_comment")
public class ProductComment implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("product_id")
    private Long productId;

    /** 0 一级 / >0 二级（指向一级评论 id） */
    @TableField("parent_id")
    private Long parentId;

    /** 二级回复中被 @ 的用户 id */
    @TableField("reply_user_id")
    private Long replyUserId;

    @TableField("user_id")
    private Long userId;

    /** 快照字段：避免用户改昵称/头像后历史评论被污染 */
    @TableField("user_nickname")
    private String userNickname;
    @TableField("user_avatar")
    private String userAvatar;

    /** 1~5 评分（一级评论才有） */
    private Integer rating;

    private String content;

    /** 图片 URL 列表 JSON（如 ["a.jpg","b.jpg"]） */
    @TableField("image_urls")
    private String imageUrls;

    /** 是否匿名 0 否 / 1 是 */
    private Integer anonymous;

    /** 是否已删除（软删） */
    @TableField("is_deleted")
    private Integer isDeleted;

    /** 是否已买过该商品（通过订单快照判定） */
    @TableField("bought_flag")
    private Integer boughtFlag;

    /** 点赞数（冗余计数，来自 Redis） */
    @TableField("like_count")
    private Integer likeCount;

    /** 状态：0 待审核 / 1 通过 / 2 驳回 */
    private Integer status;

    @TableField(fill = com.baomidou.mybatisplus.annotation.FieldFill.INSERT)
    private LocalDateTime createTime;
}
