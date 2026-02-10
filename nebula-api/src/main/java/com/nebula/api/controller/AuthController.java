package com.nebula.api.controller;

import com.nebula.config.result.Result;
import com.nebula.model.dto.LoginDTO;
import com.nebula.model.dto.RefreshTokenDTO;
import com.nebula.model.dto.RegisterDTO;
import com.nebula.model.vo.LoginVO;
import com.nebula.service.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "认证管理", description = "登录、注册等认证相关接口")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "用户登录")
    public Result<LoginVO> login(@Valid @RequestBody LoginDTO loginDTO) {
        LoginVO loginVO = authService.login(loginDTO);
        return Result.success("登录成功", loginVO);
    }

    @PostMapping("/register")
    @Operation(summary = "用户注册")
    public Result<LoginVO> register(@Valid @RequestBody RegisterDTO registerDTO) {
        LoginVO loginVO = authService.register(registerDTO);
        return Result.success("注册成功", loginVO);
    }

    @PostMapping("/logout")
    @Operation(summary = "用户登出")
    public Result<Void> logout(@RequestHeader("Authorization") String token) {
        Long userId = authService.validateToken(token);
        authService.logout(userId);
        return Result.success("登出成功");
    }

    @GetMapping("/user-info")
    @Operation(summary = "获取当前用户信息")
    public Result<LoginVO.UserInfo> getUserInfo(@RequestHeader("Authorization") String token) {
        // 先验证 token，获取 userId，再根据 userId 查询用户信息
        Long userId = authService.validateToken(token);
        LoginVO.UserInfo userInfo = authService.getUserInfo(userId);
        return Result.success(userInfo);
    }

    @PostMapping("/refresh-token")
    @Operation(summary = "刷新Token", description = "使用refresh token获取新的access token")
    public Result<LoginVO> refreshToken(@Valid @RequestBody RefreshTokenDTO refreshTokenDTO) {
        LoginVO loginVO = authService.refreshToken(refreshTokenDTO.getRefreshToken());
        return Result.success("刷新成功", loginVO);
    }
}
