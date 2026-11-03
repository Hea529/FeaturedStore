package com.preferredmall.product.service;

import com.preferredmall.product.entity.Category;

import java.util.List;

public interface CategoryService {

    Category getById(Long id);

    List<Category> listByParent(Long parentId);

    /**
     * 查整个分类树（parentId=0 递归展开）
     */
    List<Category> listAllEnabled();
}
