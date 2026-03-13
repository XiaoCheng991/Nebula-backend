package com.nebula.service.service;

import cn.dev33.satoken.util.SaResult;
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
    SaResult login(LoginDTO loginDTO);

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
}
