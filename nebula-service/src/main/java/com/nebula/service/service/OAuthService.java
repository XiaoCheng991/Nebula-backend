package com.nebula.service.service;

import com.nebula.model.dto.GitHubOAuthDTO;
import com.nebula.model.vo.LoginVO;

/**
 * OAuth服务接口
 */
public interface OAuthService {

    /**
     * 处理GitHub OAuth回调
     *
     * @param oauthDTO OAuth回调DTO
     * @return 登录响应
     */
    LoginVO handleGitHubCallback(GitHubOAuthDTO oauthDTO);

    /**
     * 获取GitHub授权URL
     *
     * @param state 随机状态码，用于防止CSRF攻击
     * @return GitHub授权URL
     */
    String getGitHubAuthorizeUrl(String state);
}
