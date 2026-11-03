package com.preferredmall.common.exception;

import com.preferredmall.common.result.ResultCode;
import lombok.Getter;

/**
 * 业务异常：所有业务校验失败统一抛出
 */
@Getter
public class BusinessException extends RuntimeException {

    private final Integer code;

    public BusinessException(ResultCode rc) {
        super(rc.getMessage());
        this.code = rc.getCode();
    }

    public BusinessException(ResultCode rc, String message) {
        super(message);
        this.code = rc.getCode();
    }

    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
    }
}
