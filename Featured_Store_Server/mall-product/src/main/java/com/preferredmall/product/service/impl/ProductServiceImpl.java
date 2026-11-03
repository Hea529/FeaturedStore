package com.preferredmall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.preferredmall.common.exception.BusinessException;
import com.preferredmall.common.result.ResultCode;
import com.preferredmall.product.entity.Product;
import com.preferredmall.product.entity.ProductSpec;
import com.preferredmall.product.mapper.ProductMapper;
import com.preferredmall.product.mapper.ProductSpecMapper;
import com.preferredmall.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductMapper productMapper;
    private final ProductSpecMapper productSpecMapper;

    @Override
    public Product getById(Long id) {
        Product p = productMapper.selectById(id);
        if (p == null) {
            throw new BusinessException(ResultCode.PRODUCT_NOT_EXIST);
        }
        return p;
    }

    @Override
    public List<Product> listByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) return Collections.emptyList();
        return productMapper.selectBatchIds(ids);
    }

    @Override
    public ProductSpec getSpecById(Long specId) {
        ProductSpec spec = productSpecMapper.selectById(specId);
        if (spec == null) {
            throw new BusinessException(ResultCode.SPEC_NOT_EXIST);
        }
        return spec;
    }

    @Override
    public List<ProductSpec> listSpecsByIds(List<Long> specIds) {
        if (specIds == null || specIds.isEmpty()) return Collections.emptyList();
        return productSpecMapper.selectBatchIds(specIds);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean confirmStock(Long specId, int qty) {
        if (qty <= 0) return true;
        ProductSpec spec = productSpecMapper.selectById(specId);
        if (spec == null || spec.getStock() < qty) {
            throw new BusinessException(ResultCode.STOCK_INSUFFICIENT);
        }
        // 乐观锁：update product_spec set stock = stock - #{qty} where id = #{specId} and stock >= #{qty}
        int rows = productSpecMapper.update(null,
                new LambdaUpdateWrapper<ProductSpec>()
                        .eq(ProductSpec::getId, specId)
                        .ge(ProductSpec::getStock, qty)
                        .setSql("stock = stock - " + qty));
        if (rows == 0) {
            throw new BusinessException(ResultCode.STOCK_INSUFFICIENT);
        }
        // 同步扣减 SPU 总库存（冗余聚合字段，必须一起维护）；不足时回滚整个事务
        int spuRows = productMapper.update(null,
                new LambdaUpdateWrapper<Product>()
                        .eq(Product::getId, spec.getProductId())
                        .ge(Product::getStock, qty)
                        .setSql("stock = stock - " + qty));
        if (spuRows == 0) {
            throw new BusinessException(ResultCode.STOCK_INSUFFICIENT, "SPU 汇总库存不足");
        }
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void releaseStock(Long specId, int qty) {
        if (qty <= 0) return;
        ProductSpec spec = productSpecMapper.selectById(specId);
        if (spec == null) return;
        productSpecMapper.update(null,
                new LambdaUpdateWrapper<ProductSpec>()
                        .eq(ProductSpec::getId, specId)
                        .setSql("stock = stock + " + qty));
        productMapper.update(null,
                new LambdaUpdateWrapper<Product>()
                        .eq(Product::getId, spec.getProductId())
                        .setSql("stock = stock + " + qty));
    }

    @Override
    public void incrSales(Long productId, int qty) {
        if (qty <= 0) return;
        productMapper.update(null,
                new LambdaUpdateWrapper<Product>()
                        .eq(Product::getId, productId)
                        .setSql("sales_count = sales_count + " + qty));
    }
}
