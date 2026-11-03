package com.preferredmall.product.controller.portal;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.preferredmall.common.exception.BusinessException;
import com.preferredmall.common.result.Result;
import com.preferredmall.common.result.ResultCode;
import com.preferredmall.product.entity.Category;
import com.preferredmall.product.entity.Product;
import com.preferredmall.product.mapper.ProductMapper;
import com.preferredmall.product.service.CategoryService;
import com.preferredmall.product.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * C 端：分类树 + 商品列表 / 详情
 */
@Tag(name = "商品查询（前台）")
@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class ProductQueryController {

    private final ProductService productService;
    private final CategoryService categoryService;
    private final ProductMapper productMapper;

    @Operation(summary = "获取分类列表")
    @GetMapping("/categories")
    public Result<List<Category>> categories() {
        return Result.success(categoryService.listAllEnabled());
    }

    @Operation(summary = "获取商品列表")
    @GetMapping("/list")
    public Result<List<Product>> list(@RequestParam(required = false) Long categoryId,
                                       @RequestParam(required = false) String keyword) {
        LambdaQueryWrapper<Product> w = new LambdaQueryWrapper<>();
        w.eq(Product::getStatus, 1);
        if (categoryId != null) {
            w.eq(Product::getCategoryId, categoryId);
        }
        if (keyword != null && !keyword.isBlank()) {
            w.like(Product::getName, keyword);
        }
        w.orderByDesc(Product::getSalesCount);
        return Result.success(productMapper.selectList(w));
    }

    @Operation(summary = "获取商品详情")
    @GetMapping("/{id}")
    public Result<Product> detail(@PathVariable("id") Long id) {
        Product product = productService.getById(id);
        if (product.getStatus() == null || product.getStatus() != 1) {
            throw new BusinessException(ResultCode.PRODUCT_OFFLINE);
        }
        return Result.success(product);
    }
}
