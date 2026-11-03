package com.preferredmall.common.result;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * 通用启用/禁用状态
 */
@Getter
public enum CommonStatus {
    ACTIVE("active", 1, "启用"),
    INACTIVE("inactive", 0, "禁用");

    @JsonValue
    private final String value;
    @EnumValue
    private final Integer number;
    private final String desc;

    CommonStatus(String value, Integer number, String desc) {
        this.value = value;
        this.number = number;
        this.desc = desc;
    }

    public static CommonStatus getByValue(String value) {
        for (CommonStatus s : values()) {
            if (s.value.equals(value)) return s;
        }
        throw new IllegalArgumentException("无效的 CommonStatus.value: " + value);
    }

    @JsonCreator
    public static CommonStatus fromValue(String value) {
        return getByValue(value);
    }
}
