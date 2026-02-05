package com.nebula.api.controller;

import com.nebula.config.result.Result;
import com.nebula.model.dto.UserDTO;
import com.nebula.model.vo.UserVO;
import com.nebula.service.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "用户管理", description = "用户相关接口")
public class UserController {

    private final UserService userService;

    @GetMapping("/{id}")
    @Operation(summary = "根据ID查询用户")
    public Result<UserVO> getUser(@PathVariable Long id) {
        UserVO userVO = userService.getUserVO(id);
        return Result.success(userVO);
    }

    @GetMapping
    @Operation(summary = "查询所有用户")
    public Result<List<UserVO>> listUsers() {
        List<UserVO> users = userService.listUsers();
        return Result.success(users);
    }

    @PostMapping
    @Operation(summary = "创建用户")
    public Result<Long> createUser(@Validated @RequestBody UserDTO userDTO) {
        Long id = userService.createUser(userDTO);
        return Result.success("创建成功", id);
    }

    @PutMapping
    @Operation(summary = "更新用户")
    public Result<Boolean> updateUser(@Validated @RequestBody UserDTO userDTO) {
        Boolean success = userService.updateUser(userDTO);
        return Result.success("更新成功", success);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除用户")
    public Result<Boolean> deleteUser(@PathVariable Long id) {
        Boolean success = userService.deleteUser(id);
        return Result.success("删除成功", success);
    }
}
