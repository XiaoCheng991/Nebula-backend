package com.nebula.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DataScopeEnum {
    ALL("ALL", "全部数据"),
    SELF("SELF", "仅本人数据");

    private final String code;
    private final String desc;

    public static DataScopeEnum fromCode(String code) {
        for (DataScopeEnum scope : values()) {
            if (scope.code.equals(code)) {
                return scope;
            }
        }
        return SELF;
    }
}
