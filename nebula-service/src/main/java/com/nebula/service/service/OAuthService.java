package com.nebula.service.service;

import com.nebula.model.dto.GitHubOAuthConfirmDTO;
import com.nebula.model.dto.GitHubOAuthDTO;
import com.nebula.model.vo.GitHubOAuthConfirmVO;
import com.nebula.model.vo.LoginVO;

/**
 * OAuth服务接口
 */
public interface OAuthService {

    /**
     * 处理GitHub OAuth回调 - 返回确认信息（不直接登录）
     *
     * @param oauthDTO OAuth回调DTO
     * @return GitHub用户确认信息
     */
    GitHubOAuthConfirmVO handleGitHubCallbackForConfirm(GitHubOAuthDTO oauthDTO);

    /**
     * 用临时token获取GitHub用户信息
     *
     * @param tempToken 临时token
     * @return GitHub用户确认信息
     */
    GitHubOAuthConfirmVO getGitHubUserInfo(String tempToken);

    /**
     * 确认GitHub OAuth登录
     *
     * @param confirmDTO 确认信息DTO
     * @return 登录响应
     */
    LoginVO confirmGitHubLogin(GitHubOAuthConfirmDTO confirmDTO);

    /**
     * 老用户直接登录（无需确认）
     *
     * @param tempToken 临时token
     * @return 登录响应
     */
    LoginVO loginGitHubExistingUser(String tempToken);

    /**
     * 获取GitHub授权URL
     *
     * @param state 随机状态码，用于防止CSRF攻击
     * @return GitHub授权URL
     */
    String getGitHubAuthorizeUrl(String state);
}
