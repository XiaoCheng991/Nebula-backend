-- ============================================
-- NebulaHub 后台管理系统 - 数据库迁移脚本 (修复版)
-- 版本: v1.0
-- 数据库: PostgreSQL
-- ============================================

-- ============================================
-- 1. 系统管理 - 菜单表
-- ============================================
CREATE TABLE IF NOT EXISTS sys_menu (
    id BIGSERIAL PRIMARY KEY,
    parent_id BIGINT NOT NULL DEFAULT 0,
    menu_type VARCHAR(20) NOT NULL,
    menu_name VARCHAR(100) NOT NULL,
    path VARCHAR(200),
    component VARCHAR(200),
    permission VARCHAR(100),
    icon VARCHAR(100),
    sort_order INT NOT NULL DEFAULT 0,
    is_visible BOOLEAN NOT NULL DEFAULT true,
    is_system BOOLEAN NOT NULL DEFAULT false,
    create_time TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN NOT NULL DEFAULT false
);

COMMENT ON TABLE sys_menu IS '菜单表';
COMMENT ON COLUMN sys_menu.id IS '菜单ID';
COMMENT ON COLUMN sys_menu.parent_id IS '父菜单ID，0表示根节点';
COMMENT ON COLUMN sys_menu.menu_type IS '菜单类型：directory-目录，menu-菜单，button-按钮';
COMMENT ON COLUMN sys_menu.menu_name IS '菜单名称';
COMMENT ON COLUMN sys_menu.path IS '路由路径';
COMMENT ON COLUMN sys_menu.component IS '组件路径';
COMMENT ON COLUMN sys_menu.permission IS '权限标识';
COMMENT ON COLUMN sys_menu.icon IS '菜单图标';
COMMENT ON COLUMN sys_menu.sort_order IS '排序';
COMMENT ON COLUMN sys_menu.is_visible IS '是否显示';
COMMENT ON COLUMN sys_menu.is_system IS '是否系统内置，不可删除';
COMMENT ON COLUMN sys_menu.create_time IS '创建时间';
COMMENT ON COLUMN sys_menu.update_time IS '更新时间';
COMMENT ON COLUMN sys_menu.deleted IS '逻辑删除标志';

-- ============================================
-- 2. 系统管理 - 角色表
-- ============================================
CREATE TABLE IF NOT EXISTS sys_role (
    id BIGSERIAL PRIMARY KEY,
    role_name VARCHAR(100) NOT NULL,
    role_code VARCHAR(100) NOT NULL UNIQUE,
    data_scope VARCHAR(20) NOT NULL DEFAULT 'SELF',
    description VARCHAR(500),
    is_system BOOLEAN NOT NULL DEFAULT false,
    sort_order INT NOT NULL DEFAULT 0,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    create_time TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN NOT NULL DEFAULT false
);

COMMENT ON TABLE sys_role IS '角色表';
COMMENT ON COLUMN sys_role.id IS '角色ID';
COMMENT ON COLUMN sys_role.role_name IS '角色名称';
COMMENT ON COLUMN sys_role.role_code IS '角色编码';
COMMENT ON COLUMN sys_role.data_scope IS '数据权限范围：ALL-全部数据，SELF-仅本人数据';
COMMENT ON COLUMN sys_role.description IS '角色描述';
COMMENT ON COLUMN sys_role.is_system IS '是否系统内置，不可删除';
COMMENT ON COLUMN sys_role.sort_order IS '排序';
COMMENT ON COLUMN sys_role.status IS '状态：ACTIVE-启用，DISABLED-禁用';
COMMENT ON COLUMN sys_role.create_time IS '创建时间';
COMMENT ON COLUMN sys_role.update_time IS '更新时间';
COMMENT ON COLUMN sys_role.deleted IS '逻辑删除标志';

-- ============================================
-- 3. 系统管理 - 角色-菜单关联表
-- ============================================
CREATE TABLE IF NOT EXISTS sys_role_menu (
    id BIGSERIAL PRIMARY KEY,
    role_id BIGINT NOT NULL,
    menu_id BIGINT NOT NULL,
    create_time TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(role_id, menu_id)
);

COMMENT ON TABLE sys_role_menu IS '角色-菜单关联表';
COMMENT ON COLUMN sys_role_menu.id IS '主键ID';
COMMENT ON COLUMN sys_role_menu.role_id IS '角色ID';
COMMENT ON COLUMN sys_role_menu.menu_id IS '菜单ID';
COMMENT ON COLUMN sys_role_menu.create_time IS '创建时间';

CREATE INDEX IF NOT EXISTS idx_sys_role_menu_role_id ON sys_role_menu(role_id);
CREATE INDEX IF NOT EXISTS idx_sys_role_menu_menu_id ON sys_role_menu(menu_id);

-- ============================================
-- 4. 系统管理 - 用户-角色关联表
-- ============================================
CREATE TABLE IF NOT EXISTS sys_user_role (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    create_time TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(user_id, role_id)
);

COMMENT ON TABLE sys_user_role IS '用户-角色关联表';
COMMENT ON COLUMN sys_user_role.id IS '主键ID';
COMMENT ON COLUMN sys_user_role.user_id IS '用户ID';
COMMENT ON COLUMN sys_user_role.role_id IS '角色ID';
COMMENT ON COLUMN sys_user_role.create_time IS '创建时间';

CREATE INDEX IF NOT EXISTS idx_sys_user_role_user_id ON sys_user_role(user_id);
CREATE INDEX IF NOT EXISTS idx_sys_user_role_role_id ON sys_user_role(role_id);

-- ============================================
-- 5. 系统管理 - 数据字典类型表
-- ============================================
CREATE TABLE IF NOT EXISTS sys_dict_type (
    id BIGSERIAL PRIMARY KEY,
    dict_name VARCHAR(100) NOT NULL,
    dict_code VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(500),
    is_system BOOLEAN NOT NULL DEFAULT false,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    create_time TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN NOT NULL DEFAULT false
);

COMMENT ON TABLE sys_dict_type IS '数据字典类型表';
COMMENT ON COLUMN sys_dict_type.id IS '字典类型ID';
COMMENT ON COLUMN sys_dict_type.dict_name IS '字典名称';
COMMENT ON COLUMN sys_dict_type.dict_code IS '字典编码';
COMMENT ON COLUMN sys_dict_type.description IS '描述';
COMMENT ON COLUMN sys_dict_type.is_system IS '是否系统内置，不可删除';
COMMENT ON COLUMN sys_dict_type.status IS '状态：ACTIVE-启用，DISABLED-禁用';
COMMENT ON COLUMN sys_dict_type.create_time IS '创建时间';
COMMENT ON COLUMN sys_dict_type.update_time IS '更新时间';
COMMENT ON COLUMN sys_dict_type.deleted IS '逻辑删除标志';

-- ============================================
-- 6. 系统管理 - 数据字典项表
-- ============================================
CREATE TABLE IF NOT EXISTS sys_dict_item (
    id BIGSERIAL PRIMARY KEY,
    dict_type_id BIGINT NOT NULL,
    dict_label VARCHAR(100) NOT NULL,
    dict_value VARCHAR(100) NOT NULL,
    sort_order INT NOT NULL DEFAULT 0,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    is_default BOOLEAN NOT NULL DEFAULT false,
    css_class VARCHAR(100),
    list_class VARCHAR(100),
    create_time TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN NOT NULL DEFAULT false
);

COMMENT ON TABLE sys_dict_item IS '数据字典项表';
COMMENT ON COLUMN sys_dict_item.id IS '字典项ID';
COMMENT ON COLUMN sys_dict_item.dict_type_id IS '字典类型ID';
COMMENT ON COLUMN sys_dict_item.dict_label IS '字典标签';
COMMENT ON COLUMN sys_dict_item.dict_value IS '字典值';
COMMENT ON COLUMN sys_dict_item.sort_order IS '排序';
COMMENT ON COLUMN sys_dict_item.status IS '状态：ACTIVE-启用，DISABLED-禁用';
COMMENT ON COLUMN sys_dict_item.is_default IS '是否默认';
COMMENT ON COLUMN sys_dict_item.css_class IS '样式属性';
COMMENT ON COLUMN sys_dict_item.list_class IS '表格回显样式';
COMMENT ON COLUMN sys_dict_item.create_time IS '创建时间';
COMMENT ON COLUMN sys_dict_item.update_time IS '更新时间';
COMMENT ON COLUMN sys_dict_item.deleted IS '逻辑删除标志';

CREATE INDEX IF NOT EXISTS idx_sys_dict_item_type_id ON sys_dict_item(dict_type_id);

-- ============================================
-- 7. 系统管理 - 操作日志表
-- ============================================
CREATE TABLE IF NOT EXISTS sys_operation_log (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT,
    username VARCHAR(100),
    module VARCHAR(100),
    operation VARCHAR(100),
    method VARCHAR(200),
    request_method VARCHAR(20),
    request_url VARCHAR(500),
    request_params TEXT,
    response_result TEXT,
    ip_address VARCHAR(50),
    location VARCHAR(200),
    browser VARCHAR(200),
    os VARCHAR(200),
    status VARCHAR(20) NOT NULL DEFAULT 'SUCCESS',
    error_msg TEXT,
    execution_time BIGINT,
    operation_time TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE sys_operation_log IS '操作日志表';
COMMENT ON COLUMN sys_operation_log.id IS '日志ID';
COMMENT ON COLUMN sys_operation_log.user_id IS '用户ID';
COMMENT ON COLUMN sys_operation_log.username IS '用户名';
COMMENT ON COLUMN sys_operation_log.module IS '模块名称';
COMMENT ON COLUMN sys_operation_log.operation IS '操作描述';
COMMENT ON COLUMN sys_operation_log.method IS '方法名称';
COMMENT ON COLUMN sys_operation_log.request_method IS '请求方式';
COMMENT ON COLUMN sys_operation_log.request_url IS '请求URL';
COMMENT ON COLUMN sys_operation_log.request_params IS '请求参数';
COMMENT ON COLUMN sys_operation_log.response_result IS '响应结果';
COMMENT ON COLUMN sys_operation_log.ip_address IS 'IP地址';
COMMENT ON COLUMN sys_operation_log.location IS '地理位置';
COMMENT ON COLUMN sys_operation_log.browser IS '浏览器';
COMMENT ON COLUMN sys_operation_log.os IS '操作系统';
COMMENT ON COLUMN sys_operation_log.status IS '状态：SUCCESS-成功，FAIL-失败';
COMMENT ON COLUMN sys_operation_log.error_msg IS '错误信息';
COMMENT ON COLUMN sys_operation_log.execution_time IS '执行时间(毫秒)';
COMMENT ON COLUMN sys_operation_log.operation_time IS '操作时间';

CREATE INDEX IF NOT EXISTS idx_sys_operation_log_user_id ON sys_operation_log(user_id);
CREATE INDEX IF NOT EXISTS idx_sys_operation_log_time ON sys_operation_log(operation_time);

-- ============================================
-- 8. 系统管理 - 在线用户表
-- ============================================
CREATE TABLE IF NOT EXISTS sys_online_user (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    username VARCHAR(100) NOT NULL,
    nickname VARCHAR(100),
    token VARCHAR(500) NOT NULL,
    ip_address VARCHAR(50),
    location VARCHAR(200),
    browser VARCHAR(200),
    os VARCHAR(200),
    login_time TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_activity_time TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expired BOOLEAN NOT NULL DEFAULT false
);

COMMENT ON TABLE sys_online_user IS '在线用户表';
COMMENT ON COLUMN sys_online_user.id IS '主键ID';
COMMENT ON COLUMN sys_online_user.user_id IS '用户ID';
COMMENT ON COLUMN sys_online_user.username IS '用户名';
COMMENT ON COLUMN sys_online_user.nickname IS '昵称';
COMMENT ON COLUMN sys_online_user.token IS 'Token';
COMMENT ON COLUMN sys_online_user.ip_address IS 'IP地址';
COMMENT ON COLUMN sys_online_user.location IS '地理位置';
COMMENT ON COLUMN sys_online_user.browser IS '浏览器';
COMMENT ON COLUMN sys_online_user.os IS '操作系统';
COMMENT ON COLUMN sys_online_user.login_time IS '登录时间';
COMMENT ON COLUMN sys_online_user.last_activity_time IS '最后活动时间';
COMMENT ON COLUMN sys_online_user.expired IS '是否已过期';

CREATE INDEX IF NOT EXISTS idx_sys_online_user_user_id ON sys_online_user(user_id);
CREATE INDEX IF NOT EXISTS idx_sys_online_user_token ON sys_online_user(token);
CREATE INDEX IF NOT EXISTS idx_sys_online_user_expired ON sys_online_user(expired);

-- ============================================
-- 9. 博客管理 - 文章分类表
-- ============================================
CREATE TABLE IF NOT EXISTS blog_category (
    id BIGSERIAL PRIMARY KEY,
    parent_id BIGINT NOT NULL DEFAULT 0,
    category_name VARCHAR(100) NOT NULL,
    slug VARCHAR(100),
    description VARCHAR(500),
    icon VARCHAR(100),
    sort_order INT NOT NULL DEFAULT 0,
    article_count INT NOT NULL DEFAULT 0,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    create_time TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN NOT NULL DEFAULT false,
    create_by BIGINT
);

COMMENT ON TABLE blog_category IS '文章分类表';
COMMENT ON COLUMN blog_category.id IS '分类ID';
COMMENT ON COLUMN blog_category.parent_id IS '父分类ID';
COMMENT ON COLUMN blog_category.category_name IS '分类名称';
COMMENT ON COLUMN blog_category.slug IS '分类别名';
COMMENT ON COLUMN blog_category.description IS '描述';
COMMENT ON COLUMN blog_category.icon IS '图标';
COMMENT ON COLUMN blog_category.sort_order IS '排序';
COMMENT ON COLUMN blog_category.article_count IS '文章数量';
COMMENT ON COLUMN blog_category.status IS '状态：ACTIVE-启用，DISABLED-禁用';
COMMENT ON COLUMN blog_category.create_time IS '创建时间';
COMMENT ON COLUMN blog_category.update_time IS '更新时间';
COMMENT ON COLUMN blog_category.deleted IS '逻辑删除标志';
COMMENT ON COLUMN blog_category.create_by IS '创建者ID';

-- ============================================
-- 10. 博客管理 - 文章标签表
-- ============================================
CREATE TABLE IF NOT EXISTS blog_tag (
    id BIGSERIAL PRIMARY KEY,
    tag_name VARCHAR(100) NOT NULL UNIQUE,
    slug VARCHAR(100),
    description VARCHAR(500),
    icon VARCHAR(100),
    color VARCHAR(20),
    article_count INT NOT NULL DEFAULT 0,
    sort_order INT NOT NULL DEFAULT 0,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    create_time TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN NOT NULL DEFAULT false,
    create_by BIGINT
);

COMMENT ON TABLE blog_tag IS '文章标签表';
COMMENT ON COLUMN blog_tag.id IS '标签ID';
COMMENT ON COLUMN blog_tag.tag_name IS '标签名称';
COMMENT ON COLUMN blog_tag.slug IS '标签别名';
COMMENT ON COLUMN blog_tag.description IS '描述';
COMMENT ON COLUMN blog_tag.icon IS '图标';
COMMENT ON COLUMN blog_tag.color IS '颜色';
COMMENT ON COLUMN blog_tag.article_count IS '文章数量';
COMMENT ON COLUMN blog_tag.sort_order IS '排序';
COMMENT ON COLUMN blog_tag.status IS '状态：ACTIVE-启用，DISABLED-禁用';
COMMENT ON COLUMN blog_tag.create_time IS '创建时间';
COMMENT ON COLUMN blog_tag.update_time IS '更新时间';
COMMENT ON COLUMN blog_tag.deleted IS '逻辑删除标志';
COMMENT ON COLUMN blog_tag.create_by IS '创建者ID';

-- ============================================
-- 11. 博客管理 - 文章表
-- ============================================
CREATE TABLE IF NOT EXISTS blog_article (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    slug VARCHAR(200),
    summary TEXT,
    content TEXT NOT NULL,
    content_html TEXT,
    cover_image VARCHAR(500),
    category_id BIGINT,
    author_id BIGINT NOT NULL,
    author_name VARCHAR(100),
    view_count BIGINT NOT NULL DEFAULT 0,
    like_count BIGINT NOT NULL DEFAULT 0,
    comment_count BIGINT NOT NULL DEFAULT 0,
    status VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
    is_top BOOLEAN NOT NULL DEFAULT false,
    is_recommended BOOLEAN NOT NULL DEFAULT false,
    is_comment_enabled BOOLEAN NOT NULL DEFAULT true,
    word_count INT,
    publish_time TIMESTAMP WITH TIME ZONE,
    create_time TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN NOT NULL DEFAULT false
);

COMMENT ON TABLE blog_article IS '文章表';
COMMENT ON COLUMN blog_article.id IS '文章ID';
COMMENT ON COLUMN blog_article.title IS '文章标题';
COMMENT ON COLUMN blog_article.slug IS '文章别名';
COMMENT ON COLUMN blog_article.summary IS '文章摘要';
COMMENT ON COLUMN blog_article.content IS '文章内容(Markdown)';
COMMENT ON COLUMN blog_article.content_html IS '文章内容(HTML)';
COMMENT ON COLUMN blog_article.cover_image IS '封面图片';
COMMENT ON COLUMN blog_article.category_id IS '分类ID';
COMMENT ON COLUMN blog_article.author_id IS '作者ID';
COMMENT ON COLUMN blog_article.author_name IS '作者名称';
COMMENT ON COLUMN blog_article.view_count IS '浏览次数';
COMMENT ON COLUMN blog_article.like_count IS '点赞次数';
COMMENT ON COLUMN blog_article.comment_count IS '评论次数';
COMMENT ON COLUMN blog_article.status IS '状态：DRAFT-草稿，PENDING-待审核，PUBLISHED-已发布，REJECTED-已拒绝';
COMMENT ON COLUMN blog_article.is_top IS '是否置顶';
COMMENT ON COLUMN blog_article.is_recommended IS '是否推荐';
COMMENT ON COLUMN blog_article.is_comment_enabled IS '是否允许评论';
COMMENT ON COLUMN blog_article.word_count IS '字数';
COMMENT ON COLUMN blog_article.publish_time IS '发布时间';
COMMENT ON COLUMN blog_article.create_time IS '创建时间';
COMMENT ON COLUMN blog_article.update_time IS '更新时间';
COMMENT ON COLUMN blog_article.deleted IS '逻辑删除标志';

CREATE INDEX IF NOT EXISTS idx_blog_article_author_id ON blog_article(author_id);
CREATE INDEX IF NOT EXISTS idx_blog_article_category_id ON blog_article(category_id);
CREATE INDEX IF NOT EXISTS idx_blog_article_status ON blog_article(status);
CREATE INDEX IF NOT EXISTS idx_blog_article_create_time ON blog_article(create_time);

-- ============================================
-- 12. 博客管理 - 文章-标签关联表
-- ============================================
CREATE TABLE IF NOT EXISTS blog_article_tag (
    id BIGSERIAL PRIMARY KEY,
    article_id BIGINT NOT NULL,
    tag_id BIGINT NOT NULL,
    create_time TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(article_id, tag_id)
);

COMMENT ON TABLE blog_article_tag IS '文章-标签关联表';
COMMENT ON COLUMN blog_article_tag.id IS '主键ID';
COMMENT ON COLUMN blog_article_tag.article_id IS '文章ID';
COMMENT ON COLUMN blog_article_tag.tag_id IS '标签ID';
COMMENT ON COLUMN blog_article_tag.create_time IS '创建时间';

CREATE INDEX IF NOT EXISTS idx_blog_article_tag_article_id ON blog_article_tag(article_id);
CREATE INDEX IF NOT EXISTS idx_blog_article_tag_tag_id ON blog_article_tag(tag_id);

-- ============================================
-- 13. 博客管理 - 评论表
-- ============================================
CREATE TABLE IF NOT EXISTS blog_comment (
    id BIGSERIAL PRIMARY KEY,
    article_id BIGINT NOT NULL,
    parent_id BIGINT NOT NULL DEFAULT 0,
    user_id BIGINT NOT NULL,
    username VARCHAR(100),
    nickname VARCHAR(100),
    avatar_url VARCHAR(500),
    content TEXT NOT NULL,
    ip_address VARCHAR(50),
    location VARCHAR(200),
    like_count BIGINT NOT NULL DEFAULT 0,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    create_time TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN NOT NULL DEFAULT false
);

COMMENT ON TABLE blog_comment IS '评论表';
COMMENT ON COLUMN blog_comment.id IS '评论ID';
COMMENT ON COLUMN blog_comment.article_id IS '文章ID';
COMMENT ON COLUMN blog_comment.parent_id IS '父评论ID';
COMMENT ON COLUMN blog_comment.user_id IS '用户ID';
COMMENT ON COLUMN blog_comment.username IS '用户名';
COMMENT ON COLUMN blog_comment.nickname IS '昵称';
COMMENT ON COLUMN blog_comment.avatar_url IS '头像URL';
COMMENT ON COLUMN blog_comment.content IS '评论内容';
COMMENT ON COLUMN blog_comment.ip_address IS 'IP地址';
COMMENT ON COLUMN blog_comment.location IS '地理位置';
COMMENT ON COLUMN blog_comment.like_count IS '点赞次数';
COMMENT ON COLUMN blog_comment.status IS '状态：PENDING-待审核，APPROVED-已通过，REJECTED-已拒绝';
COMMENT ON COLUMN blog_comment.create_time IS '创建时间';
COMMENT ON COLUMN blog_comment.update_time IS '更新时间';
COMMENT ON COLUMN blog_comment.deleted IS '逻辑删除标志';

CREATE INDEX IF NOT EXISTS idx_blog_comment_article_id ON blog_comment(article_id);
CREATE INDEX IF NOT EXISTS idx_blog_comment_user_id ON blog_comment(user_id);
CREATE INDEX IF NOT EXISTS idx_blog_comment_status ON blog_comment(status);

-- ============================================
-- 14. IM管理 - 敏感词表
-- ============================================
CREATE TABLE IF NOT EXISTS im_sensitive_word (
    id BIGSERIAL PRIMARY KEY,
    word VARCHAR(200) NOT NULL UNIQUE,
    word_type VARCHAR(20) NOT NULL DEFAULT 'NORMAL',
    replace_str VARCHAR(200) DEFAULT '***',
    is_enabled BOOLEAN NOT NULL DEFAULT true,
    create_time TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    create_by BIGINT
);

COMMENT ON TABLE im_sensitive_word IS '敏感词表';
COMMENT ON COLUMN im_sensitive_word.id IS '敏感词ID';
COMMENT ON COLUMN im_sensitive_word.word IS '敏感词';
COMMENT ON COLUMN im_sensitive_word.word_type IS '敏感词类型：NORMAL-普通，POLITICAL-政治，PORN-色情，VIOLENCE-暴力';
COMMENT ON COLUMN im_sensitive_word.replace_str IS '替换字符';
COMMENT ON COLUMN im_sensitive_word.is_enabled IS '是否启用';
COMMENT ON COLUMN im_sensitive_word.create_time IS '创建时间';
COMMENT ON COLUMN im_sensitive_word.update_time IS '更新时间';
COMMENT ON COLUMN im_sensitive_word.create_by IS '创建者ID';

-- ============================================
-- 15. IM管理 - 消息归档表
-- ============================================
CREATE TABLE IF NOT EXISTS im_message_archive (
    id BIGSERIAL PRIMARY KEY,
    message_id BIGINT NOT NULL,
    room_id BIGINT NOT NULL,
    sender_id BIGINT NOT NULL,
    sender_name VARCHAR(100),
    message_type VARCHAR(20) NOT NULL,
    content TEXT,
    is_sensitive BOOLEAN NOT NULL DEFAULT false,
    sensitive_hits TEXT,
    is_recalled BOOLEAN NOT NULL DEFAULT false,
    recall_time TIMESTAMP WITH TIME ZONE,
    create_time TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE im_message_archive IS '消息归档表';
COMMENT ON COLUMN im_message_archive.id IS '归档ID';
COMMENT ON COLUMN im_message_archive.message_id IS '消息ID';
COMMENT ON COLUMN im_message_archive.room_id IS '聊天室ID';
COMMENT ON COLUMN im_message_archive.sender_id IS '发送者ID';
COMMENT ON COLUMN im_message_archive.sender_name IS '发送者名称';
COMMENT ON COLUMN im_message_archive.message_type IS '消息类型';
COMMENT ON COLUMN im_message_archive.content IS '消息内容';
COMMENT ON COLUMN im_message_archive.is_sensitive IS '是否包含敏感词';
COMMENT ON COLUMN im_message_archive.sensitive_hits IS '命中的敏感词(JSON数组)';
COMMENT ON COLUMN im_message_archive.is_recalled IS '是否已撤回';
COMMENT ON COLUMN im_message_archive.recall_time IS '撤回时间';
COMMENT ON COLUMN im_message_archive.create_time IS '创建时间';

CREATE INDEX IF NOT EXISTS idx_im_message_archive_message_id ON im_message_archive(message_id);
CREATE INDEX IF NOT EXISTS idx_im_message_archive_room_id ON im_message_archive(room_id);
CREATE INDEX IF NOT EXISTS idx_im_message_archive_sender_id ON im_message_archive(sender_id);
CREATE INDEX IF NOT EXISTS idx_im_message_archive_create_time ON im_message_archive(create_time);

-- ============================================
-- 16. IM管理 - 用户封禁表
-- ============================================
CREATE TABLE IF NOT EXISTS im_user_ban (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    username VARCHAR(100) NOT NULL,
    ban_type VARCHAR(20) NOT NULL,
    reason VARCHAR(500),
    ban_time TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expire_time TIMESTAMP WITH TIME ZONE,
    is_permanent BOOLEAN NOT NULL DEFAULT false,
    operator_id BIGINT,
    operator_name VARCHAR(100),
    is_active BOOLEAN NOT NULL DEFAULT true,
    unban_time TIMESTAMP WITH TIME ZONE,
    unban_reason VARCHAR(500),
    create_time TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE im_user_ban IS '用户封禁表';
COMMENT ON COLUMN im_user_ban.id IS '封禁ID';
COMMENT ON COLUMN im_user_ban.user_id IS '用户ID';
COMMENT ON COLUMN im_user_ban.username IS '用户名';
COMMENT ON COLUMN im_user_ban.ban_type IS '封禁类型：CHAT-禁言，LOGIN-禁止登录，FULL-全封禁';
COMMENT ON COLUMN im_user_ban.reason IS '封禁原因';
COMMENT ON COLUMN im_user_ban.ban_time IS '封禁时间';
COMMENT ON COLUMN im_user_ban.expire_time IS '过期时间';
COMMENT ON COLUMN im_user_ban.is_permanent IS '是否永久封禁';
COMMENT ON COLUMN im_user_ban.operator_id IS '操作者ID';
COMMENT ON COLUMN im_user_ban.operator_name IS '操作者名称';
COMMENT ON COLUMN im_user_ban.is_active IS '是否有效';
COMMENT ON COLUMN im_user_ban.unban_time IS '解封时间';
COMMENT ON COLUMN im_user_ban.unban_reason IS '解封原因';
COMMENT ON COLUMN im_user_ban.create_time IS '创建时间';
COMMENT ON COLUMN im_user_ban.update_time IS '更新时间';

CREATE INDEX IF NOT EXISTS idx_im_user_ban_user_id ON im_user_ban(user_id);
CREATE INDEX IF NOT EXISTS idx_im_user_ban_active ON im_user_ban(is_active);

-- ============================================
-- 17. 扩展现有表 - chat_rooms
-- ============================================
ALTER TABLE chat_rooms ADD COLUMN IF NOT EXISTS status VARCHAR(20) DEFAULT 'ACTIVE';
ALTER TABLE chat_rooms ADD COLUMN IF NOT EXISTS is_system BOOLEAN DEFAULT false;

COMMENT ON COLUMN chat_rooms.status IS '状态：ACTIVE-正常，DISABLED-禁用，ARCHIVED-已归档';
COMMENT ON COLUMN chat_rooms.is_system IS '是否系统聊天室';

-- ============================================
-- 18. 创建自动更新时间的函数
-- ============================================
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS '
BEGIN
    NEW.update_time = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
' language 'plpgsql';

-- ============================================
-- 19. 为需要自动更新 update_time 的表创建触发器
-- ============================================

-- sys_menu
DROP TRIGGER IF EXISTS trigger_sys_menu_update_time ON sys_menu;
CREATE TRIGGER trigger_sys_menu_update_time
    BEFORE UPDATE ON sys_menu
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- sys_role
DROP TRIGGER IF EXISTS trigger_sys_role_update_time ON sys_role;
CREATE TRIGGER trigger_sys_role_update_time
    BEFORE UPDATE ON sys_role
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- sys_dict_type
DROP TRIGGER IF EXISTS trigger_sys_dict_type_update_time ON sys_dict_type;
CREATE TRIGGER trigger_sys_dict_type_update_time
    BEFORE UPDATE ON sys_dict_type
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- sys_dict_item
DROP TRIGGER IF EXISTS trigger_sys_dict_item_update_time ON sys_dict_item;
CREATE TRIGGER trigger_sys_dict_item_update_time
    BEFORE UPDATE ON sys_dict_item
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- blog_category
DROP TRIGGER IF EXISTS trigger_blog_category_update_time ON blog_category;
CREATE TRIGGER trigger_blog_category_update_time
    BEFORE UPDATE ON blog_category
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- blog_tag
DROP TRIGGER IF EXISTS trigger_blog_tag_update_time ON blog_tag;
CREATE TRIGGER trigger_blog_tag_update_time
    BEFORE UPDATE ON blog_tag
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- blog_article
DROP TRIGGER IF EXISTS trigger_blog_article_update_time ON blog_article;
CREATE TRIGGER trigger_blog_article_update_time
    BEFORE UPDATE ON blog_article
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- blog_comment
DROP TRIGGER IF EXISTS trigger_blog_comment_update_time ON blog_comment;
CREATE TRIGGER trigger_blog_comment_update_time
    BEFORE UPDATE ON blog_comment
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- im_sensitive_word
DROP TRIGGER IF EXISTS trigger_im_sensitive_word_update_time ON im_sensitive_word;
CREATE TRIGGER trigger_im_sensitive_word_update_time
    BEFORE UPDATE ON im_sensitive_word
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- im_user_ban
DROP TRIGGER IF EXISTS trigger_im_user_ban_update_time ON im_user_ban;
CREATE TRIGGER trigger_im_user_ban_update_time
    BEFORE UPDATE ON im_user_ban
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
