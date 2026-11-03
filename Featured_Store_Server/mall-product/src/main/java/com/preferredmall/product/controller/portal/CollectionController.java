package com.preferredmall.product.controller.portal;

import com.preferredmall.common.context.BaseContext;
import com.preferredmall.common.result.Result;
import com.preferredmall.product.entity.ProductCollection;
import com.preferredmall.product.service.CollectionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * C 端：商品收藏
 */
@Tag(name = "商品收藏")
@RestController
@RequestMapping("/api/collect")
@RequiredArgsConstructor
public class CollectionController {

    private final CollectionService collectionService;

    @Operation(summary = "收藏/取消收藏商品")
    @PostMapping("/toggle")
    public Result<Boolean> toggle(@RequestParam Long productId) {
        Long uid = BaseContext.currentUserId();
        boolean collected = collectionService.toggle(uid, productId);
        return Result.success(collected);
    }

    @Operation(summary = "获取我的收藏列表")
    @GetMapping
    public Result<List<ProductCollection>> list() {
        Long uid = BaseContext.currentUserId();
        return Result.success(collectionService.listByUser(uid));
    }

    @Operation(summary = "检查商品是否已收藏")
    @GetMapping("/check")
    public Result<Boolean> check(@RequestParam Long productId) {
        Long uid = BaseContext.currentUserId();
        return Result.success(collectionService.isCollected(uid, productId));
    }
}
