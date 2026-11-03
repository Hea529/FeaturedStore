package com.preferredmall.product.controller.admin;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.preferredmall.common.result.Result;
import com.preferredmall.product.entity.Category;
import com.preferredmall.product.mapper.CategoryMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "分类管理")
@RestController
@RequestMapping("/admin/category")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryMapper categoryMapper;

    @Operation(summary = "新增分类")
    @PostMapping
    public Result<Category> create(@RequestBody Category category) {
        category.setStatus(1);
        categoryMapper.insert(category);
        return Result.success(category);
    }

    @Operation(summary = "更新分类")
    @PutMapping("/{id}")
    public Result<Category> update(@PathVariable Long id, @RequestBody Category category) {
        category.setId(id);
        categoryMapper.updateById(category);
        return Result.success(category);
    }

    @Operation(summary = "切换分类启用状态")
    @PostMapping("/{id}/toggle")
    public Result<Void> toggle(@PathVariable Long id) {
        categoryMapper.update(null, new LambdaUpdateWrapper<Category>()
                .eq(Category::getId, id)
                .setSql("status = 1 - status"));
        return Result.success();
    }
}
