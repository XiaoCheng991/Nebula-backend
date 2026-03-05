package com.nebula.service.impl.system;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nebula.model.entity.system.SysRoleMenu;
import com.nebula.service.mapper.system.SysRoleMenuMapper;
import com.nebula.service.service.system.SysRoleMenuService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SysRoleMenuServiceImpl extends ServiceImpl<SysRoleMenuMapper, SysRoleMenu> implements SysRoleMenuService {
}
