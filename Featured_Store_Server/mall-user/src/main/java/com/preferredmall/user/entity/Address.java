package com.preferredmall.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户收货地址表
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("address")
public class Address implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("user_id")
    private Long userId;

    /** 收货人姓名 */
    private String receiver;

    private String phone;

    @TableField("province_id")
    private Long provinceId;
    @TableField("city_id")
    private Long cityId;
    @TableField("district_id")
    private Long districtId;

    @TableField("province_name")
    private String provinceName;
    @TableField("city_name")
    private String cityName;
    @TableField("district_name")
    private String districtName;

    /** 详细地址 */
    @TableField("detail_address")
    private String detailAddress;

    /** 是否默认地址 1 是 / 0 否 */
    @TableField("is_default")
    private Integer isDefault;

    @TableField(fill = com.baomidou.mybatisplus.annotation.FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = com.baomidou.mybatisplus.annotation.FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
