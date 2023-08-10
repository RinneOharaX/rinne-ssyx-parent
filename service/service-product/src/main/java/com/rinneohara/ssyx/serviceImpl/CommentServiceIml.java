package com.rinneohara.ssyx.serviceImpl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rinneohara.ssyx.mapper.CommentMapper;
import com.rinneohara.ssyx.model.product.Comment;
import com.rinneohara.ssyx.service.CommentService;
import org.springframework.stereotype.Service;

/**
 * @PROJECT_NAME: rinne-ssyx-parent
 * @DESCRIPTION:
 * @USER: Administrator
 * @DATE: 2023/8/10 13:49
 */
@Service
public class CommentServiceIml extends ServiceImpl<CommentMapper, Comment> implements CommentService {
}
