package com.preferredmall.product.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.preferredmall.common.exception.BusinessException;
import com.preferredmall.common.result.Result;
import com.preferredmall.common.result.ResultCode;
import com.preferredmall.product.dto.ProductSaveDTO;
import com.preferredmall.product.entity.Product;
import com.preferredmall.product.mapper.ProductMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 管理端：商品 CRUD / 上下架。入参使用白名单 DTO，防批量赋值篡改敏感字段。
 */
@Tag(name = "商品管理（后台）")
@RestController
@RequestMapping("/admin/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductMapper productMapper;

    @Operation(summary = "获取商品列表")
    @GetMapping("/list")
    public Result<List<Product>> list() {
        return Result.success(productMapper.selectList(new LambdaQueryWrapper<Product>()
                .orderByDesc(Product::getId)));
    }

    @Operation(summary = "新增商品")
    @PostMapping
    public Result<Product> create(@RequestBody @Validated ProductSaveDTO dto) {
        Product product = new Product();
        applyDto(product, dto);
        product.setStatus(1);
        product.setStock(0);
        product.setSalesCount(0);
        product.setViewCount(0);
        productMapper.insert(product);
        return Result.success(product);
    }

    @Operation(summary = "更新商品")
    @PutMapping("/{id}")
    public Result<Product> update(@PathVariable Long id, @RequestBody @Validated ProductSaveDTO dto) {
        Product product = productMapper.selectById(id);
        if (product == null) {
            throw new BusinessException(ResultCode.PRODUCT_NOT_EXIST);
        }
        applyDto(product, dto);
        productMapper.updateById(product);
        return Result.success(product);
    }

    @Operation(summary = "商品上下架")
    @PostMapping("/{id}/shelve")
    public Result<Void> shelve(@PathVariable Long id, @RequestParam Integer status) {
        if (status == null || (status != 0 && status != 1)) {
            throw new BusinessException(ResultCode.VALIDATE_FAILED, "status 仅支持 0/1");
        }
        productMapper.update(null, new LambdaUpdateWrapper<Product>()
                .eq(Product::getId, id)
                .set(Product::getStatus, status));
        return Result.success();
    }

    private void applyDto(Product p, ProductSaveDTO dto) {
        p.setName(dto.getName());
        p.setSubtitle(dto.getSubtitle());
        p.setCoverImage(dto.getCoverImage());
        p.setDetail(dto.getDetail());
        p.setCategoryId(dto.getCategoryId());
        p.setPrice(dto.getPrice());
        p.setEnterprisePrice(dto.getEnterprisePrice());
    }
}
