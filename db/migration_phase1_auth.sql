-- ============================================
-- Phase 1: 基础设施与核心认证 - 数据库迁移
-- 包含: 邮箱验证、密码重置、登录日志
-- 注意: 使用逻辑外键，不使用物理外键
-- ============================================

-- ============================================
-- 1. 邮箱验证令牌表
-- ============================================
CREATE TABLE IF NOT EXISTS email_verifications (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    email VARCHAR(100) NOT NULL,
    token VARCHAR(255) NOT NULL,
    type VARCHAR(20) NOT NULL CHECK (type IN ('registration', 'email_change', 'password_reset')),
    status VARCHAR(20) DEFAULT 'pending' CHECK (status IN ('pending', 'verified', 'expired')),
    verified_at TIMESTAMP WITH TIME ZONE,
    expires_at TIMESTAMP WITH TIME ZONE NOT NULL,
    create_time TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    update_time TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    deleted INTEGER DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_email_verifications_user ON email_verifications(user_id);
CREATE INDEX IF NOT EXISTS idx_email_verifications_token ON email_verifications(token);
CREATE INDEX IF NOT EXISTS idx_email_verifications_status ON email_verifications(status);
CREATE INDEX IF NOT EXISTS idx_email_verifications_expires ON email_verifications(expires_at);

COMMENT ON TABLE email_verifications IS '邮箱验证令牌表';
COMMENT ON COLUMN email_verifications.id IS '验证ID';
COMMENT ON COLUMN email_verifications.user_id IS '用户ID（逻辑外键）';
COMMENT ON COLUMN email_verifications.email IS '邮箱地址';
COMMENT ON COLUMN email_verifications.token IS '验证令牌';
COMMENT ON COLUMN email_verifications.type IS '验证类型(registration-注册验证,email_change-邮箱修改,password_reset-密码重置)';
COMMENT ON COLUMN email_verifications.status IS '状态(pending-待验证,verified-已验证,expired-已过期)';
COMMENT ON COLUMN email_verifications.verified_at IS '验证时间';
COMMENT ON COLUMN email_verifications.expires_at IS '过期时间';

DROP TRIGGER IF EXISTS update_email_verifications_updated_at ON email_verifications;
CREATE TRIGGER update_email_verifications_updated_at
    BEFORE UPDATE ON email_verifications
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- ============================================
-- 2. 密码重置令牌表
-- ============================================
CREATE TABLE IF NOT EXISTS password_reset_tokens (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    email VARCHAR(100) NOT NULL,
    token VARCHAR(255) NOT NULL,
    status VARCHAR(20) DEFAULT 'pending' CHECK (status IN ('pending', 'used', 'expired')),
    used_at TIMESTAMP WITH TIME ZONE,
    expires_at TIMESTAMP WITH TIME ZONE NOT NULL,
    ip_address VARCHAR(45),
    user_agent TEXT,
    create_time TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    update_time TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    deleted INTEGER DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_password_reset_user ON password_reset_tokens(user_id);
CREATE INDEX IF NOT EXISTS idx_password_reset_token ON password_reset_tokens(token);
CREATE INDEX IF NOT EXISTS idx_password_reset_status ON password_reset_tokens(status);
CREATE INDEX IF NOT EXISTS idx_password_reset_expires ON password_reset_tokens(expires_at);

COMMENT ON TABLE password_reset_tokens IS '密码重置令牌表';
COMMENT ON COLUMN password_reset_tokens.id IS '令牌ID';
COMMENT ON COLUMN password_reset_tokens.user_id IS '用户ID（逻辑外键）';
COMMENT ON COLUMN password_reset_tokens.email IS '邮箱地址';
COMMENT ON COLUMN password_reset_tokens.token IS '重置令牌';
COMMENT ON COLUMN password_reset_tokens.status IS '状态(pending-待使用,used-已使用,expired-已过期)';
COMMENT ON COLUMN password_reset_tokens.used_at IS '使用时间';
COMMENT ON COLUMN password_reset_tokens.expires_at IS '过期时间';
COMMENT ON COLUMN password_reset_tokens.ip_address IS '请求IP';
COMMENT ON COLUMN password_reset_tokens.user_agent IS 'User-Agent';

DROP TRIGGER IF EXISTS update_password_reset_updated_at ON password_reset_tokens;
CREATE TRIGGER update_password_reset_updated_at
    BEFORE UPDATE ON password_reset_tokens
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- ============================================
-- 3. 登录日志表
-- ============================================
CREATE TABLE IF NOT EXISTS login_logs (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT,
    username VARCHAR(50),
    login_type VARCHAR(20) NOT NULL CHECK (login_type IN ('password', 'github', 'google', 'wechat')),
    status VARCHAR(20) NOT NULL CHECK (status IN ('success', 'failed', 'locked')),
    fail_reason VARCHAR(255),
    ip_address VARCHAR(45) NOT NULL,
    ip_location VARCHAR(100),
    user_agent TEXT,
    browser VARCHAR(100),
    os VARCHAR(100),
    device VARCHAR(100),
    login_at TIMESTAMP WITH TIME ZONE NOT NULL,
    logout_at TIMESTAMP WITH TIME ZONE,
    token_id VARCHAR(255),
    create_time TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    update_time TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    deleted INTEGER DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_login_logs_user ON login_logs(user_id);
CREATE INDEX IF NOT EXISTS idx_login_logs_username ON login_logs(username);
CREATE INDEX IF NOT EXISTS idx_login_logs_type ON login_logs(login_type);
CREATE INDEX IF NOT EXISTS idx_login_logs_status ON login_logs(status);
CREATE INDEX IF NOT EXISTS idx_login_logs_ip ON login_logs(ip_address);
CREATE INDEX IF NOT EXISTS idx_login_logs_login_at ON login_logs(login_at DESC);

COMMENT ON TABLE login_logs IS '登录日志表';
COMMENT ON COLUMN login_logs.id IS '日志ID';
COMMENT ON COLUMN login_logs.user_id IS '用户ID（逻辑外键）';
COMMENT ON COLUMN login_logs.username IS '用户名';
COMMENT ON COLUMN login_logs.login_type IS '登录类型(password-密码,github-GitHub,google-谷歌,wechat-微信)';
COMMENT ON COLUMN login_logs.status IS '状态(success-成功,failed-失败,locked-锁定)';
COMMENT ON COLUMN login_logs.fail_reason IS '失败原因';
COMMENT ON COLUMN login_logs.ip_address IS 'IP地址';
COMMENT ON COLUMN login_logs.ip_location IS 'IP归属地';
COMMENT ON COLUMN login_logs.user_agent IS 'User-Agent';
COMMENT ON COLUMN login_logs.browser IS '浏览器';
COMMENT ON COLUMN login_logs.os IS '操作系统';
COMMENT ON COLUMN login_logs.device IS '设备';
COMMENT ON COLUMN login_logs.login_at IS '登录时间';
COMMENT ON COLUMN login_logs.logout_at IS '登出时间';
COMMENT ON COLUMN login_logs.token_id IS '令牌ID';

DROP TRIGGER IF EXISTS update_login_logs_updated_at ON login_logs;
CREATE TRIGGER update_login_logs_updated_at
    BEFORE UPDATE ON login_logs
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- ============================================
-- 4. 登录失败锁定表 (防止暴力破解)
-- ============================================
CREATE TABLE IF NOT EXISTS login_attempts (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    ip_address VARCHAR(45) NOT NULL,
    attempt_count INTEGER DEFAULT 1,
    last_attempt_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    locked_until TIMESTAMP WITH TIME ZONE,
    create_time TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    update_time TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    deleted INTEGER DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_login_attempts_username ON login_attempts(username);
CREATE INDEX IF NOT EXISTS idx_login_attempts_ip ON login_attempts(ip_address);
CREATE INDEX IF NOT EXISTS idx_login_attempts_locked ON login_attempts(locked_until);

COMMENT ON TABLE login_attempts IS '登录失败尝试表';
COMMENT ON COLUMN login_attempts.id IS '记录ID';
COMMENT ON COLUMN login_attempts.username IS '用户名';
COMMENT ON COLUMN login_attempts.ip_address IS 'IP地址';
COMMENT ON COLUMN login_attempts.attempt_count IS '尝试次数';
COMMENT ON COLUMN login_attempts.last_attempt_at IS '最后尝试时间';
COMMENT ON COLUMN login_attempts.locked_until IS '锁定至';

DROP TRIGGER IF EXISTS update_login_attempts_updated_at ON login_attempts;
CREATE TRIGGER update_login_attempts_updated_at
    BEFORE UPDATE ON login_attempts
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- ============================================
-- 验证安装
-- ============================================
SELECT 'Phase 1 数据库表创建完成！' AS status;

SELECT table_name,
       (SELECT COUNT(*) FROM information_schema.columns c WHERE c.table_name = t.table_name AND c.table_schema = 'public') AS column_count
FROM information_schema.tables t
WHERE table_schema = 'public'
AND table_type = 'BASE TABLE'
AND table_name IN ('email_verifications', 'password_reset_tokens', 'login_logs', 'login_attempts')
ORDER BY table_name;
