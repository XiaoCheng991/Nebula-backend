package com.nebula.service.impl.system;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nebula.model.entity.system.SysUserRole;
import com.nebula.service.mapper.system.SysUserRoleMapper;
import com.nebula.service.service.system.SysUserRoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SysUserRoleServiceImpl extends ServiceImpl<SysUserRoleMapper, SysUserRole> implements SysUserRoleService {
}
