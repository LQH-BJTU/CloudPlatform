package com.sustar.ecsservice.exceptions;

import com.sustar.ecsservice.constants.ResponseCode;
import lombok.Getter;

/**
 * 业务异常类
 */
@Getter
public class BusinessException extends RuntimeException {

    private final Integer code;

    public BusinessException(String message) {
        super(message);
        this.code = ResponseCode.INTERNAL_ERROR;
    }

    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    
}
