package com.nebula.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.nebula.common.exception.BusinessException;
import com.nebula.config.result.ResultCode;
import com.nebula.model.dto.EmailVerificationDTO;
import com.nebula.model.dto.VerifyEmailDTO;
import com.nebula.model.entity.EmailVerification;
import com.nebula.model.entity.SysUser;
import com.nebula.service.mapper.EmailVerificationMapper;
import com.nebula.service.mapper.SysUserMapper;
import com.nebula.service.service.EmailService;
import com.nebula.service.service.EmailVerificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

/**
 * 邮箱验证服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmailVerificationServiceImpl implements EmailVerificationService {

    private final EmailVerificationMapper verificationMapper;
    private final SysUserMapper userMapper;
    private final EmailService emailService;

    // 令牌有效期：24小时
    private static final int TOKEN_EXPIRY_HOURS = 24;
    // 每日最大发送次数
    private static final int MAX_DAILY_SENDS = 5;

    @Override
    @Transactional
    public void sendVerificationEmail(EmailVerificationDTO verificationDTO) {
        String email = verificationDTO.getEmail();
        String type = verificationDTO.getType() != null ? verificationDTO.getType() : "registration";

        // 1. 查找用户
        LambdaQueryWrapper<SysUser> userWrapper = new LambdaQueryWrapper<>();
        userWrapper.eq(SysUser::getEmail, email);
        SysUser user = userMapper.selectOne(userWrapper);

        if (user == null) {
            // 为了安全，不暴露邮箱是否存在
            log.warn("邮箱验证请求: 邮箱 {} 不存在", email);
            return;
        }

        // 2. 检查当日发送次数
        OffsetDateTime todayStart = OffsetDateTime.now().withHour(0).withMinute(0).withSecond(0);
        LambdaQueryWrapper<EmailVerification> countWrapper = new LambdaQueryWrapper<>();
        countWrapper.eq(EmailVerification::getUserId, user.getId())
                .eq(EmailVerification::getType, type)
                .ge(EmailVerification::getCreateTime, todayStart);
        long todayCount = verificationMapper.selectCount(countWrapper);

        if (todayCount >= MAX_DAILY_SENDS) {
            throw new BusinessException(ResultCode.ERROR,
                    "今日验证邮件发送次数已达上限，请明天再试");
        }

        // 3. 使之前的验证记录过期
        verificationMapper.expirePendingByUserAndType(user.getId(), type);

        // 4. 生成新令牌
        String token = UUID.randomUUID().toString();

        // 5. 保存验证记录
        EmailVerification verification = new EmailVerification();
        verification.setUserId(user.getId());
        verification.setEmail(email);
        verification.setToken(token);
        verification.setType(type);
        verification.setStatus("pending");
        verification.setExpiresAt(OffsetDateTime.now().plusHours(TOKEN_EXPIRY_HOURS));
        verification.setCreateTime(OffsetDateTime.now());
        verification.setUpdateTime(OffsetDateTime.now());
        verification.setDeleted(0);

        verificationMapper.insert(verification);

        // 6. 发送邮件
        String subject = switch (type) {
            case "registration" -> "【NebulaHub】邮箱注册验证";
            case "email_change" -> "【NebulaHub】邮箱修改验证";
            case "password_reset" -> "【NebulaHub】密码重置验证";
            default -> "【NebulaHub】邮箱验证";
        };

        emailService.sendVerificationEmail(email, subject, token, type);

        log.info("验证邮件已发送: userId={}, email={}, type={}", user.getId(), email, type);
    }

    @Override
    @Transactional
    public boolean verifyEmail(VerifyEmailDTO verifyDTO) {
        String token = verifyDTO.getToken();

        Optional<EmailVerification> verificationOpt = verificationMapper.findByToken(token);

        if (verificationOpt.isEmpty()) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "无效的验证链接");
        }

        EmailVerification verification = verificationOpt.get();

        // 检查状态
        if (!"pending".equals(verification.getStatus())) {
            String statusText = switch (verification.getStatus()) {
                case "verified" -> "已验证";
                case "expired" -> "已过期";
                default -> "无效";
            };
            throw new BusinessException(ResultCode.ERROR, "该验证链接已" + statusText);
        }

        // 检查是否过期
        if (verification.getExpiresAt().isBefore(OffsetDateTime.now())) {
            // 更新状态为过期
            verification.setStatus("expired");
            verification.setUpdateTime(OffsetDateTime.now());
            verificationMapper.updateById(verification);
            throw new BusinessException(ResultCode.ERROR, "验证链接已过期，请重新申请");
        }

        // 更新验证状态
        verification.setStatus("verified");
        verification.setVerifiedAt(OffsetDateTime.now());
        verification.setUpdateTime(OffsetDateTime.now());
        verificationMapper.updateById(verification);

        // 如果是注册验证，更新用户邮箱验证状态
        if ("registration".equals(verification.getType())) {
            SysUser user = userMapper.selectById(verification.getUserId());
            if (user != null) {
                // 可以添加邮箱验证状态的字段
                // user.setEmailVerified(true);
                // userMapper.updateById(user);
            }
        }

        log.info("邮箱验证成功: userId={}, email={}", verification.getUserId(), verification.getEmail());
        return true;
    }

    @Override
    public boolean isEmailVerified(Long userId) {
        LambdaQueryWrapper<EmailVerification> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EmailVerification::getUserId, userId)
                .eq(EmailVerification::getStatus, "verified")
                .eq(EmailVerification::getType, "registration")
                .eq(EmailVerification::getDeleted, 0);
        return verificationMapper.selectCount(wrapper) > 0;
    }

    @Override
    @Transactional
    public void resendVerificationEmail(String email) {
        EmailVerificationDTO dto = new EmailVerificationDTO();
        dto.setEmail(email);
        dto.setType("registration");
        sendVerificationEmail(dto);
    }

    @Override
    public void cleanupExpiredVerifications() {
        OffsetDateTime beforeTime = OffsetDateTime.now().minusDays(7);
        int count = verificationMapper.cleanupExpiredRecords(beforeTime);
        log.info("清理过期邮箱验证记录: {} 条", count);
    }
}
