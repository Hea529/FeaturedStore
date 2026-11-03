package com.preferredmall.order.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 下单请求 DTO
 */
@Data
public class OrderCreateDTO {

    /** 收货地址 id（从 user.AddressService 取） */
    @NotNull(message = "收货地址必填")
    private Long addressId;

    /** 选中的购物车项（cartIds），或直接指定 item */
    private List<Long> cartIds;

    /** 直接下单场景（非购物车） */
    private List<OrderItemDTO> items;

    /** 优惠券 id，可为 null */
    private Long userCouponId;

    /** 支付方式 */
    private Integer payType;

    private String remark;

    @Data
    public static class OrderItemDTO {
        @NotNull private Long specId;
        @NotNull private Integer quantity;
    }
}
