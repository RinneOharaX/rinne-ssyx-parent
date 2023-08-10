package com.rinneohara.ssyx.serviceImpl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rinneohara.ssyx.mapper.CommentReplayMapper;
import com.rinneohara.ssyx.model.product.CommentReplay;
import com.rinneohara.ssyx.service.CommentReplayService;
import org.springframework.stereotype.Service;

/**
 * @PROJECT_NAME: rinne-ssyx-parent
 * @DESCRIPTION:
 * @USER: Administrator
 * @DATE: 2023/8/10 13:54
 */
@Service
public class CommentReplayServiceImpl extends ServiceImpl<CommentReplayMapper, CommentReplay> implements CommentReplayService {
}
