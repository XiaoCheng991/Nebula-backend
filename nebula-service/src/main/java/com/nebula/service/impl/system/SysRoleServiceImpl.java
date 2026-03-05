package com.nebula.service.impl.system;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nebula.common.constant.AdminConstants;
import com.nebula.common.exception.BusinessException;
import com.nebula.common.exception.ErrorCode;
import com.nebula.model.entity.system.SysRole;
import com.nebula.model.entity.system.SysRoleMenu;
import com.nebula.model.entity.system.SysUserRole;
import com.nebula.service.mapper.system.SysRoleMapper;
import com.nebula.service.mapper.system.SysRoleMenuMapper;
import com.nebula.service.mapper.system.SysUserRoleMapper;
import com.nebula.service.service.system.SysRoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {

    private final SysRoleMenuMapper sysRoleMenuMapper;
    private final SysUserRoleMapper sysUserRoleMapper;

    @Override
    public List<SysRole> getRolesByUserId(Long userId) {
        return baseMapper.selectList(
                new LambdaQueryWrapper<SysRole>()
                        .inSql(SysRole::getId, "SELECT role_id FROM sys_user_role WHERE user_id = " + userId)
                        .eq(SysRole::getStatus, "ACTIVE")
        );
    }

    @Override
    public List<Long> getMenuIdsByRoleId(Long roleId) {
        return sysRoleMenuMapper.selectList(
                new LambdaQueryWrapper<SysRoleMenu>()
                        .eq(SysRoleMenu::getRoleId, roleId)
        ).stream().map(SysRoleMenu::getMenuId).toList();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveRoleMenus(Long roleId, List<Long> menuIds) {
        sysRoleMenuMapper.delete(
                new LambdaQueryWrapper<SysRoleMenu>()
                        .eq(SysRoleMenu::getRoleId, roleId)
        );

        if (menuIds != null && !menuIds.isEmpty()) {
            for (Long menuId : menuIds) {
                SysRoleMenu roleMenu = new SysRoleMenu();
                roleMenu.setRoleId(roleId);
                roleMenu.setMenuId(menuId);
                sysRoleMenuMapper.insert(roleMenu);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignRolesToUser(Long userId, List<Long> roleIds) {
        sysUserRoleMapper.delete(
                new LambdaQueryWrapper<SysUserRole>()
                        .eq(SysUserRole::getUserId, userId)
        );

        if (roleIds != null && !roleIds.isEmpty()) {
            for (Long roleId : roleIds) {
                SysUserRole userRole = new SysUserRole();
                userRole.setUserId(userId);
                userRole.setRoleId(roleId);
                sysUserRoleMapper.insert(userRole);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteRole(Long roleId) {
        SysRole role = getById(roleId);
        if (role == null) {
            throw new BusinessException(ErrorCode.ROLE_NOT_FOUND);
        }
        if (Boolean.TRUE.equals(role.getIsSystem())) {
            throw new BusinessException(ErrorCode.CANNOT_DELETE_SYSTEM_ROLE);
        }
        if (AdminConstants.SUPER_ADMIN_ROLE_ID.equals(roleId)) {
            throw new BusinessException(ErrorCode.CANNOT_DELETE_SYSTEM_ROLE);
        }

        sysRoleMenuMapper.delete(
                new LambdaQueryWrapper<SysRoleMenu>()
                        .eq(SysRoleMenu::getRoleId, roleId)
        );
        sysUserRoleMapper.delete(
                new LambdaQueryWrapper<SysUserRole>()
                        .eq(SysUserRole::getRoleId, roleId)
        );
        removeById(roleId);
    }

    @Override
    public boolean hasRole(Long userId, String roleCode) {
        return getRolesByUserId(userId).stream()
                .anyMatch(r -> roleCode.equals(r.getRoleCode()));
    }

    @Override
    public boolean isSuperAdmin(Long userId) {
        return AdminConstants.SUPER_ADMIN_USER_ID.equals(userId) ||
                hasRole(userId, AdminConstants.SUPER_ADMIN_ROLE_CODE);
    }
}
