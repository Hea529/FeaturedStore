package com.preferredmall.product.controller.portal;

import com.preferredmall.common.context.BaseContext;
import com.preferredmall.common.result.Result;
import com.preferredmall.product.entity.ProductComment;
import com.preferredmall.product.service.ProductCommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * C 端：评论/追评/点赞
 */
@Tag(name = "商品评论")
@RestController
@RequestMapping("/api/comment")
@RequiredArgsConstructor
public class CommentController {

    private final ProductCommentService commentService;

    @Operation(summary = "获取商品首评列表")
    @GetMapping("/first/{productId}")
    public Result<List<ProductComment>> first(@PathVariable Long productId) {
        return Result.success(commentService.listFirstByProduct(productId));
    }

    @Operation(summary = "获取评论回复列表")
    @GetMapping("/replies/{parentId}")
    public Result<List<ProductComment>> replies(@PathVariable Long parentId) {
        return Result.success(commentService.listReplies(parentId));
    }

    @Operation(summary = "发表评论")
    @PostMapping("/post")
    public Result<ProductComment> post(@RequestBody ProductComment comment) {
        Long uid = BaseContext.currentUserId();
        return Result.success(commentService.post(uid, comment));
    }

    @Operation(summary = "回复评论")
    @PostMapping("/reply")
    public Result<ProductComment> reply(@RequestParam Long parentId,
                                         @RequestBody ProductComment reply) {
        Long uid = BaseContext.currentUserId();
        return Result.success(commentService.reply(uid, parentId, reply));
    }

    @Operation(summary = "点赞/取消点赞评论")
    @PostMapping("/like")
    public Result<Boolean> like(@RequestParam Long commentId) {
        Long uid = BaseContext.currentUserId();
        return Result.success(commentService.toggleLike(uid, commentId));
    }
}
