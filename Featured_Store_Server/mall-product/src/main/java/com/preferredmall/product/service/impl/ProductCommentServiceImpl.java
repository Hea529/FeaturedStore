package com.preferredmall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.preferredmall.common.exception.BusinessException;
import com.preferredmall.common.result.ResultCode;
import com.preferredmall.product.entity.ProductComment;
import com.preferredmall.product.entity.ProductCommentLike;
import com.preferredmall.product.mapper.ProductCommentLikeMapper;
import com.preferredmall.product.mapper.ProductCommentMapper;
import com.preferredmall.product.service.ProductCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductCommentServiceImpl implements ProductCommentService {

    private final ProductCommentMapper commentMapper;
    private final ProductCommentLikeMapper likeMapper;

    @Override
    public ProductComment post(Long userId, ProductComment comment) {
        comment.setUserId(userId);
        comment.setParentId(0L);
        comment.setStatus(1); // 简化：管理端上线前直接通过审核，正式环境改 0 待审核
        comment.setLikeCount(0);
        comment.setAnonymous(0);
        comment.setIsDeleted(0);
        commentMapper.insert(comment);
        return comment;
    }

    @Override
    public ProductComment reply(Long userId, Long parentId, ProductComment replyComment) {
        ProductComment parent = commentMapper.selectById(parentId);
        if (parent == null) {
            throw new BusinessException(ResultCode.VALIDATE_FAILED, "被回复的评论不存在");
        }
        replyComment.setUserId(userId);
        replyComment.setParentId(parentId);
        replyComment.setProductId(parent.getProductId());
        replyComment.setRating(null);
        replyComment.setStatus(1);
        replyComment.setLikeCount(0);
        replyComment.setAnonymous(0);
        replyComment.setIsDeleted(0);
        commentMapper.insert(replyComment);
        return replyComment;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean toggleLike(Long userId, Long commentId) {
        ProductCommentLike existing = likeMapper.selectOne(new LambdaQueryWrapper<ProductCommentLike>()
                .eq(ProductCommentLike::getCommentId, commentId)
                .eq(ProductCommentLike::getUserId, userId)
                .last("LIMIT 1"));
        boolean liked;
        if (existing == null) {
            ProductCommentLike l = new ProductCommentLike();
            l.setCommentId(commentId);
            l.setUserId(userId);
            l.setStatus(1);
            l.setUpdateTime(LocalDateTime.now());
            likeMapper.insert(l);
            liked = true;
        } else {
            int next = existing.getStatus() == 1 ? 0 : 1;
            likeMapper.update(null, new LambdaUpdateWrapper<ProductCommentLike>()
                    .eq(ProductCommentLike::getCommentId, commentId)
                    .eq(ProductCommentLike::getUserId, userId)
                    .set(ProductCommentLike::getStatus, next)
                    .set(ProductCommentLike::getUpdateTime, LocalDateTime.now()));
            liked = next == 1;
        }
        // 同步更新 comment.like_count（DB 兜底，高并发用 Redis 再定时回写）
        int delta = liked ? 1 : -1;
        commentMapper.update(null, new LambdaUpdateWrapper<ProductComment>()
                .eq(ProductComment::getId, commentId)
                .setSql("like_count = like_count + " + delta));
        return liked;
    }

    @Override
    public List<ProductComment> listFirstByProduct(Long productId) {
        return commentMapper.selectList(new LambdaQueryWrapper<ProductComment>()
                .eq(ProductComment::getProductId, productId)
                .eq(ProductComment::getParentId, 0L)
                .eq(ProductComment::getStatus, 1)
                .orderByDesc(ProductComment::getCreateTime));
    }

    @Override
    public List<ProductComment> listReplies(Long firstCommentId) {
        return commentMapper.selectList(new LambdaQueryWrapper<ProductComment>()
                .eq(ProductComment::getParentId, firstCommentId)
                .eq(ProductComment::getStatus, 1)
                .orderByAsc(ProductComment::getCreateTime));
    }
}
