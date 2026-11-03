package com.preferredmall.product.service;

import com.preferredmall.product.entity.Product;
import com.preferredmall.product.entity.ProductSpec;

import java.util.List;

/**
 * 商品核心服务：供其他模块注入的对外 API
 */
public interface ProductService {

    Product getById(Long id);

    List<Product> listByIds(List<Long> ids);

    ProductSpec getSpecById(Long specId);

    /**
     * 批量按 specId 查规格，用于下单时组装 order_item 快照
     */
    List<ProductSpec> listSpecsByIds(List<Long> specIds);

    /**
     * 扣减规格库存 + 同步扣减 SPU 总库存（下单确认阶段）
     * @return true 成功 / false 库存不足
     */
    boolean confirmStock(Long specId, int qty);

    /**
     * 释放规格库存 + 同步释放 SPU 总库存（订单取消/超时）
     */
    void releaseStock(Long specId, int qty);

    /**
     * 增加销量（订单完成后）
     */
    void incrSales(Long productId, int qty);
}
