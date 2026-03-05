package com.nebula.service.service.system;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nebula.model.entity.system.SysRole;

import java.util.List;

public interface SysRoleService extends IService<SysRole> {

    List<SysRole> getRolesByUserId(Long userId);

    List<Long> getMenuIdsByRoleId(Long roleId);

    void saveRoleMenus(Long roleId, List<Long> menuIds);

    void assignRolesToUser(Long userId, List<Long> roleIds);

    void deleteRole(Long roleId);

    boolean hasRole(Long userId, String roleCode);

    boolean isSuperAdmin(Long userId);
}
