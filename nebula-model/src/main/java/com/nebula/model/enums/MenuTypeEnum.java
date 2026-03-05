package com.nebula.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MenuTypeEnum {
    DIRECTORY("directory", "目录"),
    MENU("menu", "菜单"),
    BUTTON("button", "按钮");

    private final String code;
    private final String desc;

    public static MenuTypeEnum fromCode(String code) {
        for (MenuTypeEnum type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        return null;
    }
}
