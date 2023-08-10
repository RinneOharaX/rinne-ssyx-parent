package com.rinneohara.ssyx.serviceImpl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rinneohara.ssyx.mapper.AttrGroupMapper;
import com.rinneohara.ssyx.model.product.AttrGroup;
import com.rinneohara.ssyx.service.AttrGroupService;
import org.springframework.stereotype.Service;

/**
 * @PROJECT_NAME: rinne-ssyx-parent
 * @DESCRIPTION:
 * @USER: Administrator
 * @DATE: 2023/8/10 13:40
 */
@Service
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupMapper,AttrGroup> implements AttrGroupService {
}
