package com.rinneohara.ssyx.acl.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rinneohara.ssyx.model.acl.Permission;
import org.apache.ibatis.annotations.Mapper;


@Mapper
public interface PermissionMapper extends BaseMapper<Permission> {
}
