package com.rinneohara.ssyx.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rinneohara.ssyx.mapper.OrderSetMapper;
import com.rinneohara.ssyx.model.order.OrderSet;
import com.rinneohara.ssyx.service.OrderSetService;
import org.springframework.stereotype.Service;

/**
 * @PROJECT_NAME: rinne-ssyx-parent
 * @DESCRIPTION:
 * @USER: Administrator
 * @DATE: 2023/10/10 16:34
 */
@Service
public class OrderSetServiceImpl extends ServiceImpl<OrderSetMapper, OrderSet> implements OrderSetService {
}
