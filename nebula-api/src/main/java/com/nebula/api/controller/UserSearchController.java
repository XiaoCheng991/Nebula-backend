package com.nebula.api.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.nebula.config.result.Result;
import com.nebula.model.vo.UserVO;
import com.nebula.service.service.UserSearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户搜索控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Validated
@Tag(name = "用户搜索", description = "用户搜索相关接口")
public class UserSearchController {

    private final UserSearchService userSearchService;

    @GetMapping("/search")
    @Operation(summary = "搜索用户", description = "根据关键词搜索用户，支持用户名、昵称、邮箱搜索",
            security = @SecurityRequirement(name = "Authorization"))
    public Result<IPage<UserVO>> searchUsers(
            @Parameter(description = "搜索关键词") @RequestParam(required = false) String keyword,
            @Parameter(description = "在线状态(online-在线,offline-离线,busy-忙碌,away-离开)") @RequestParam(required = false) String onlineStatus,
            @Parameter(description = "页码", example = "1") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小", example = "20") @RequestParam(defaultValue = "20") Integer pageSize) {

        IPage<UserVO> result = userSearchService.searchUsers(keyword, onlineStatus, pageNum, pageSize);
        return Result.success(result);
    }

    @GetMapping("/suggest")
    @Operation(summary = "用户搜索建议", description = "根据关键词返回搜索建议",
            security = @SecurityRequirement(name = "Authorization"))
    public Result<List<UserVO>> searchSuggestions(
            @Parameter(description = "搜索关键词", required = true) @RequestParam String keyword,
            @Parameter(description = "返回数量限制", example = "10") @RequestParam(defaultValue = "10") Integer limit) {

        List<UserVO> users = userSearchService.searchUsersByKeyword(keyword, limit);
        return Result.success(users);
    }

    @GetMapping("/{userId}")
    @Operation(summary = "获取用户信息", description = "根据用户ID获取用户详细信息",
            security = @SecurityRequirement(name = "Authorization"))
    public Result<UserVO> getUserById(
            @Parameter(description = "用户ID", required = true) @PathVariable Long userId) {

        UserVO user = userSearchService.getUserById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }
        return Result.success(user);
    }

    @GetMapping("/username/{username}")
    @Operation(summary = "根据用户名获取用户信息", description = "根据用户名获取用户详细信息",
            security = @SecurityRequirement(name = "Authorization"))
    public Result<UserVO> getUserByUsername(
            @Parameter(description = "用户名", required = true) @PathVariable String username) {

        UserVO user = userSearchService.getUserByUsername(username);
        if (user == null) {
            return Result.error("用户不存在");
        }
        return Result.success(user);
    }

    @GetMapping("/current")
    @Operation(summary = "获取当前用户信息", description = "获取当前登录用户的详细信息",
            security = @SecurityRequirement(name = "Authorization"))
    public Result<UserVO> getCurrentUser() {
        StpUtil.checkLogin();
        Long userId = StpUtil.getLoginIdAsLong();

        UserVO user = userSearchService.getUserById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }
        return Result.success(user);
    }
}
