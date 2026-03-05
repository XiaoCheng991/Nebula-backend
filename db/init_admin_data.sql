-- ============================================
-- NebulaHub 后台管理系统 - 初始化数据
-- 版本: v1.0
-- 数据库: PostgreSQL
-- ============================================

-- ============================================
-- 1. 初始化菜单数据
-- ============================================

-- 系统管理（目录）
INSERT INTO sys_menu (id, parent_id, menu_type, menu_name, path, component, permission, icon, sort_order, is_visible, is_system, create_time, update_time, deleted) VALUES
(1, 0, 'directory', '系统管理', '/system', NULL, NULL, 'SettingOutlined', 1, true, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false)
ON CONFLICT (id) DO NOTHING;

-- 用户管理（菜单）
INSERT INTO sys_menu (id, parent_id, menu_type, menu_name, path, component, permission, icon, sort_order, is_visible, is_system, create_time, update_time, deleted) VALUES
(2, 1, 'menu', '用户管理', 'user', 'system/user/index', 'system:user:view', 'UserOutlined', 1, true, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false)
ON CONFLICT (id) DO NOTHING;

-- 用户管理按钮
INSERT INTO sys_menu (id, parent_id, menu_type, menu_name, path, component, permission, icon, sort_order, is_visible, is_system, create_time, update_time, deleted) VALUES
(101, 2, 'button', '用户查询', '', '', 'system:user:query', '', 1, true, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false),
(102, 2, 'button', '用户新增', '', '', 'system:user:add', '', 2, true, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false),
(103, 2, 'button', '用户编辑', '', '', 'system:user:edit', '', 3, true, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false),
(104, 2, 'button', '用户删除', '', '', 'system:user:delete', '', 4, true, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false),
(105, 2, 'button', '重置密码', '', '', 'system:user:resetPwd', '', 5, true, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false)
ON CONFLICT (id) DO NOTHING;

-- 角色管理（菜单）
INSERT INTO sys_menu (id, parent_id, menu_type, menu_name, path, component, permission, icon, sort_order, is_visible, is_system, create_time, update_time, deleted) VALUES
(3, 1, 'menu', '角色管理', 'role', 'system/role/index', 'system:role:view', 'TeamOutlined', 2, true, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false)
ON CONFLICT (id) DO NOTHING;

-- 角色管理按钮
INSERT INTO sys_menu (id, parent_id, menu_type, menu_name, path, component, permission, icon, sort_order, is_visible, is_system, create_time, update_time, deleted) VALUES
(201, 3, 'button', '角色查询', '', '', 'system:role:query', '', 1, true, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false),
(202, 3, 'button', '角色新增', '', '', 'system:role:add', '', 2, true, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false),
(203, 3, 'button', '角色编辑', '', '', 'system:role:edit', '', 3, true, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false),
(204, 3, 'button', '角色删除', '', '', 'system:role:delete', '', 4, true, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false)
ON CONFLICT (id) DO NOTHING;

-- 菜单管理（菜单）
INSERT INTO sys_menu (id, parent_id, menu_type, menu_name, path, component, permission, icon, sort_order, is_visible, is_system, create_time, update_time, deleted) VALUES
(4, 1, 'menu', '菜单管理', 'menu', 'system/menu/index', 'system:menu:view', 'MenuOutlined', 3, true, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false)
ON CONFLICT (id) DO NOTHING;

-- 菜单管理按钮
INSERT INTO sys_menu (id, parent_id, menu_type, menu_name, path, component, permission, icon, sort_order, is_visible, is_system, create_time, update_time, deleted) VALUES
(301, 4, 'button', '菜单查询', '', '', 'system:menu:query', '', 1, true, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false),
(302, 4, 'button', '菜单新增', '', '', 'system:menu:add', '', 2, true, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false),
(303, 4, 'button', '菜单编辑', '', '', 'system:menu:edit', '', 3, true, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false),
(304, 4, 'button', '菜单删除', '', '', 'system:menu:delete', '', 4, true, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false)
ON CONFLICT (id) DO NOTHING;

-- 字典管理（菜单）
INSERT INTO sys_menu (id, parent_id, menu_type, menu_name, path, component, permission, icon, sort_order, is_visible, is_system, create_time, update_time, deleted) VALUES
(5, 1, 'menu', '字典管理', 'dict', 'system/dict/index', 'system:dict:view', 'BookOutlined', 4, true, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false)
ON CONFLICT (id) DO NOTHING;

-- 字典管理按钮
INSERT INTO sys_menu (id, parent_id, menu_type, menu_name, path, component, permission, icon, sort_order, is_visible, is_system, create_time, update_time, deleted) VALUES
(401, 5, 'button', '字典查询', '', '', 'system:dict:query', '', 1, true, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false),
(402, 5, 'button', '字典新增', '', '', 'system:dict:add', '', 2, true, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false),
(403, 5, 'button', '字典编辑', '', '', 'system:dict:edit', '', 3, true, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false),
(404, 5, 'button', '字典删除', '', '', 'system:dict:delete', '', 4, true, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false)
ON CONFLICT (id) DO NOTHING;

-- 系统监控（目录）
INSERT INTO sys_menu (id, parent_id, menu_type, menu_name, path, component, permission, icon, sort_order, is_visible, is_system, create_time, update_time, deleted) VALUES
(6, 0, 'directory', '系统监控', '/monitor', NULL, NULL, 'EyeOutlined', 2, true, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false)
ON CONFLICT (id) DO NOTHING;

-- 操作日志（菜单）
INSERT INTO sys_menu (id, parent_id, menu_type, menu_name, path, component, permission, icon, sort_order, is_visible, is_system, create_time, update_time, deleted) VALUES
(7, 6, 'menu', '操作日志', 'operlog', 'monitor/operlog/index', 'monitor:operlog:view', 'FileTextOutlined', 1, true, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false)
ON CONFLICT (id) DO NOTHING;

-- 操作日志按钮
INSERT INTO sys_menu (id, parent_id, menu_type, menu_name, path, component, permission, icon, sort_order, is_visible, is_system, create_time, update_time, deleted) VALUES
(501, 7, 'button', '日志查询', '', '', 'monitor:operlog:query', '', 1, true, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false),
(502, 7, 'button', '日志删除', '', '', 'monitor:operlog:delete', '', 2, true, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false)
ON CONFLICT (id) DO NOTHING;

-- 登录日志（菜单）
INSERT INTO sys_menu (id, parent_id, menu_type, menu_name, path, component, permission, icon, sort_order, is_visible, is_system, create_time, update_time, deleted) VALUES
(8, 6, 'menu', '登录日志', 'loginlog', 'monitor/loginlog/index', 'monitor:loginlog:view', 'LoginOutlined', 2, true, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false)
ON CONFLICT (id) DO NOTHING;

-- 登录日志按钮
INSERT INTO sys_menu (id, parent_id, menu_type, menu_name, path, component, permission, icon, sort_order, is_visible, is_system, create_time, update_time, deleted) VALUES
(601, 8, 'button', '日志查询', '', '', 'monitor:loginlog:query', '', 1, true, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false),
(602, 8, 'button', '日志删除', '', '', 'monitor:loginlog:delete', '', 2, true, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false)
ON CONFLICT (id) DO NOTHING;

-- 在线用户（菜单）
INSERT INTO sys_menu (id, parent_id, menu_type, menu_name, path, component, permission, icon, sort_order, is_visible, is_system, create_time, update_time, deleted) VALUES
(9, 6, 'menu', '在线用户', 'online', 'monitor/online/index', 'monitor:online:view', 'UserSwitchOutlined', 3, true, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false)
ON CONFLICT (id) DO NOTHING;

-- 在线用户按钮
INSERT INTO sys_menu (id, parent_id, menu_type, menu_name, path, component, permission, icon, sort_order, is_visible, is_system, create_time, update_time, deleted) VALUES
(701, 9, 'button', '在线查询', '', '', 'monitor:online:query', '', 1, true, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false),
(702, 9, 'button', '强制下线', '', '', 'monitor:online:forceLogout', '', 2, true, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false)
ON CONFLICT (id) DO NOTHING;

-- 博客管理（目录）
INSERT INTO sys_menu (id, parent_id, menu_type, menu_name, path, component, permission, icon, sort_order, is_visible, is_system, create_time, update_time, deleted) VALUES
(10, 0, 'directory', '博客管理', '/blog', NULL, NULL, 'ReadOutlined', 3, true, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false)
ON CONFLICT (id) DO NOTHING;

-- 文章管理（菜单）
INSERT INTO sys_menu (id, parent_id, menu_type, menu_name, path, component, permission, icon, sort_order, is_visible, is_system, create_time, update_time, deleted) VALUES
(11, 10, 'menu', '文章管理', 'article', 'blog/article/index', 'blog:article:view', 'FileTextOutlined', 1, true, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false)
ON CONFLICT (id) DO NOTHING;

-- 文章管理按钮
INSERT INTO sys_menu (id, parent_id, menu_type, menu_name, path, component, permission, icon, sort_order, is_visible, is_system, create_time, update_time, deleted) VALUES
(801, 11, 'button', '文章查询', '', '', 'blog:article:query', '', 1, true, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false),
(802, 11, 'button', '文章新增', '', '', 'blog:article:add', '', 2, true, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false),
(803, 11, 'button', '文章编辑', '', '', 'blog:article:edit', '', 3, true, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false),
(804, 11, 'button', '文章删除', '', '', 'blog:article:delete', '', 4, true, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false)
ON CONFLICT (id) DO NOTHING;

-- 分类管理（菜单）
INSERT INTO sys_menu (id, parent_id, menu_type, menu_name, path, component, permission, icon, sort_order, is_visible, is_system, create_time, update_time, deleted) VALUES
(12, 10, 'menu', '分类管理', 'category', 'blog/category/index', 'blog:category:view', 'FolderOutlined', 2, true, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false)
ON CONFLICT (id) DO NOTHING;

-- 分类管理按钮
INSERT INTO sys_menu (id, parent_id, menu_type, menu_name, path, component, permission, icon, sort_order, is_visible, is_system, create_time, update_time, deleted) VALUES
(901, 12, 'button', '分类查询', '', '', 'blog:category:query', '', 1, true, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false),
(902, 12, 'button', '分类新增', '', '', 'blog:category:add', '', 2, true, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false),
(903, 12, 'button', '分类编辑', '', '', 'blog:category:edit', '', 3, true, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false),
(904, 12, 'button', '分类删除', '', '', 'blog:category:delete', '', 4, true, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false)
ON CONFLICT (id) DO NOTHING;

-- 标签管理（菜单）
INSERT INTO sys_menu (id, parent_id, menu_type, menu_name, path, component, permission, icon, sort_order, is_visible, is_system, create_time, update_time, deleted) VALUES
(13, 10, 'menu', '标签管理', 'tag', 'blog/tag/index', 'blog:tag:view', 'TagsOutlined', 3, true, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false)
ON CONFLICT (id) DO NOTHING;

-- 标签管理按钮
INSERT INTO sys_menu (id, parent_id, menu_type, menu_name, path, component, permission, icon, sort_order, is_visible, is_system, create_time, update_time, deleted) VALUES
(1001, 13, 'button', '标签查询', '', '', 'blog:tag:query', '', 1, true, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false),
(1002, 13, 'button', '标签新增', '', '', 'blog:tag:add', '', 2, true, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false),
(1003, 13, 'button', '标签编辑', '', '', 'blog:tag:edit', '', 3, true, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false),
(1004, 13, 'button', '标签删除', '', '', 'blog:tag:delete', '', 4, true, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false)
ON CONFLICT (id) DO NOTHING;

-- 评论管理（菜单）
INSERT INTO sys_menu (id, parent_id, menu_type, menu_name, path, component, permission, icon, sort_order, is_visible, is_system, create_time, update_time, deleted) VALUES
(14, 10, 'menu', '评论管理', 'comment', 'blog/comment/index', 'blog:comment:view', 'MessageOutlined', 4, true, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false)
ON CONFLICT (id) DO NOTHING;

-- 评论管理按钮
INSERT INTO sys_menu (id, parent_id, menu_type, menu_name, path, component, permission, icon, sort_order, is_visible, is_system, create_time, update_time, deleted) VALUES
(1101, 14, 'button', '评论查询', '', '', 'blog:comment:query', '', 1, true, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false),
(1102, 14, 'button', '评论审核', '', '', 'blog:comment:audit', '', 2, true, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false),
(1103, 14, 'button', '评论删除', '', '', 'blog:comment:delete', '', 3, true, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false)
ON CONFLICT (id) DO NOTHING;

-- IM管理（目录）
INSERT INTO sys_menu (id, parent_id, menu_type, menu_name, path, component, permission, icon, sort_order, is_visible, is_system, create_time, update_time, deleted) VALUES
(15, 0, 'directory', 'IM管理', '/im', NULL, NULL, 'MessageOutlined', 4, true, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false)
ON CONFLICT (id) DO NOTHING;

-- 敏感词管理（菜单）
INSERT INTO sys_menu (id, parent_id, menu_type, menu_name, path, component, permission, icon, sort_order, is_visible, is_system, create_time, update_time, deleted) VALUES
(16, 15, 'menu', '敏感词管理', 'sensitive', 'im/sensitive/index', 'im:sensitive:view', 'AlertOutlined', 1, true, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false)
ON CONFLICT (id) DO NOTHING;

-- 敏感词管理按钮
INSERT INTO sys_menu (id, parent_id, menu_type, menu_name, path, component, permission, icon, sort_order, is_visible, is_system, create_time, update_time, deleted) VALUES
(1201, 16, 'button', '敏感词查询', '', '', 'im:sensitive:query', '', 1, true, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false),
(1202, 16, 'button', '敏感词新增', '', '', 'im:sensitive:add', '', 2, true, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false),
(1203, 16, 'button', '敏感词编辑', '', '', 'im:sensitive:edit', '', 3, true, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false),
(1204, 16, 'button', '敏感词删除', '', '', 'im:sensitive:delete', '', 4, true, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false)
ON CONFLICT (id) DO NOTHING;

-- 消息管理（菜单）
INSERT INTO sys_menu (id, parent_id, menu_type, menu_name, path, component, permission, icon, sort_order, is_visible, is_system, create_time, update_time, deleted) VALUES
(17, 15, 'menu', '消息管理', 'message', 'im/message/index', 'im:message:view', 'MessageOutlined', 2, true, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false)
ON CONFLICT (id) DO NOTHING;

-- 消息管理按钮
INSERT INTO sys_menu (id, parent_id, menu_type, menu_name, path, component, permission, icon, sort_order, is_visible, is_system, create_time, update_time, deleted) VALUES
(1301, 17, 'button', '消息查询', '', '', 'im:message:query', '', 1, true, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false),
(1302, 17, 'button', '消息撤回', '', '', 'im:message:recall', '', 2, true, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false)
ON CONFLICT (id) DO NOTHING;

-- 聊天室管理（菜单）
INSERT INTO sys_menu (id, parent_id, menu_type, menu_name, path, component, permission, icon, sort_order, is_visible, is_system, create_time, update_time, deleted) VALUES
(18, 15, 'menu', '聊天室管理', 'room', 'im/room/index', 'im:room:view', 'TeamOutlined', 3, true, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false)
ON CONFLICT (id) DO NOTHING;

-- 聊天室管理按钮
INSERT INTO sys_menu (id, parent_id, menu_type, menu_name, path, component, permission, icon, sort_order, is_visible, is_system, create_time, update_time, deleted) VALUES
(1401, 18, 'button', '聊天室查询', '', '', 'im:room:query', '', 1, true, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false),
(1402, 18, 'button', '聊天室管理', '', '', 'im:room:manage', '', 2, true, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false)
ON CONFLICT (id) DO NOTHING;

-- 用户封禁管理（菜单）
INSERT INTO sys_menu (id, parent_id, menu_type, menu_name, path, component, permission, icon, sort_order, is_visible, is_system, create_time, update_time, deleted) VALUES
(19, 15, 'menu', '用户封禁', 'ban', 'im/ban/index', 'im:ban:view', 'StopOutlined', 4, true, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false)
ON CONFLICT (id) DO NOTHING;

-- 用户封禁管理按钮
INSERT INTO sys_menu (id, parent_id, menu_type, menu_name, path, component, permission, icon, sort_order, is_visible, is_system, create_time, update_time, deleted) VALUES
(1501, 19, 'button', '封禁查询', '', '', 'im:ban:query', '', 1, true, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false),
(1502, 19, 'button', '封禁用户', '', '', 'im:ban:add', '', 2, true, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false),
(1503, 19, 'button', '解封用户', '', '', 'im:ban:remove', '', 3, true, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false)
ON CONFLICT (id) DO NOTHING;

-- ============================================
-- 2. 初始化角色数据
-- ============================================
INSERT INTO sys_role (id, role_name, role_code, data_scope, description, is_system, sort_order, status, create_time, update_time, deleted) VALUES
(1, '超级管理员', 'super_admin', 'ALL', '超级管理员，拥有所有权限，不可删除', true, 1, 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false),
(2, '管理员', 'admin', 'ALL', '管理员，可以管理系统和内容', false, 2, 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false),
(3, '普通用户', 'user', 'SELF', '普通用户，只能管理自己的内容', false, 3, 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false)
ON CONFLICT (id) DO NOTHING;

-- ============================================
-- 3. 为超级管理员分配所有菜单
-- ============================================
-- 这里需要先有菜单ID，假设菜单ID 1-19
INSERT INTO sys_role_menu (role_id, menu_id, create_time)
SELECT 1, id, CURRENT_TIMESTAMP FROM sys_menu
WHERE NOT EXISTS (
    SELECT 1 FROM sys_role_menu WHERE role_id = 1 AND menu_id = sys_menu.id
);

-- ============================================
-- 4. 初始化字典数据
-- ============================================

-- 字典类型：用户状态
INSERT INTO sys_dict_type (id, dict_name, dict_code, description, is_system, status, create_time, update_time, deleted) VALUES
(1, '用户状态', 'sys_user_status', '用户状态：0-禁用，1-启用', true, 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false)
ON CONFLICT (id) DO NOTHING;

INSERT INTO sys_dict_item (id, dict_type_id, dict_label, dict_value, sort_order, status, is_default, css_class, list_class, create_time, update_time, deleted) VALUES
(1, 1, '禁用', '0', 1, 'ACTIVE', false, '', 'danger', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false),
(2, 1, '启用', '1', 2, 'ACTIVE', true, '', 'success', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false)
ON CONFLICT (id) DO NOTHING;

-- 字典类型：角色状态
INSERT INTO sys_dict_type (id, dict_name, dict_code, description, is_system, status, create_time, update_time, deleted) VALUES
(2, '角色状态', 'sys_role_status', '角色状态：ACTIVE-启用，DISABLED-禁用', true, 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false)
ON CONFLICT (id) DO NOTHING;

INSERT INTO sys_dict_item (id, dict_type_id, dict_label, dict_value, sort_order, status, is_default, css_class, list_class, create_time, update_time, deleted) VALUES
(3, 2, '禁用', 'DISABLED', 1, 'ACTIVE', false, '', 'danger', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false),
(4, 2, '启用', 'ACTIVE', 2, 'ACTIVE', true, '', 'success', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false)
ON CONFLICT (id) DO NOTHING;

-- 字典类型：数据权限范围
INSERT INTO sys_dict_type (id, dict_name, dict_code, description, is_system, status, create_time, update_time, deleted) VALUES
(3, '数据权限范围', 'sys_data_scope', '数据权限范围：ALL-全部数据，SELF-仅本人数据', true, 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false)
ON CONFLICT (id) DO NOTHING;

INSERT INTO sys_dict_item (id, dict_type_id, dict_label, dict_value, sort_order, status, is_default, css_class, list_class, create_time, update_time, deleted) VALUES
(5, 3, '全部数据', 'ALL', 1, 'ACTIVE', false, '', 'primary', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false),
(6, 3, '仅本人数据', 'SELF', 2, 'ACTIVE', true, '', 'default', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false)
ON CONFLICT (id) DO NOTHING;

-- 字典类型：文章状态
INSERT INTO sys_dict_type (id, dict_name, dict_code, description, is_system, status, create_time, update_time, deleted) VALUES
(4, '文章状态', 'blog_article_status', '文章状态：DRAFT-草稿，PENDING-待审核，PUBLISHED-已发布，REJECTED-已拒绝', true, 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false)
ON CONFLICT (id) DO NOTHING;

INSERT INTO sys_dict_item (id, dict_type_id, dict_label, dict_value, sort_order, status, is_default, css_class, list_class, create_time, update_time, deleted) VALUES
(7, 4, '草稿', 'DRAFT', 1, 'ACTIVE', true, '', 'default', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false),
(8, 4, '待审核', 'PENDING', 2, 'ACTIVE', false, '', 'warning', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false),
(9, 4, '已发布', 'PUBLISHED', 3, 'ACTIVE', false, '', 'success', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false),
(10, 4, '已拒绝', 'REJECTED', 4, 'ACTIVE', false, '', 'danger', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false)
ON CONFLICT (id) DO NOTHING;

-- 字典类型：评论状态
INSERT INTO sys_dict_type (id, dict_name, dict_code, description, is_system, status, create_time, update_time, deleted) VALUES
(5, '评论状态', 'blog_comment_status', '评论状态：PENDING-待审核，APPROVED-已通过，REJECTED-已拒绝', true, 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false)
ON CONFLICT (id) DO NOTHING;

INSERT INTO sys_dict_item (id, dict_type_id, dict_label, dict_value, sort_order, status, is_default, css_class, list_class, create_time, update_time, deleted) VALUES
(11, 5, '待审核', 'PENDING', 1, 'ACTIVE', true, '', 'warning', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false),
(12, 5, '已通过', 'APPROVED', 2, 'ACTIVE', false, '', 'success', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false),
(13, 5, '已拒绝', 'REJECTED', 3, 'ACTIVE', false, '', 'danger', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false)
ON CONFLICT (id) DO NOTHING;

-- 字典类型：敏感词类型
INSERT INTO sys_dict_type (id, dict_name, dict_code, description, is_system, status, create_time, update_time, deleted) VALUES
(6, '敏感词类型', 'im_sensitive_type', '敏感词类型：NORMAL-普通，POLITICAL-政治，PORN-色情，VIOLENCE-暴力', true, 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false)
ON CONFLICT (id) DO NOTHING;

INSERT INTO sys_dict_item (id, dict_type_id, dict_label, dict_value, sort_order, status, is_default, css_class, list_class, create_time, update_time, deleted) VALUES
(14, 6, '普通', 'NORMAL', 1, 'ACTIVE', true, '', 'default', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false),
(15, 6, '政治', 'POLITICAL', 2, 'ACTIVE', false, '', 'danger', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false),
(16, 6, '色情', 'PORN', 3, 'ACTIVE', false, '', 'warning', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false),
(17, 6, '暴力', 'VIOLENCE', 4, 'ACTIVE', false, '', 'danger', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false)
ON CONFLICT (id) DO NOTHING;

-- 字典类型：封禁类型
INSERT INTO sys_dict_type (id, dict_name, dict_code, description, is_system, status, create_time, update_time, deleted) VALUES
(7, '封禁类型', 'im_ban_type', '封禁类型：CHAT-禁言，LOGIN-禁止登录，FULL-全封禁', true, 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false)
ON CONFLICT (id) DO NOTHING;

INSERT INTO sys_dict_item (id, dict_type_id, dict_label, dict_value, sort_order, status, is_default, css_class, list_class, create_time, update_time, deleted) VALUES
(18, 7, '禁言', 'CHAT', 1, 'ACTIVE', true, '', 'warning', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false),
(19, 7, '禁止登录', 'LOGIN', 2, 'ACTIVE', false, '', 'danger', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false),
(20, 7, '全封禁', 'FULL', 3, 'ACTIVE', false, '', 'danger', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false)
ON CONFLICT (id) DO NOTHING;

-- ============================================
-- 5. 为现有用户分配角色（如果存在ID为1的用户，设为超级管理员）
-- ============================================
INSERT INTO sys_user_role (user_id, role_id, create_time)
SELECT 1, 1, CURRENT_TIMESTAMP
WHERE EXISTS (SELECT 1 FROM sys_users WHERE id = 1)
  AND NOT EXISTS (SELECT 1 FROM sys_user_role WHERE user_id = 1 AND role_id = 1);
