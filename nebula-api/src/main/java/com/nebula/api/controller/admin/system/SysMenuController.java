package com.nebula.api.controller.admin.system;

import cn.dev33.satoken.stp.StpUtil;
import com.nebula.common.annotation.OperationLog;
import com.nebula.common.annotation.RequirePermission;
import com.nebula.config.result.Result;
import com.nebula.model.entity.system.SysMenu;
import com.nebula.service.service.system.SysMenuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/system/menu")
@RequiredArgsConstructor
@Tag(name = "菜单管理", description = "菜单管理接口")
@RequirePermission("system:menu:view")
public class SysMenuController {

    private final SysMenuService sysMenuService;

    @GetMapping("/list")
    @Operation(summary = "获取菜单列表")
    @RequirePermission("system:menu:query")
    public Result<List<SysMenu>> list() {
        List<SysMenu> menus = sysMenuService.list();
        return Result.success(menus);
    }

    @GetMapping("/tree")
    @Operation(summary = "获取菜单树")
    @RequirePermission("system:menu:query")
    public Result<List<SysMenu>> getMenuTree() {
        List<SysMenu> menus = sysMenuService.list();
        List<SysMenu> tree = sysMenuService.buildMenuTree(menus);
        return Result.success(tree);
    }

    @GetMapping("/user")
    @Operation(summary = "获取当前用户的菜单树")
    @RequirePermission("system:menu:query")
    public Result<List<SysMenu>> getUserMenuTree() {
        StpUtil.checkLogin();
        Long userId = StpUtil.getLoginIdAsLong();
        List<SysMenu> menus = sysMenuService.getMenuListByUserId(userId);
        List<SysMenu> tree = sysMenuService.buildMenuTree(menus);
        return Result.success(tree);
    }

    @GetMapping("/{menuId}")
    @Operation(summary = "获取菜单详情")
    @RequirePermission("system:menu:query")
    public Result<SysMenu> getById(@PathVariable Long menuId) {
        return Result.success(sysMenuService.getById(menuId));
    }

    @PostMapping
    @Operation(summary = "新增菜单")
    @RequirePermission("system:menu:add")
    @OperationLog(module = "菜单管理", operation = "新增菜单")
    public Result<Void> add(@Valid @RequestBody SysMenu menu) {
        menu.setIsSystem(false);
        sysMenuService.save(menu);
        return Result.success();
    }

    @PutMapping
    @Operation(summary = "编辑菜单")
    @RequirePermission("system:menu:edit")
    @OperationLog(module = "菜单管理", operation = "编辑菜单")
    public Result<Void> edit(@Valid @RequestBody SysMenu menu) {
        sysMenuService.updateById(menu);
        return Result.success();
    }

    @DeleteMapping("/{menuId}")
    @Operation(summary = "删除菜单")
    @RequirePermission("system:menu:delete")
    @OperationLog(module = "菜单管理", operation = "删除菜单")
    public Result<Void> delete(@PathVariable Long menuId) {
        sysMenuService.deleteMenu(menuId);
        return Result.success();
    }
}
