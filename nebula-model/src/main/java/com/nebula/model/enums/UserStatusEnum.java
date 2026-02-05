package com.nebula.model.enums;

import lombok.Getter;

/**
 * 用户状态枚举
 */
@Getter
public enum UserStatusEnum {

    /**
     * 禁用
     */
    DISABLED(0, "禁用"),

    /**
     * 启用
     */
    ENABLED(1, "启用");

    private final Integer code;
    private final String desc;

    UserStatusEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
