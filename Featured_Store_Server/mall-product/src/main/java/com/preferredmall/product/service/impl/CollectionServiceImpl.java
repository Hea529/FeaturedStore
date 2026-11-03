package com.preferredmall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.preferredmall.product.entity.ProductCollection;
import com.preferredmall.product.mapper.ProductCollectionMapper;
import com.preferredmall.product.service.CollectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CollectionServiceImpl implements CollectionService {

    private final ProductCollectionMapper collectionMapper;

    @Override
    public boolean toggle(Long userId, Long productId) {
        ProductCollection existing = collectionMapper.selectOne(new LambdaQueryWrapper<ProductCollection>()
                .eq(ProductCollection::getUserId, userId)
                .eq(ProductCollection::getProductId, productId)
                .last("LIMIT 1"));
        if (existing != null) {
            collectionMapper.deleteById(existing);
            return false;
        }
        ProductCollection c = new ProductCollection();
        c.setUserId(userId);
        c.setProductId(productId);
        collectionMapper.insert(c);
        return true;
    }

    @Override
    public boolean isCollected(Long userId, Long productId) {
        return collectionMapper.selectCount(new LambdaQueryWrapper<ProductCollection>()
                .eq(ProductCollection::getUserId, userId)
                .eq(ProductCollection::getProductId, productId)) > 0;
    }

    @Override
    public List<ProductCollection> listByUser(Long userId) {
        return collectionMapper.selectList(new LambdaQueryWrapper<ProductCollection>()
                .eq(ProductCollection::getUserId, userId)
                .orderByDesc(ProductCollection::getCreateTime));
    }
}
