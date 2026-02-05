package com.nebula.service.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nebula.model.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;

/**
 * 系统用户Mapper
 */
@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {
}
