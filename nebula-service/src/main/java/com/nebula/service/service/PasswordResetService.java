package com.nebula.service.service;

import com.nebula.model.dto.PasswordResetConfirmDTO;
import com.nebula.model.dto.PasswordResetRequestDTO;

/**
 * 密码重置服务接口
 */
public interface PasswordResetService {

    /**
     * 请求密码重置
     * 发送重置邮件到用户邮箱
     *
     * @param requestDTO 重置请求
     */
    void requestPasswordReset(PasswordResetRequestDTO requestDTO);

    /**
     * 验证重置令牌是否有效
     *
     * @param token 重置令牌
     * @return 是否有效
     */
    boolean validateToken(String token);

    /**
     * 确认密码重置
     *
     * @param confirmDTO 确认信息
     */
    void confirmPasswordReset(PasswordResetConfirmDTO confirmDTO);

    /**
     * 清理过期的重置令牌
     */
    void cleanupExpiredTokens();
}
