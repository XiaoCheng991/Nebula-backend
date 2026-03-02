package com.nebula.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.nebula.common.exception.BusinessException;
import com.nebula.config.result.ResultCode;
import com.nebula.model.dto.PasswordResetConfirmDTO;
import com.nebula.model.dto.PasswordResetRequestDTO;
import com.nebula.model.entity.PasswordResetToken;
import com.nebula.model.entity.SysUser;
import com.nebula.service.mapper.PasswordResetTokenMapper;
import com.nebula.service.mapper.SysUserMapper;
import com.nebula.service.service.EmailService;
import com.nebula.service.service.PasswordResetService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

/**
 * 密码重置服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PasswordResetServiceImpl implements PasswordResetService {

    private final PasswordResetTokenMapper tokenMapper;
    private final SysUserMapper userMapper;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    // 令牌有效期：1小时
    private static final int TOKEN_EXPIRY_HOURS = 1;
    // 每日最大请求次数
    private static final int MAX_DAILY_REQUESTS = 3;

    @Override
    @Transactional
    public void requestPasswordReset(PasswordResetRequestDTO requestDTO) {
        String email = requestDTO.getEmail();

        // 1. 查找用户
        LambdaQueryWrapper<SysUser> userWrapper = new LambdaQueryWrapper<>();
        userWrapper.eq(SysUser::getEmail, email);
        SysUser user = userMapper.selectOne(userWrapper);

        if (user == null) {
            // 为了安全，不暴露邮箱是否存在
            log.warn("密码重置请求: 邮箱 {} 不存在", email);
            return;
        }

        // 2. 检查当日请求次数
        LocalDateTime todayStart = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        LambdaQueryWrapper<PasswordResetToken> countWrapper = new LambdaQueryWrapper<>();
        countWrapper.eq(PasswordResetToken::getUserId, user.getId())
                .ge(PasswordResetToken::getCreateTime, todayStart);
        long todayCount = tokenMapper.selectCount(countWrapper);

        if (todayCount >= MAX_DAILY_REQUESTS) {
            throw new BusinessException(ResultCode.ERROR, "今日密码重置请求次数已达上限，请明天再试");
        }

        // 3. 使之前的令牌失效
        tokenMapper.expirePendingByUser(user.getId());

        // 4. 生成新令牌
        String token = UUID.randomUUID().toString();

        // 5. 获取请求信息
        HttpServletRequest request = getCurrentRequest();
        String ipAddress = request != null ? getClientIpAddress(request) : null;
        String userAgent = request != null ? request.getHeader("User-Agent") : null;

        // 6. 保存令牌
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setUserId(user.getId());
        resetToken.setEmail(email);
        resetToken.setToken(token);
        resetToken.setStatus("pending");
        resetToken.setExpiresAt(LocalDateTime.now().plusHours(TOKEN_EXPIRY_HOURS));
        resetToken.setIpAddress(ipAddress);
        resetToken.setUserAgent(userAgent);
        resetToken.setCreateTime(LocalDateTime.now());
        resetToken.setUpdateTime(LocalDateTime.now());
        resetToken.setDeleted(0);

        tokenMapper.insert(resetToken);

        // 7. 发送邮件
        emailService.sendPasswordResetEmail(email, token);

        log.info("密码重置邮件已发送: userId={}, email={}", user.getId(), email);
    }

    @Override
    public boolean validateToken(String token) {
        Optional<PasswordResetToken> tokenOpt = tokenMapper.findByToken(token);

        if (tokenOpt.isEmpty()) {
            return false;
        }

        PasswordResetToken resetToken = tokenOpt.get();

        // 检查状态
        if (!"pending".equals(resetToken.getStatus())) {
            return false;
        }

        // 检查是否过期
        if (resetToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            return false;
        }

        return true;
    }

    @Override
    @Transactional
    public void confirmPasswordReset(PasswordResetConfirmDTO confirmDTO) {
        String token = confirmDTO.getToken();
        String newPassword = confirmDTO.getNewPassword();
        String confirmPassword = confirmDTO.getConfirmPassword();

        // 1. 验证两次密码是否一致
        if (!newPassword.equals(confirmPassword)) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "两次输入的密码不一致");
        }

        // 2. 验证令牌
        Optional<PasswordResetToken> tokenOpt = tokenMapper.findByToken(token);
        if (tokenOpt.isEmpty()) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "无效的重置令牌");
        }

        PasswordResetToken resetToken = tokenOpt.get();

        // 3. 检查状态
        if (!"pending".equals(resetToken.getStatus())) {
            throw new BusinessException(ResultCode.ERROR, "该重置链接已" +
                    ("used".equals(resetToken.getStatus()) ? "使用" : "过期"));
        }

        // 4. 检查是否过期
        if (resetToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new BusinessException(ResultCode.ERROR, "重置链接已过期，请重新申请");
        }

        // 5. 更新用户密码
        SysUser user = userMapper.selectById(resetToken.getUserId());
        if (user == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "用户不存在");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setUpdateTime(java.time.LocalDateTime.now());
        userMapper.updateById(user);

        // 6. 标记令牌为已使用
        tokenMapper.markAsUsed(resetToken.getId());

        log.info("密码重置成功: userId={}, email={}", user.getId(), resetToken.getEmail());
    }

    @Override
    public void cleanupExpiredTokens() {
        LocalDateTime beforeTime = LocalDateTime.now().minusDays(7);
        int count = tokenMapper.cleanupOldRecords(beforeTime);
        log.info("清理过期密码重置令牌: {} 条", count);
    }

    /**
     * 获取当前HTTP请求
     */
    private HttpServletRequest getCurrentRequest() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            return attributes != null ? attributes.getRequest() : null;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取客户端真实IP地址
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String[] headerNames = {
                "X-Forwarded-For",
                "X-Real-IP",
                "Proxy-Client-IP",
                "WL-Proxy-Client-IP",
                "HTTP_X_FORWARDED_FOR",
                "HTTP_X_FORWARDED",
                "HTTP_X_CLUSTER_CLIENT_IP",
                "HTTP_CLIENT_IP",
                "HTTP_FORWARDED_FOR",
                "HTTP_FORWARDED",
                "HTTP_VIA",
                "REMOTE_ADDR"
        };

        for (String headerName : headerNames) {
            String ip = request.getHeader(headerName);
            if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
                // 如果包含多个IP，取第一个
                if (ip.contains(",")) {
                    ip = ip.split(",")[0].trim();
                }
                return ip;
            }
        }

        return request.getRemoteAddr();
    }
}
