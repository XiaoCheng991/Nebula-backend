package com.nebula.service.impl.system;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nebula.common.constant.AdminConstants;
import com.nebula.common.exception.BusinessException;
import com.nebula.common.exception.ErrorCode;
import com.nebula.model.entity.system.SysMenu;
import com.nebula.model.entity.system.SysRoleMenu;
import com.nebula.service.mapper.system.SysMenuMapper;
import com.nebula.service.mapper.system.SysRoleMenuMapper;
import com.nebula.service.service.system.SysMenuService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements SysMenuService {

    private final SysRoleMenuMapper sysRoleMenuMapper;

    @Override
    public List<SysMenu> getMenuListByUserId(Long userId) {
        return baseMapper.selectMenuListByUserId(userId);
    }

    @Override
    public List<SysMenu> getMenuListByRoleId(Long roleId) {
        return baseMapper.selectMenuListByRoleId(roleId);
    }

    @Override
    public List<String> getPermsByUserId(Long userId) {
        return baseMapper.selectPermsByUserId(userId);
    }

    @Override
    public List<SysMenu> buildMenuTree(List<SysMenu> menus) {
        // 构建 id -> name 映射，用于填充 parentName
        Map<Long, String> nameMap = menus.stream()
                .filter(m -> m.getId() != null)
                .collect(Collectors.toMap(SysMenu::getId, m -> m.getMenuName() != null ? m.getMenuName() : ""));
        for (SysMenu menu : menus) {
            if (menu.getParentId() != null && menu.getParentId() > 0) {
                menu.setParentName(nameMap.get(menu.getParentId()));
            }
        }

        List<SysMenu> tree = new ArrayList<>();
        for (SysMenu menu : menus) {
            if (menu.getParentId() == null || menu.getParentId() == 0) {
                tree.add(menu);
            }
        }
        for (SysMenu menu : tree) {
            menu.setChildren(getChildren(menu, menus));
        }
        return tree;
    }

    private List<SysMenu> getChildren(SysMenu parent, List<SysMenu> menus) {
        List<SysMenu> children = new ArrayList<>();
        for (SysMenu menu : menus) {
            if (parent.getId().equals(menu.getParentId())) {
                menu.setChildren(getChildren(menu, menus));
                children.add(menu);
            }
        }
        return children.stream().sorted((m1, m2) -> {
            Integer s1 = m1.getSortOrder() == null ? 0 : m1.getSortOrder();
            Integer s2 = m2.getSortOrder() == null ? 0 : m2.getSortOrder();
            return s1.compareTo(s2);
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteMenu(Long menuId) {
        SysMenu menu = getById(menuId);
        if (menu == null) {
            throw new BusinessException(ErrorCode.MENU_NOT_FOUND);
        }
        if (Boolean.TRUE.equals(menu.getIsSystem())) {
            throw new BusinessException(ErrorCode.CANNOT_DELETE_SYSTEM_MENU);
        }

        Long count = count(new LambdaQueryWrapper<SysMenu>().eq(SysMenu::getParentId, menuId));
        if (count > 0) {
            throw new BusinessException(ErrorCode.OPERATION_NOT_ALLOWED, "存在子菜单，无法删除");
        }

        sysRoleMenuMapper.delete(
                new LambdaQueryWrapper<SysRoleMenu>()
                        .eq(SysRoleMenu::getMenuId, menuId)
        );
        removeById(menuId);
    }
}
