package com.nebula.service.service;

import com.nebula.model.dto.EmailVerificationDTO;
import com.nebula.model.dto.VerifyEmailDTO;

/**
 * 邮箱验证服务接口
 */
public interface EmailVerificationService {

    /**
     * 发送邮箱验证邮件
     *
     * @param verificationDTO 验证请求
     */
    void sendVerificationEmail(EmailVerificationDTO verificationDTO);

    /**
     * 验证邮箱
     *
     * @param verifyDTO 验证信息
     * @return 是否验证成功
     */
    boolean verifyEmail(VerifyEmailDTO verifyDTO);

    /**
     * 检查邮箱是否已验证
     *
     * @param userId 用户ID
     * @return 是否已验证
     */
    boolean isEmailVerified(Long userId);

    /**
     * 重新发送验证邮件
     *
     * @param email 邮箱地址
     */
    void resendVerificationEmail(String email);

    /**
     * 清理过期的验证记录
     */
    void cleanupExpiredVerifications();
}
