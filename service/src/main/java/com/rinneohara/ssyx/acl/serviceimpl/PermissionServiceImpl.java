package com.rinneohara.ssyx.acl.serviceimpl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rinneohara.ssyx.acl.mapper.PermissionMapper;
import com.rinneohara.ssyx.acl.service.PermissionService;
import com.rinneohara.ssyx.model.acl.Permission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @PROJECT_NAME: rinne-ssyx-parent
 * @DESCRIPTION:
 * @USER: Administrator
 * @DATE: 2023/8/3 16:39
 */
@Service
public class PermissionServiceImpl extends ServiceImpl<PermissionMapper, Permission>implements PermissionService {
    @Autowired
    private PermissionMapper permissionMapper;
    @Override
    public List<Permission> queryAllMenu() {
        List<Permission> permissions = permissionMapper.selectList(null);

    }
}
