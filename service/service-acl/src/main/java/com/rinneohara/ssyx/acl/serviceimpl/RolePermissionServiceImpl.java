package com.rinneohara.ssyx.acl.serviceimpl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rinneohara.ssyx.acl.mapper.PermissionMapper;
import com.rinneohara.ssyx.acl.mapper.RolePermissionMapper;
import com.rinneohara.ssyx.acl.service.PermissionService;
import com.rinneohara.ssyx.acl.service.RolePermissionService;
import com.rinneohara.ssyx.model.acl.Permission;
import com.rinneohara.ssyx.model.acl.RolePermission;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @PROJECT_NAME: rinne-ssyx-parent
 * @DESCRIPTION:
 * @USER: Administrator
 * @DATE: 2023/8/4 15:20
 */
@Service
public class RolePermissionServiceImpl extends ServiceImpl<RolePermissionMapper, RolePermission>implements RolePermissionService  {
    @Autowired
    private RolePermissionMapper rolePermissionMapper;
    @Autowired
    private PermissionMapper permissionMapper;

    @Autowired
    private PermissionService permissionService;
    @Override
    public Map<String, Object> selectByRoleId(Long roleId) {
        Map<String,Object> hashmap=new HashMap<>();
        List<Long> permissionIdList =new ArrayList<>();
        List<RolePermission> rolePermissions = rolePermissionMapper.selectList(new LambdaQueryWrapper<RolePermission>().eq(RolePermission::getRoleId, roleId));
        rolePermissions.stream().forEach(item->{
            permissionIdList.add(item.getPermissionId());
        });
        List<Permission> permissionList = permissionService.queryAllMenu();
        //当前角色所有可选的权限
        hashmap.put("permissionList",permissionList);
        //当前角色已经选中的权限
        hashmap.put("permissionIdList",permissionIdList);
        return hashmap;
    }

    @Override
    public void doAssign(Long roleId, Long[] permissionId) {
        rolePermissionMapper.removeAllByRoleId(roleId);
        if (permissionId.length != 0){
            rolePermissionMapper.assginRole(roleId,permissionId);
        }
    }
}
