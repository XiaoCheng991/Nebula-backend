package com.nebula.service.service;

import com.nebula.model.dto.LoginDTO;
import com.nebula.model.dto.RegisterDTO;
import com.nebula.model.vo.LoginVO;

/**
 * 认证服务接口
 */
public interface AuthService {

    /**
     * 用户登录
     */
    LoginVO login(LoginDTO loginDTO);

    /**
     * 用户注册
     */
    LoginVO register(RegisterDTO registerDTO);

    /**
     * 用户登出
     */
    void logout(Long userId);

    /**
     * 获取用户信息
     */
    LoginVO.UserInfo getUserInfo(Long userId);

    /**
     * 验证token
     */
    Long validateToken(String token);

    /**
     * 刷新token
     */
    LoginVO refreshToken(String refreshToken);
}
