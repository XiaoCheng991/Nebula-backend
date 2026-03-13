package com.nebula.api.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.nebula.config.result.Result;
import com.nebula.model.entity.SysUser;
import com.nebula.service.mapper.SysUserMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;

/**
 * 用户档案控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Tag(name = "用户档案", description = "用户档案相关接口")
public class ProfileController {

    private final SysUserMapper sysUserMapper;

    @GetMapping("/profile")
    @Operation(summary = "获取用户档案", description = "获取当前用户的完整档案信息")
    public Result<UserProfileVO> getUserProfile() {
        try {
            StpUtil.checkLogin();
            Long userId = StpUtil.getLoginIdAsLong();

            // 从数据库获取完整的用户信息
            SysUser sysUser = sysUserMapper.selectById(userId);
            if (sysUser == null) {
                return Result.error("用户不存在");
            }

            UserProfileVO profileVO = new UserProfileVO();
            profileVO.setUsername(sysUser.getUsername());
            profileVO.setDisplayName(sysUser.getNickname());
            profileVO.setAvatar(sysUser.getAvatarUrl());
            profileVO.setBio(sysUser.getBio());

            return Result.success(profileVO);
        } catch (Exception e) {
            log.error("获取用户档案失败", e);
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/profile")
    @Operation(summary = "更新用户档案", description = "更新当前用户的档案信息")
    public Result<Void> updateUserProfile(@Valid @RequestBody UserProfileVO profileVO) {
        try {
            StpUtil.checkLogin();
            Long userId = StpUtil.getLoginIdAsLong();

            // 查询现有用户
            SysUser sysUser = sysUserMapper.selectById(userId);
            if (sysUser == null) {
                return Result.error("用户不存在");
            }

            // 更新用户信息
            sysUser.setNickname(profileVO.getDisplayName());
            sysUser.setAvatarUrl(profileVO.getAvatar());
            sysUser.setBio(profileVO.getBio());
            sysUser.setUpdateTime(OffsetDateTime.now());
            sysUserMapper.updateById(sysUser);

            log.info("更新用户档案成功: userId={}", userId);
            return Result.success("更新成功");
        } catch (Exception e) {
            log.error("更新用户档案失败", e);
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/profile/avatar")
    @Operation(summary = "更新用户头像", description = "更新当前用户的头像信息")
    public Result<Void> updateUserAvatar(
            @RequestBody AvatarUpdateRequest avatarRequest
    ) {
        try {
            StpUtil.checkLogin();
            Long userId = StpUtil.getLoginIdAsLong();

            // 查询用户
            SysUser sysUser = sysUserMapper.selectById(userId);
            if (sysUser == null) {
                return Result.error("用户不存在");
            }

            // 更新用户头像字段
            sysUser.setAvatarUrl(avatarRequest.getAvatarUrl());
            sysUser.setUpdateTime(OffsetDateTime.now());
            sysUserMapper.updateById(sysUser);

            log.info("更新用户头像成功: userId={}, avatarUrl={}",
                    userId, avatarRequest.getAvatarUrl());
            return Result.success("头像更新成功");
        } catch (Exception e) {
            log.error("更新用户头像失败", e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 用户档案VO
     */
    @Data
    @io.swagger.v3.oas.annotations.media.Schema(description = "用户档案")
    public static class UserProfileVO {
        @io.swagger.v3.oas.annotations.media.Schema(description = "用户名")
        private String username;

        @io.swagger.v3.oas.annotations.media.Schema(description = "显示名称")
        private String displayName;

        @io.swagger.v3.oas.annotations.media.Schema(description = "头像URL")
        private String avatar;

        @io.swagger.v3.oas.annotations.media.Schema(description = "个人简介")
        private String bio;
    }

    /**
     * 头像更新请求DTO
     */
    @Data
    @io.swagger.v3.oas.annotations.media.Schema(description = "头像更新请求")
    public static class AvatarUpdateRequest {
        @io.swagger.v3.oas.annotations.media.Schema(
                description = "头像在MinIO中的URL",
                requiredMode = io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED
        )
        private String avatarUrl;
    }
}
