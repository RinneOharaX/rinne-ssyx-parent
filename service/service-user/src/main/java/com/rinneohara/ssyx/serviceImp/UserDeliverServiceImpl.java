package com.rinneohara.ssyx.serviceImp;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rinneohara.ssyx.mapper.UserDeliverMapper;
import com.rinneohara.ssyx.model.user.UserDelivery;
import com.rinneohara.ssyx.service.UserDeliverService;
import com.rinneohara.ssyx.service.UserService;
import org.springframework.stereotype.Service;

/**
 * @PROJECT_NAME: rinne-ssyx-parent
 * @DESCRIPTION:
 * @USER: Administrator
 * @DATE: 2023/9/6 15:23
 */
@Service
public class UserDeliverServiceImpl extends ServiceImpl<UserDeliverMapper, UserDelivery> implements UserDeliverService  {
}
