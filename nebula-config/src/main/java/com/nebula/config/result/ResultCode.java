package com.nebula.config.result;

/**
 * 响应码枚举
 */
public interface ResultCode {

    /**
     * 成功
     */
    Integer SUCCESS = 200;

    /**
     * 失败
     */
    Integer ERROR = 500;

    /**
     * 参数错误
     */
    Integer PARAM_ERROR = 400;

    /**
     * 未授权
     */
    Integer UNAUTHORIZED = 401;

    /**
     * 禁止访问
     */
    Integer FORBIDDEN = 403;

    /**
     * 资源不存在
     */
    Integer NOT_FOUND = 404;
}
