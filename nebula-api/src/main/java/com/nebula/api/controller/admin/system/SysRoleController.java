package com.nebula.api.controller.admin.system;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nebula.common.annotation.OperationLog;
import com.nebula.common.annotation.RequirePermission;
import com.nebula.config.result.Result;
import com.nebula.model.entity.system.SysRole;
import com.nebula.service.service.system.SysRoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/system/role")
@RequiredArgsConstructor
@Tag(name = "角色管理", description = "角色管理接口")
@RequirePermission("system:role:view")
public class SysRoleController {

    private final SysRoleService sysRoleService;

    @GetMapping("/list")
    @Operation(summary = "获取角色列表")
    @RequirePermission("system:role:query")
    public Result<IPage<SysRole>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword) {

        Page<SysRole> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();

        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like(SysRole::getRoleName, keyword)
                    .or().like(SysRole::getRoleCode, keyword));
        }
        wrapper.orderByAsc(SysRole::getSortOrder);

        return Result.success(sysRoleService.page(page, wrapper));
    }

    @GetMapping("/all")
    @Operation(summary = "获取所有启用角色")
    @RequirePermission("system:role:query")
    public Result<List<SysRole>> getAll() {
        List<SysRole> list = sysRoleService.list(
                new LambdaQueryWrapper<SysRole>()
                        .eq(SysRole::getStatus, "ACTIVE")
                        .orderByAsc(SysRole::getSortOrder)
        );
        return Result.success(list);
    }

    @GetMapping("/{roleId}")
    @Operation(summary = "获取角色详情")
    @RequirePermission("system:role:query")
    public Result<SysRole> getById(@PathVariable Long roleId) {
        return Result.success(sysRoleService.getById(roleId));
    }

    @GetMapping("/{roleId}/menu-ids")
    @Operation(summary = "获取角色菜单ID列表")
    @RequirePermission("system:role:query")
    public Result<List<Long>> getMenuIdsByRoleId(@PathVariable Long roleId) {
        return Result.success(sysRoleService.getMenuIdsByRoleId(roleId));
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "获取用户的角色列表")
    @RequirePermission("system:role:query")
    public Result<List<SysRole>> getRolesByUserId(@PathVariable Long userId) {
        return Result.success(sysRoleService.getRolesByUserId(userId));
    }

    @PostMapping
    @Operation(summary = "新增角色")
    @RequirePermission("system:role:add")
    @OperationLog(module = "角色管理", operation = "新增角色")
    public Result<Void> add(@Valid @RequestBody SysRole role) {
        role.setIsSystem(false);
        sysRoleService.save(role);
        return Result.success();
    }

    @PutMapping
    @Operation(summary = "编辑角色")
    @RequirePermission("system:role:edit")
    @OperationLog(module = "角色管理", operation = "编辑角色")
    public Result<Void> edit(@Valid @RequestBody SysRole role) {
        sysRoleService.updateById(role);
        return Result.success();
    }

    @PutMapping("/{roleId}/menus")
    @Operation(summary = "分配角色菜单")
    @RequirePermission("system:role:edit")
    @OperationLog(module = "角色管理", operation = "分配菜单")
    public Result<Void> saveRoleMenus(
            @PathVariable Long roleId,
            @RequestBody List<Long> menuIds) {
        sysRoleService.saveRoleMenus(roleId, menuIds);
        return Result.success();
    }

    @PutMapping("/user/{userId}")
    @Operation(summary = "分配角色给用户")
    @RequirePermission("system:role:edit")
    @OperationLog(module = "角色管理", operation = "分配用户角色")
    public Result<Void> assignRolesToUser(
            @PathVariable Long userId,
            @RequestBody List<Long> roleIds) {
        sysRoleService.assignRolesToUser(userId, roleIds);
        return Result.success();
    }

    @DeleteMapping("/{roleId}")
    @Operation(summary = "删除角色")
    @RequirePermission("system:role:delete")
    @OperationLog(module = "角色管理", operation = "删除角色")
    public Result<Void> delete(@PathVariable Long roleId) {
        sysRoleService.deleteRole(roleId);
        return Result.success();
    }
}
