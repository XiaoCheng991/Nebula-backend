package com.nebula.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 错误码枚举
 * 统一定义系统中的所有错误码和错误信息
 */
@Getter
@AllArgsConstructor
public enum ErrorCode {

    // 通用错误 1xxx
    SUCCESS(200, "操作成功"),
    SYSTEM_ERROR(1000, "系统繁忙，请稍后重试"),
    PARAM_ERROR(1001, "参数错误"),
    PARAM_MISSING(1002, "缺少必要参数"),
    PARAM_INVALID(1003, "参数格式不正确"),

    // 认证相关错误 2xxx
    UNAUTHORIZED(2001, "未登录，请先登录"),
    TOKEN_EXPIRED(2002, "登录已过期，请重新登录"),
    TOKEN_INVALID(2003, "Token 无效"),
    TOKEN_MISSING(2004, "Token 不能为空"),
    REFRESH_TOKEN_EXPIRED(2005, "刷新令牌已过期，请重新登录"),
    REFRESH_TOKEN_INVALID(2006, "刷新令牌无效"),
    LOGIN_FAILED(2007, "登录失败"),
    PASSWORD_ERROR(2008, "密码错误"),
    USER_NOT_FOUND(2009, "用户不存在"),
    USER_DISABLED(2010, "账号已被禁用"),
    USER_DELETED(2011, "账号已被删除"),

    // 注册相关错误 3xxx
    EMAIL_EXISTS(3001, "邮箱已被注册"),
    USERNAME_EXISTS(3002, "用户名已被占用"),
    REGISTER_FAILED(3003, "注册失败"),

    // 用户相关错误 4xxx
    USER_INFO_NOT_FOUND(4001, "用户信息不存在"),
    PROFILE_UPDATE_FAILED(4002, "更新个人资料失败"),

    // 文件相关错误 5xxx
    FILE_UPLOAD_FAILED(5001, "文件上传失败"),
    FILE_NOT_FOUND(5002, "文件不存在"),
    FILE_TYPE_ERROR(5003, "文件类型不支持"),
    FILE_SIZE_EXCEED(5004, "文件大小超出限制"),

    // OAuth 相关错误 6xxx
    OAUTH_FAILED(6001, "OAuth 认证失败"),
    GITHUB_AUTH_FAILED(6002, "GitHub 认证失败"),
    OAUTH_USER_INFO_ERROR(6003, "获取用户信息失败"),

    // Redis 相关错误 7xxx
    CACHE_ERROR(7001, "缓存操作失败"),
    CACHE_SET_FAILED(7002, "缓存设置失败"),
    CACHE_GET_FAILED(7003, "缓存获取失败"),
    CACHE_DELETE_FAILED(7004, "缓存删除失败"),

    // 数据库相关错误 8xxx
    DB_ERROR(8001, "数据库操作失败"),
    DB_INSERT_FAILED(8002, "数据插入失败"),
    DB_UPDATE_FAILED(8003, "数据更新失败"),
    DB_DELETE_FAILED(8004, "数据删除失败"),
    DB_QUERY_FAILED(8005, "数据查询失败"),

    // 业务逻辑错误 9xxx
    OPERATION_NOT_ALLOWED(9001, "不允许执行此操作"),
    RESOURCE_NOT_FOUND(9002, "资源不存在"),
    PERMISSION_DENIED(9003, "权限不足"),
    DUPLICATE_OPERATION(9004, "请勿重复操作");

    /**
     * 错误码
     */
    private final int code;

    /**
     * 错误信息
     */
    private final String message;

    /**
     * 根据错误码获取错误信息
     */
    public static String getMessageByCode(int code) {
        for (ErrorCode errorCode : values()) {
            if (errorCode.getCode() == code) {
                return errorCode.getMessage();
            }
        }
        return "未知错误";
    }
}
