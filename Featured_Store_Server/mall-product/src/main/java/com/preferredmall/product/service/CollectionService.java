package com.preferredmall.product.service;

import com.preferredmall.product.entity.ProductCollection;

import java.util.List;

public interface CollectionService {

    /**
     * 切换收藏状态：已收藏 → 取消；未收藏 → 收藏
     */
    boolean toggle(Long userId, Long productId);

    /**
     * 用户是否已收藏该商品
     */
    boolean isCollected(Long userId, Long productId);

    /**
     * 查用户全部收藏
     */
    List<ProductCollection> listByUser(Long userId);
}
