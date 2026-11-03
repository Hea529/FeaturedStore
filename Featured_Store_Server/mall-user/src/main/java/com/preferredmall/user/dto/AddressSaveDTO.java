package com.preferredmall.user.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


/**
 * 新增收货地址
 */
@Data
public class AddressSaveDTO {

    @NotBlank(message = "收货人必填")
    private String receiver;

    @NotBlank(message = "手机号必填")
    @Size(min = 11, max = 11, message = "手机号长度必须 11 位")
    private String phone;

    private Long provinceId;
    private Long cityId;
    private Long districtId;
    private String provinceName;
    private String cityName;
    private String districtName;

    @NotBlank(message = "详细地址必填")
    private String detailAddress;

    /** 是否默认：1 是 / 0 否 */
    private Integer isDefault;
}
