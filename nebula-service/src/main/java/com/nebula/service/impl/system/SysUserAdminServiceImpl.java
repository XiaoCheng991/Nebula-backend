package com.nebula.service.impl.system;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nebula.model.entity.SysUser;
import com.nebula.service.mapper.SysUserMapper;
import com.nebula.service.service.system.SysUserAdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SysUserAdminServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserAdminService {
}
