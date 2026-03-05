package com.nebula.service.impl.system;

import com.nebula.common.constant.AdminConstants;
import com.nebula.model.entity.system.SysRole;
import com.nebula.service.service.system.PermissionService;
import com.nebula.service.service.system.SysMenuService;
import com.nebula.service.service.system.SysRoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PermissionServiceImpl implements PermissionService {

    private final SysMenuService sysMenuService;
    private final SysRoleService sysRoleService;

    @Override
    public boolean hasPermission(Long userId, String permission) {
        if (permission == null || permission.isEmpty()) {
            return true;
        }

        if (sysRoleService.isSuperAdmin(userId)) {
            return true;
        }

        Set<String> permissions = getUserPermissions(userId);
        return permissions.contains(permission) || permissions.contains("*:*:*");
    }

    @Override
    public Set<String> getUserPermissions(Long userId) {
        List<String> perms = sysMenuService.getPermsByUserId(userId);
        return perms.stream().filter(p -> p != null && !p.isEmpty()).collect(Collectors.toSet());
    }

    @Override
    public String getDataScopeSql(Long userId, String tableAlias) {
        if (sysRoleService.isSuperAdmin(userId)) {
            return "";
        }

        List<SysRole> roles = sysRoleService.getRolesByUserId(userId);
        boolean hasAllDataScope = roles.stream()
                .anyMatch(r -> AdminConstants.ALL_DATA_SCOPE.equals(r.getDataScope()));

        if (hasAllDataScope) {
            return "";
        }

        String prefix = tableAlias != null && !tableAlias.isEmpty() ? tableAlias + "." : "";
        return prefix + "create_by = " + userId;
    }
}
