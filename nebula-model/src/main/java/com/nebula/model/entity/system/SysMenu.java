package com.nebula.model.entity.system;

import com.baomidou.mybatisplus.annotation.*;
import com.nebula.model.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_menu")
@Schema(description = "菜单表")
public class SysMenu extends BaseEntity {

    @TableId(type = IdType.AUTO)
    @Schema(description = "菜单ID")
    private Long id;

    @Schema(description = "父菜单ID")
    private Long parentId;

    @Schema(description = "菜单类型：directory-目录，menu-菜单，button-按钮")
    private String menuType;

    @Schema(description = "菜单名称")
    private String menuName;

    @Schema(description = "路由路径")
    private String path;

    @Schema(description = "组件路径")
    private String component;

    @Schema(description = "权限标识")
    private String permission;

    @Schema(description = "菜单图标")
    private String icon;

    @Schema(description = "排序")
    private Integer sortOrder;

    @Schema(description = "是否显示")
    private Boolean isVisible;

    @Schema(description = "是否系统内置，不可删除")
    private Boolean isSystem;

    @TableField(exist = false)
    @Schema(description = "父菜单名称")
    private String parentName;

    @TableField(exist = false)
    @Schema(description = "子菜单")
    private List<SysMenu> children;
}
