package com.nebula.config.exception;

import com.nebula.config.result.Result;
import com.nebula.config.result.ResultCode;
import lombok.Getter;

/**
 * 业务异常
 */
@Getter
public class BusinessException extends RuntimeException {

    private final Integer code;

    public BusinessException(String message) {
        super(message);
        this.code = ResultCode.ERROR;
    }

    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    public Result<?> toResult() {
        return Result.error(code, getMessage());
    }
}
