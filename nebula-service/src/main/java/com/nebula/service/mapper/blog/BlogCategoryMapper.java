package com.nebula.service.mapper.blog;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nebula.model.entity.blog.BlogCategory;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BlogCategoryMapper extends BaseMapper<BlogCategory> {
}
