package com.preferredmall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.preferredmall.common.exception.BusinessException;
import com.preferredmall.common.result.ResultCode;
import com.preferredmall.product.entity.Category;
import com.preferredmall.product.mapper.CategoryMapper;
import com.preferredmall.product.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryMapper categoryMapper;

    @Override
    public Category getById(Long id) {
        Category c = categoryMapper.selectById(id);
        if (c == null) {
            throw new BusinessException(ResultCode.VALIDATE_FAILED, "分类不存在");
        }
        return c;
    }

    @Override
    public List<Category> listByParent(Long parentId) {
        return categoryMapper.selectList(new LambdaQueryWrapper<Category>()
                .eq(Category::getParentId, parentId)
                .eq(Category::getStatus, 1)
                .orderByAsc(Category::getSort));
    }

    @Override
    public List<Category> listAllEnabled() {
        return categoryMapper.selectList(new LambdaQueryWrapper<Category>()
                .eq(Category::getStatus, 1)
                .orderByAsc(Category::getParentId)
                .orderByAsc(Category::getSort));
    }
}
