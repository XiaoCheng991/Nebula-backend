package com.nebula.api.controller;

import com.nebula.config.result.Result;
import com.nebula.model.dto.UserProfileDTO;
import com.nebula.model.entity.UserProfile;
import com.nebula.model.vo.LoginVO;
import com.nebula.service.mapper.UserProfileMapper;
import com.nebula.service.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 用户档案控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Tag(name = "用户档案", description = "用户档案相关接口")
public class ProfileController {

    private final AuthService authService;
    private final UserProfileMapper userProfileMapper;

    @GetMapping("/profile")
    @Operation(summary = "获取用户档案", description = "获取当前用户的完整档案信息")
    public Result<UserProfileDTO> getUserProfile(@RequestHeader("Authorization") String token) {
        try {
            Long userId = authService.validateToken(token);
            LoginVO.UserInfo userInfo = authService.getUserInfo(userId);

            // 从数据库获取完整的用户档案
            UserProfile profile = userProfileMapper.selectById(userInfo.getId());

            UserProfileDTO profileDTO = new UserProfileDTO();
            profileDTO.setUsername(profile != null ? profile.getUsername() : userInfo.getUsername());
            profileDTO.setDisplayName(profile != null ? profile.getDisplayName() : userInfo.getNickname());
            // 优先从数据库的 UserProfile 获取头像URL，如果没有则使用 userInfo 中的旧数据
            profileDTO.setAvatar(profile != null && profile.getAvatarUrl() != null ? profile.getAvatarUrl() : null);
            // 确保bio不会是null，使用空字符串作为默认值
            String bio = profile != null ? profile.getBio() : "";
            profileDTO.setBio(bio != null ? bio : "");

            return Result.success(profileDTO);
        } catch (Exception e) {
            log.error("获取用户档案失败", e);
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/profile")
    @Operation(summary = "更新用户档案", description = "更新当前用户的档案信息")
    public Result<Void> updateUserProfile(
            @RequestHeader("Authorization") String token,
            @Valid @RequestBody UserProfileDTO profileDTO
    ) {
        try {
            // 从token获取用户ID
            Long userId = authService.validateToken(token);
            LoginVO.UserInfo userInfo = authService.getUserInfo(userId);

            // 查询现有档案
            UserProfile profile = userProfileMapper.selectById(userInfo.getId());
            if (profile == null) {
                // 如果档案不存在，创建新的
                profile = new UserProfile();
                profile.setId(userInfo.getId());
                profile.setUsername(profileDTO.getUsername());
                profile.setDisplayName(profileDTO.getDisplayName());
                profile.setAvatarUrl(profileDTO.getAvatar());
                profile.setBio(profileDTO.getBio());
                profile.setStatus("offline");
                profile.setLastSeenAt(java.time.LocalDateTime.now());
                profile.setCreateTime(java.time.LocalDateTime.now());
                profile.setUpdateTime(java.time.LocalDateTime.now());
                profile.setDeleted(0);
                userProfileMapper.insert(profile);
            } else {
                // 更新现有档案
                profile.setUsername(profileDTO.getUsername());
                profile.setDisplayName(profileDTO.getDisplayName());
                profile.setAvatarUrl(profileDTO.getAvatar());
                profile.setBio(profileDTO.getBio());
                profile.setUpdateTime(java.time.LocalDateTime.now());
                userProfileMapper.updateById(profile);
            }

            log.info("更新用户档案成功: userId={}", userInfo.getId());
            return Result.success("更新成功");
        } catch (Exception e) {
            log.error("更新用户档案失败", e);
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/profile/avatar")
    @Operation(summary = "更新用户头像", description = "更新当前用户的头像信息")
    public Result<Void> updateUserAvatar(
            @RequestHeader("Authorization") String token,
            @RequestBody AvatarUpdateRequest avatarRequest
    ) {
        try {
            // 从token获取用户ID
            Long userId = authService.validateToken(token);
            LoginVO.UserInfo userInfo = authService.getUserInfo(userId);

            // 查询现有档案
            UserProfile profile = userProfileMapper.selectById(userInfo.getId());
            if (profile == null) {
                // 如果档案不存在，创建新的
                profile = new UserProfile();
                profile.setId(userInfo.getId());
                profile.setUsername(userInfo.getUsername());
                profile.setDisplayName(userInfo.getNickname());
                profile.setAvatarName(avatarRequest.getAvatarName());
                profile.setAvatarUrl(avatarRequest.getAvatarUrl());
                profile.setAvatarSize(avatarRequest.getAvatarSize());
                profile.setStatus("offline");
                profile.setLastSeenAt(java.time.LocalDateTime.now());
                profile.setCreateTime(java.time.LocalDateTime.now());
                profile.setUpdateTime(java.time.LocalDateTime.now());
                profile.setDeleted(0);
                userProfileMapper.insert(profile);
            } else {
                // 更新现有档案的头像字段
                profile.setAvatarName(avatarRequest.getAvatarName());
                profile.setAvatarUrl(avatarRequest.getAvatarUrl());
                profile.setAvatarSize(avatarRequest.getAvatarSize());
                profile.setUpdateTime(java.time.LocalDateTime.now());
                userProfileMapper.updateById(profile);
            }

            log.info("更新用户头像成功: userId={}, avatarName={}, avatarSize={}",
                    userInfo.getId(), avatarRequest.getAvatarName(), avatarRequest.getAvatarSize());
            return Result.success("头像更新成功");
        } catch (Exception e) {
            log.error("更新用户头像失败", e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 头像更新请求DTO
     */
    @Getter
    @io.swagger.v3.oas.annotations.media.Schema(description = "头像更新请求")
    public static class AvatarUpdateRequest {
        @io.swagger.v3.oas.annotations.media.Schema(description = "头像文件名称", required = true)
        private String avatarName;

        @io.swagger.v3.oas.annotations.media.Schema(description = "头像在MinIO中的URL", required = true)
        private String avatarUrl;

        @io.swagger.v3.oas.annotations.media.Schema(description = "头像文件大小（字节）", required = true)
        private Long avatarSize;

    }
}
