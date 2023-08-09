package com.rinneohara.ssyx.acl.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.rinneohara.ssyx.model.acl.RolePermission;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public interface RolePermissionService extends IService<RolePermission> {

    public Map<String,Object> selectByRoleId(Long roleId);

    void doAssign(Long roleId,Long[] permissionId);

}
