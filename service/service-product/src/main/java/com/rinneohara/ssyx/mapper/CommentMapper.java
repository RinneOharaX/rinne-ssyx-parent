package com.rinneohara.ssyx.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rinneohara.ssyx.model.base.BaseEntity;
import com.rinneohara.ssyx.model.product.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.map.repository.config.EnableMapRepositories;

@Mapper
public interface CommentMapper extends BaseMapper<Comment> {
}
