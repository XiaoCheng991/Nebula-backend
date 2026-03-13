package com.nebula.api.controller.admin.system;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nebula.common.annotation.OperationLog;
import com.nebula.common.annotation.RequirePermission;
import com.nebula.common.constant.AdminConstants;
import com.nebula.config.result.Result;
import com.nebula.model.entity.SysUser;
import com.nebula.service.service.system.SysRoleService;
import com.nebula.service.service.system.SysUserAdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/system/user")
@RequiredArgsConstructor
@Tag(name = "后台用户管理", description = "后台用户管理接口")
@RequirePermission("system:user:view")
public class SysUserAdminController {

    private final SysUserAdminService sysUserAdminService;
    private final SysRoleService sysRoleService;

    @GetMapping("/list")
    @Operation(summary = "获取用户列表")
    @RequirePermission("system:user:query")
    public Result<IPage<SysUser>> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword) {

        Page<SysUser> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();

        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like(SysUser::getUsername, keyword)
                    .or().like(SysUser::getEmail, keyword)
                    .or().like(SysUser::getNickname, keyword));
        }
        wrapper.orderByDesc(SysUser::getCreateTime);

        return Result.success(sysUserAdminService.page(page, wrapper));
    }

    @GetMapping("/{userId}")
    @Operation(summary = "获取用户详情")
    @RequirePermission("system:user:query")
    public Result<SysUser> getById(@PathVariable Long userId) {
        return Result.success(sysUserAdminService.getById(userId));
    }

    @GetMapping("/current")
    @Operation(summary = "获取当前登录用户信息")
    @RequirePermission("system:user:query")
    public Result<SysUser> getCurrentUser() {
        StpUtil.checkLogin();
        Long userId = StpUtil.getLoginIdAsLong();
        return Result.success(sysUserAdminService.getById(userId));
    }

    @GetMapping("/{userId}/role-ids")
    @Operation(summary = "获取用户的角色ID列表")
    @RequirePermission("system:user:query")
    public Result<List<Long>> getUserRoleIds(@PathVariable Long userId) {
        List<Long> roleIds = sysRoleService.getRolesByUserId(userId)
                .stream()
                .map(r -> r.getId())
                .toList();
        return Result.success(roleIds);
    }

    @PutMapping
    @Operation(summary = "编辑用户")
    @RequirePermission("system:user:edit")
    @OperationLog(module = "用户管理", operation = "编辑用户")
    public Result<Void> edit(@RequestBody SysUser user) {
        user.setPassword(null);
        sysUserAdminService.updateById(user);
        return Result.success();
    }

    @PutMapping("/{userId}/status")
    @Operation(summary = "修改用户状态")
    @RequirePermission("system:user:edit")
    @OperationLog(module = "用户管理", operation = "修改用户状态")
    public Result<Void> updateStatus(
            @PathVariable Long userId,
            @RequestParam Integer status) {
        SysUser user = new SysUser();
        user.setId(userId);
        user.setAccountStatus(status);
        sysUserAdminService.updateById(user);
        return Result.success();
    }

    @PutMapping("/{userId}/roles")
    @Operation(summary = "分配角色给用户")
    @RequirePermission("system:user:edit")
    @OperationLog(module = "用户管理", operation = "分配用户角色")
    public Result<Void> assignRoles(
            @PathVariable Long userId,
            @RequestBody List<Long> roleIds) {
        sysRoleService.assignRolesToUser(userId, roleIds);
        return Result.success();
    }

    @DeleteMapping("/{userId}")
    @Operation(summary = "删除用户")
    @RequirePermission("system:user:delete")
    @OperationLog(module = "用户管理", operation = "删除用户")
    public Result<Void> delete(@PathVariable Long userId) {
        StpUtil.checkLogin();
        Long currentUserId = StpUtil.getLoginIdAsLong();
        if (isSuperAdmin(currentUserId) && currentUserId != null && currentUserId.equals(userId)) {
            return Result.error("不能删除当前登录的超级管理员账号");
        }
        sysUserAdminService.removeById(userId);
        return Result.success();
    }

    private boolean isSuperAdmin(Long userId) {
        if (userId == null) {
            return false;
        }
        return AdminConstants.SUPER_ADMIN_USER_ID.equals(userId) ||
                sysRoleService.hasRole(userId, AdminConstants.SUPER_ADMIN_ROLE_CODE);
    }
}
