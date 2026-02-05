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
            LoginVO.UserInfo userInfo = authService.getUserInfo(token);

            // 从数据库获取完整的用户档案
            UserProfile profile = userProfileMapper.selectById(userInfo.getId());

            UserProfileDTO profileDTO = new UserProfileDTO();
            profileDTO.setUsername(profile != null ? profile.getUsername() : userInfo.getUsername());
            profileDTO.setDisplayName(profile != null ? profile.getDisplayName() : userInfo.getNickname());
            profileDTO.setAvatar(userInfo.getAvatar());
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
            LoginVO.UserInfo userInfo = authService.getUserInfo(token);

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
}
