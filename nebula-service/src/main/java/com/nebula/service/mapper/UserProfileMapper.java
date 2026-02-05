package com.nebula.service.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nebula.model.entity.UserProfile;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户档案Mapper
 */
@Mapper
public interface UserProfileMapper extends BaseMapper<UserProfile> {
}
