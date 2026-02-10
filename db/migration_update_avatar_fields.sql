-- =====================================================
-- 更新 user_profiles 表的头像字段结构
-- 执行时间: 2025-02-09
-- =====================================================

use nebula;

-- 1. 备份现有的 avatar_url 数据到新字段
ALTER TABLE user_profiles ADD COLUMN avatar_name VARCHAR(255) COMMENT '头像文件名称';
ALTER TABLE user_profiles ADD COLUMN avatar_url_new VARCHAR(512) COMMENT '头像在MinIO中的URL';
ALTER TABLE user_profiles ADD COLUMN avatar_size BIGINT COMMENT '头像文件大小（字节）';

-- 2. 如果旧字段有数据，迁移到新字段（保留原URL作为avatar_url_new）
UPDATE user_profiles
SET
    avatar_url_new = avatar_url,
    avatar_name = CASE
        WHEN avatar_url IS NOT NULL THEN SUBSTRING_INDEX(avatar_url, '/', -1)
        END
WHERE avatar_url IS NOT NULL;

-- 3. 删除旧的 avatar_url 字段
ALTER TABLE user_profiles DROP COLUMN avatar_url;

-- 4. 重命名新字段为正式字段名
ALTER TABLE user_profiles CHANGE COLUMN avatar_url_new avatar_url VARCHAR(512) COMMENT '头像在MinIO中的URL';

-- =====================================================
-- 可选：删除 sys_user 表中的 avatar 字段（用户手动执行）
-- =====================================================
-- ALTER TABLE sys_user DROP COLUMN avatar;
