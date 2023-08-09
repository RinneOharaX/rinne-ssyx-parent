package com.rinneohara.ssyx.acl.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rinneohara.ssyx.model.acl.RolePermission;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface RolePermissionMapper extends BaseMapper<RolePermission> {

    void assginRole(Long roleId,Long[] permissionId);

    void removeAllByRoleId(Long roleId);
}
