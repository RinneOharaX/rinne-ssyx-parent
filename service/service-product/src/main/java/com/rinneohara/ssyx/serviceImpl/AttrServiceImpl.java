package com.rinneohara.ssyx.serviceImpl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rinneohara.ssyx.mapper.AttrMapper;
import com.rinneohara.ssyx.model.product.Attr;
import com.rinneohara.ssyx.service.AttrService;
import org.springframework.stereotype.Service;

/**
 * @PROJECT_NAME: rinne-ssyx-parent
 * @DESCRIPTION:
 * @USER: Administrator
 * @DATE: 2023/8/10 13:38
 */
@Service
public class AttrServiceImpl extends ServiceImpl<AttrMapper, Attr> implements AttrService {
}
