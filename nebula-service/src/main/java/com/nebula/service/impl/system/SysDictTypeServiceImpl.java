package com.nebula.service.impl.system;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nebula.model.entity.system.SysDictType;
import com.nebula.service.mapper.system.SysDictTypeMapper;
import com.nebula.service.service.system.SysDictTypeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SysDictTypeServiceImpl extends ServiceImpl<SysDictTypeMapper, SysDictType> implements SysDictTypeService {
}
