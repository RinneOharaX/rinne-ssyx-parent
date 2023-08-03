package com.rinneohara.ssyx.acl.mapper;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rinneohara.ssyx.model.acl.AdminRole;
import com.rinneohara.ssyx.model.acl.Role;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;


@Mapper
public interface AdminRoleMapper extends BaseMapper<AdminRole> {

    public List<Role> selectRoleByAdminId(Long adminId);

    void removeAllRoleByAdminId(Long adminId);

    void assginRole(Long adminId,Long[] assignedRoleId);
}
