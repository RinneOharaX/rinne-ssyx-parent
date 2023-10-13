package com.rinneohara.ssyx.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rinneohara.ssyx.mapper.OrderItemMapper;
import com.rinneohara.ssyx.model.order.OrderItem;
import com.rinneohara.ssyx.service.OrderItemService;
import org.springframework.stereotype.Service;

/**
 * @PROJECT_NAME: rinne-ssyx-parent
 * @DESCRIPTION:
 * @USER: Administrator
 * @DATE: 2023/10/10 16:34
 */
@Service
public class OrderItemServiceImpl extends ServiceImpl<OrderItemMapper,OrderItem> implements OrderItemService  {


}
