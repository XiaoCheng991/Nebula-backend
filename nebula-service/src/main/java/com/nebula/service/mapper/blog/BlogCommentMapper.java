package com.nebula.service.mapper.blog;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nebula.model.entity.blog.BlogComment;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BlogCommentMapper extends BaseMapper<BlogComment> {
}
