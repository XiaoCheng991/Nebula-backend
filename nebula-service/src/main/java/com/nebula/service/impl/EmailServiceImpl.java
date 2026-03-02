package com.nebula.service.impl;

import com.nebula.service.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import jakarta.mail.internet.MimeMessage;

/**
 * 邮件服务实现
 */
@Slf4j
@Service
public class EmailServiceImpl implements EmailService {

    @Resource
    private JavaMailSender mailSender;

    @Value("${spring.mail.username:}")
    private String fromEmail;

    @Value("${app.email.enabled:false}")
    private boolean emailEnabled;

    @Value("${app.frontend.url:http://localhost:3000}")
    private String frontendUrl;

    @Override
    public void sendVerificationEmail(String to, String subject, String token, String type) {
        if (!emailEnabled) {
            log.info("邮件功能未启用，验证令牌: {}，收件人: {}，类型: {}", token, to, type);
            return;
        }

        String verifyUrl = frontendUrl + "/verify-email?token=" + token;
        String typeText = switch (type) {
            case "registration" -> "注册";
            case "email_change" -> "邮箱修改";
            case "password_reset" -> "密码重置";
            default -> "验证";
        };

        String html = String.format("""
            <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #e0e0e0; border-radius: 8px;">
                <h2 style="color: #333; text-align: center;">%s验证</h2>
                <p style="color: #666; line-height: 1.6;">您好，</p>
                <p style="color: #666; line-height: 1.6;">您正在进行%s验证，请点击下方按钮完成验证：</p>
                <div style="text-align: center; margin: 30px 0;">
                    <a href="%s" style="display: inline-block; padding: 12px 32px; background-color: #4CAF50; color: white; text-decoration: none; border-radius: 4px; font-size: 16px;">验证%s</a>
                </div>
                <p style="color: #999; font-size: 12px; line-height: 1.6;">如果按钮无法点击，请复制以下链接到浏览器打开：</p>
                <p style="color: #4CAF50; font-size: 12px; word-break: break-all;">%s</p>
                <p style="color: #999; font-size: 12px; line-height: 1.6; margin-top: 20px;">此链接24小时内有效，请勿泄露给他人。</p>
                <hr style="border: none; border-top: 1px solid #e0e0e0; margin: 20px 0;">
                <p style="color: #999; font-size: 12px; text-align: center;">如非本人操作，请忽略此邮件。</p>
            </div>
            """, typeText, typeText, verifyUrl, typeText, verifyUrl);

        sendHtmlEmail(to, subject != null ? subject : "【NebulaHub】" + typeText + "验证", html);
    }

    @Override
    public void sendPasswordResetEmail(String to, String token) {
        if (!emailEnabled) {
            log.info("邮件功能未启用，密码重置令牌: {}，收件人: {}", token, to);
            return;
        }

        String resetUrl = frontendUrl + "/reset-password?token=" + token;

        String html = String.format("""
            <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #e0e0e0; border-radius: 8px;">
                <h2 style="color: #333; text-align: center;">密码重置</h2>
                <p style="color: #666; line-height: 1.6;">您好，</p>
                <p style="color: #666; line-height: 1.6;">您申请了密码重置，请点击下方按钮重置密码：</p>
                <div style="text-align: center; margin: 30px 0;">
                    <a href="%s" style="display: inline-block; padding: 12px 32px; background-color: #FF6B6B; color: white; text-decoration: none; border-radius: 4px; font-size: 16px;">重置密码</a>
                </div>
                <p style="color: #999; font-size: 12px; line-height: 1.6;">如果按钮无法点击，请复制以下链接到浏览器打开：</p>
                <p style="color: #FF6B6B; font-size: 12px; word-break: break-all;">%s</p>
                <p style="color: #999; font-size: 12px; line-height: 1.6; margin-top: 20px;">此链接1小时内有效，请勿泄露给他人。</p>
                <p style="color: #FF6B6B; font-size: 12px; line-height: 1.6; margin-top: 10px;">如非本人操作，请忽略此邮件，您的账户仍然安全。</p>
            </div>
            """, resetUrl, resetUrl);

        sendHtmlEmail(to, "【NebulaHub】密码重置", html);
    }

    @Override
    public void sendSimpleEmail(String to, String subject, String content) {
        if (!emailEnabled) {
            log.info("邮件功能未启用，收件人: {}，主题: {}", to, subject);
            return;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(content);
            mailSender.send(message);
            log.info("邮件发送成功: {}", to);
        } catch (Exception e) {
            log.error("邮件发送失败: {}", to, e);
            throw new RuntimeException("邮件发送失败: " + e.getMessage());
        }
    }

    @Override
    public void sendHtmlEmail(String to, String subject, String html) {
        if (!emailEnabled) {
            log.info("邮件功能未启用(HTML)，收件人: {}，主题: {}", to, subject);
            return;
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(html, true);
            mailSender.send(message);
            log.info("HTML邮件发送成功: {}", to);
        } catch (Exception e) {
            log.error("HTML邮件发送失败: {}", to, e);
            throw new RuntimeException("邮件发送失败: " + e.getMessage());
        }
    }
}
