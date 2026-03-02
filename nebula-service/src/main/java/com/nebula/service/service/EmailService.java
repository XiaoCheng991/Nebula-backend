package com.nebula.service.service;

/**
 * 邮件服务接口
 */
public interface EmailService {

    /**
     * 发送邮箱验证邮件
     *
     * @param to      收件人邮箱
     * @param subject 邮件主题
     * @param token   验证令牌
     * @param type    验证类型
     */
    void sendVerificationEmail(String to, String subject, String token, String type);

    /**
     * 发送密码重置邮件
     *
     * @param to    收件人邮箱
     * @param token 重置令牌
     */
    void sendPasswordResetEmail(String to, String token);

    /**
     * 发送普通文本邮件
     *
     * @param to      收件人
     * @param subject 主题
     * @param content 内容
     */
    void sendSimpleEmail(String to, String subject, String content);

    /**
     * 发送HTML邮件
     *
     * @param to      收件人
     * @param subject 主题
     * @param html    HTML内容
     */
    void sendHtmlEmail(String to, String subject, String html);
}
