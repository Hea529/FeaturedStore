package com.preferredmall.user.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AddressVO {

    private Long id;
    private String receiver;
    private String phone;
    private String provinceName;
    private String cityName;
    private String districtName;
    private String detailAddress;
    private Integer isDefault;
    private LocalDateTime createTime;
}
