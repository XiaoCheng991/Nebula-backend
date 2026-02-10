package com.nebula.common.exception;

import lombok.Getter;

/**
 * 业务异常类
 * 用于统一处理业务逻辑中的异常情况
 */
@Getter
public class BusinessException extends RuntimeException {

    /**
     * 错误码
     */
    private final int code;

    /**
     * 错误信息
     */
    private final String message;

    /**
     * 根据错误码枚举创建异常
     */
    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
    }

    /**
     * 根据错误码枚举和自定义错误信息创建异常
     */
    public BusinessException(ErrorCode errorCode, String customMessage) {
        super(customMessage);
        this.code = errorCode.getCode();
        this.message = customMessage;
    }

    /**
     * 根据错误码和错误信息创建异常
     */
    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    /**
     * 根据错误码枚举和原始异常创建异常
     */
    public BusinessException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
    }

    /**
     * 根据错误码枚举、自定义错误信息和原始异常创建异常
     */
    public BusinessException(ErrorCode errorCode, String customMessage, Throwable cause) {
        super(customMessage, cause);
        this.code = errorCode.getCode();
        this.message = customMessage;
    }

    /**
     * 快速创建未登录异常
     */
    public static BusinessException unauthorized() {
        return new BusinessException(ErrorCode.UNAUTHORIZED);
    }

    /**
     * 快速创建 Token 过期异常
     */
    public static BusinessException tokenExpired() {
        return new BusinessException(ErrorCode.TOKEN_EXPIRED);
    }

    /**
     * 快速创建 Token 无效异常
     */
    public static BusinessException tokenInvalid() {
        return new BusinessException(ErrorCode.TOKEN_INVALID);
    }

    /**
     * 快速创建用户不存在异常
     */
    public static BusinessException userNotFound() {
        return new BusinessException(ErrorCode.USER_NOT_FOUND);
    }

    /**
     * 快速创建参数错误异常
     */
    public static BusinessException paramError(String message) {
        return new BusinessException(ErrorCode.PARAM_ERROR, message);
    }

    /**
     * 快速创建系统错误异常
     */
    public static BusinessException systemError() {
        return new BusinessException(ErrorCode.SYSTEM_ERROR);
    }

    @Override
    public String toString() {
        return "BusinessException{" +
                "code=" + code +
                ", message='" + message + '\'' +
                '}';
    }
}
