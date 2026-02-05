package com.nebula.service.service;

import com.nebula.model.dto.GitHubOAuthDTO;
import com.nebula.model.vo.LoginVO;

/**
 * OAuth服务接口
 */
public interface OAuthService {

    /**
     * 处理GitHub OAuth回调
     */
    LoginVO handleGitHubCallback(GitHubOAuthDTO oauthDTO);

    /**
     * 获取GitHub授权URL
     */
    String getGitHubAuthorizeUrl();
}
