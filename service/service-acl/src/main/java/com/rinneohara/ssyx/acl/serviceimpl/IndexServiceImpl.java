package com.rinneohara.ssyx.acl.serviceimpl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rinneohara.ssyx.acl.mapper.AdminRoleMapper;
import com.rinneohara.ssyx.acl.mapper.IndexMapper;
import com.rinneohara.ssyx.acl.mapper.RoleMapper;
import com.rinneohara.ssyx.acl.service.IndexService;
import com.rinneohara.ssyx.common.result.Result;
import com.rinneohara.ssyx.model.acl.Admin;
import com.rinneohara.ssyx.model.acl.Role;
import com.rinneohara.ssyx.vo.acl.AdminLoginVo;
import com.rinneohara.ssyx.vo.acl.AdminQueryVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.sql.Wrapper;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @PROJECT_NAME: rinne-ssyx-parent
 * @DESCRIPTION:
 * @USER: Administrator
 * @DATE: 2023/8/1 13:10
 */

@Service
public class IndexServiceImpl extends ServiceImpl<IndexMapper, Admin> implements IndexService  {

    @Autowired
    private IndexMapper indexMapper;

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private AdminRoleMapper adminRoleMapper;


    @Override
    public IPage<Admin> selectPage(Page<Admin> page,AdminQueryVo adminQueryVo) {
        LambdaQueryWrapper<Admin> wrapper = new LambdaQueryWrapper<>();
        String name = adminQueryVo.getUsername();
        if (!StringUtils.isEmpty(name)){
             wrapper.like(Admin::getUsername, name);
        }
        IPage<Admin> adminIPage = indexMapper.selectPage(page, wrapper);
        return adminIPage;
}

    @Override
    public Map<String, Object> findRoleByAdminId(Long adminId) {
        List<Role> allRolesList = roleMapper.selectList(null);
        Map<String,Object> roleMap=new HashMap<>();
        List<Role> assignRoles = adminRoleMapper.selectRoleByAdminId(adminId);
        roleMap.put("allRolesList",allRolesList);
        roleMap.put("assignRoles",assignRoles);
        return roleMap;
    }

    @Override
    public void doAssign(Long adminId,Long[] assignedRoleId) {
        //根据用户id删除其之前的角色
        adminRoleMapper.removeAllRoleByAdminId(adminId);
        if (assignedRoleId!=null && assignedRoleId.length!=0){
            //根据传进来的角色id集合分配新的角色
            adminRoleMapper.assginRole(adminId,assignedRoleId);
        }
    }
}
