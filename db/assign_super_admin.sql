-- ============================================
-- 分配超级管理员角色
-- 使用方法：
-- 1. 先通过注册或其他方式创建一个账号
-- 2. 修改下面的 <你的用户名> 为你的用户名
-- 3. 执行此脚本
-- ============================================

-- ============================================
-- 步骤1：查看现有用户
-- ============================================
SELECT id, username, email, nickname FROM sys_users ORDER BY id;

-- ============================================
-- 步骤2：给指定用户分配超级管理员角色
-- ============================================
-- 把下面的 'your_username' 改成你的用户名
-- 或者改成你的用户ID

-- 方式A：通过用户名分配
INSERT INTO sys_user_role (user_id, role_id, create_time)
SELECT id, 1, CURRENT_TIMESTAMP
FROM sys_users
WHERE username = 'Admin_01'  -- 改成你的用户名
  AND NOT EXISTS (
    SELECT 1 FROM sys_user_role
    WHERE user_id = sys_users.id AND role_id = 1
);

-- 方式B：通过用户ID分配（如果知道ID）
-- INSERT INTO sys_user_role (user_id, role_id, create_time)
-- VALUES (<你的用户ID>, 1, CURRENT_TIMESTAMP)
-- ON CONFLICT (user_id, role_id) DO NOTHING;

-- ============================================
-- 步骤3：验证分配成功
-- ============================================
SELECT
    u.id,
    u.username,
    u.email,
    u.nickname,
    r.id as role_id,
    r.role_name,
    r.role_code
FROM sys_users u
INNER JOIN sys_user_role ur ON u.id = ur.user_id
INNER JOIN sys_role r ON ur.role_id = r.id
WHERE r.role_code = 'super_admin';
