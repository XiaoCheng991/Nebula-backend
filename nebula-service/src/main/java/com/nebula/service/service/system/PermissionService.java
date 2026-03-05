package com.nebula.service.service.system;

import java.util.Set;

public interface PermissionService {

    boolean hasPermission(Long userId, String permission);

    Set<String> getUserPermissions(Long userId);

    String getDataScopeSql(Long userId, String tableAlias);
}
