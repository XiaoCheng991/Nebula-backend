package com.nebula.service.service.system;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nebula.model.entity.system.SysMenu;

import java.util.List;

public interface SysMenuService extends IService<SysMenu> {

    List<SysMenu> getMenuListByUserId(Long userId);

    List<SysMenu> getMenuListByRoleId(Long roleId);

    List<String> getPermsByUserId(Long userId);

    List<SysMenu> buildMenuTree(List<SysMenu> menus);

    void deleteMenu(Long menuId);
}
