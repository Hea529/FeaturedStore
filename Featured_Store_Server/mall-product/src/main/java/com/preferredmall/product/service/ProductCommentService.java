package com.preferredmall.product.service;

import com.preferredmall.product.entity.ProductComment;

import java.util.List;

/**
 * 商品评论服务
 */
public interface ProductCommentService {

    /**
     * 发布一级评论
     */
    ProductComment post(Long userId, ProductComment comment);

    /**
     * 发布二级回复（parentId 指向一级评论）
     */
    ProductComment reply(Long userId, Long parentId, ProductComment replyComment);

    /**
     * 点赞 / 取消点赞：返回 true 点赞、false 取消
     */
    boolean toggleLike(Long userId, Long commentId);

    /**
     * 查商品全部一级评论（status=1 已审核）
     */
    List<ProductComment> listFirstByProduct(Long productId);

    /**
     * 查某条一级评论的所有二级回复
     */
    List<ProductComment> listReplies(Long firstCommentId);
}
