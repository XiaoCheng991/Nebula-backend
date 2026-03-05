-- ============================================
-- 初始化简单数据 - 用于联调
-- ============================================

-- 插入角色
INSERT INTO sys_role (id, role_name, role_code, data_scope, description, is_system, sort_order, status, create_time, update_time, deleted) VALUES
(1, '超级管理员', 'super_admin', 'ALL', '超级管理员，拥有所有权限，不可删除', true, 1, 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false),
(2, '管理员', 'admin', 'ALL', '管理员，可以管理系统和内容', false, 2, 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false),
(3, '普通用户', 'user', 'SELF', '普通用户，只能管理自己的内容', false, 3, 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false)
ON CONFLICT (id) DO NOTHING;

-- 插入简单菜单
INSERT INTO sys_menu (id, parent_id, menu_type, menu_name, path, component, permission, icon, sort_order, is_visible, is_system, create_time, update_time, deleted) VALUES
(1, 0, 'directory', '系统管理', '/system', NULL, NULL, 'SettingOutlined', 1, true, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false),
(2, 1, 'menu', '用户管理', 'user', 'system/user/index', 'system:user:view', 'UserOutlined', 1, true, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false),
(3, 1, 'menu', '角色管理', 'role', 'system/role/index', 'system:role:view', 'TeamOutlined', 2, true, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false),
(4, 1, 'menu', '菜单管理', 'menu', 'system/menu/index', 'system:menu:view', 'MenuOutlined', 3, true, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false),
(5, 1, 'menu', '字典管理', 'dict', 'system/dict/index', 'system:dict:view', 'BookOutlined', 4, true, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, false)
ON CONFLICT (id) DO NOTHING;

-- 为超级管理员分配所有菜单
INSERT INTO sys_role_menu (role_id, menu_id, create_time)
SELECT 1, id, CURRENT_TIMESTAMP FROM sys_menu
WHERE NOT EXISTS (
    SELECT 1 FROM sys_role_menu WHERE role_id = 1 AND menu_id = sys_menu.id
);

-- 如果有用户ID为1的用户，分配超级管理员角色
INSERT INTO sys_user_role (user_id, role_id, create_time)
SELECT 1, 1, CURRENT_TIMESTAMP
WHERE EXISTS (SELECT 1 FROM sys_users WHERE id = 1)
  AND NOT EXISTS (SELECT 1 FROM sys_user_role WHERE user_id = 1 AND role_id = 1);

-- 为其他用户分配普通用户角色
INSERT INTO sys_user_role (user_id, role_id, create_time)
SELECT id, 3, CURRENT_TIMESTAMP FROM sys_users
WHERE id != 1
  AND NOT EXISTS (SELECT 1 FROM sys_user_role WHERE user_id = sys_users.id);
