package com.preferredmall.product.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 管理端商品保存 DTO：白名单字段，防止批量赋值篡改 stock/salesCount/status 等敏感字段
 */
@Data
public class ProductSaveDTO {

    @NotBlank(message = "商品名称必填")
    private String name;

    private String subtitle;
    private String coverImage;
    private String detail;

    @NotNull(message = "分类必填")
    private Long categoryId;

    @NotNull(message = "基准价必填")
    private BigDecimal price;

    private BigDecimal enterprisePrice;
}
