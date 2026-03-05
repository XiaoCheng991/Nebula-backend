package com.nebula.service.mapper.system;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nebula.model.entity.system.SysMenu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SysMenuMapper extends BaseMapper<SysMenu> {

    List<SysMenu> selectMenuListByUserId(@Param("userId") Long userId);

    List<SysMenu> selectMenuListByRoleId(@Param("roleId") Long roleId);

    List<String> selectPermsByUserId(@Param("userId") Long userId);
}
