package com.rinneohara.ssyx.acl.serviceimpl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rinneohara.ssyx.acl.mapper.RoleMapper;
import com.rinneohara.ssyx.acl.service.RoleService;
import com.rinneohara.ssyx.model.acl.Role;
import org.springframework.stereotype.Service;

/**
 * @PROJECT_NAME: rinne-ssyx-parent
 * @DESCRIPTION:
 * @USER: Administrator
 * @DATE: 2023/8/1 16:32
 */
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {
}
