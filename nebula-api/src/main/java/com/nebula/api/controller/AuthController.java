package com.nebula.api.controller;

import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import com.nebula.config.result.Result;
import com.nebula.model.dto.LoginDTO;
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
    public Result<SaResult> login(@Valid @RequestBody LoginDTO loginDTO) {
        SaResult loginVO = authService.login(loginDTO);
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
    public Result<Void> logout() {
        StpUtil.checkLogin();
        Long userId = StpUtil.getLoginIdAsLong();
        authService.logout(userId);
        return Result.success("登出成功");
    }

    @GetMapping("/user-info")
    @Operation(summary = "获取当前用户信息")
    public Result<LoginVO.UserInfo> getUserInfo() {
        StpUtil.checkLogin();
        Long userId = StpUtil.getLoginIdAsLong();
        LoginVO.UserInfo userInfo = authService.getUserInfo(userId);
        return Result.success(userInfo);
    }

}
