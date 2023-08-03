package com.rinneohara.ssyx.acl.service;


import com.baomidou.mybatisplus.core.mapper.Mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.rinneohara.ssyx.common.result.Result;
import com.rinneohara.ssyx.model.acl.Admin;
import com.rinneohara.ssyx.model.acl.Role;
import com.rinneohara.ssyx.vo.acl.AdminLoginVo;
import com.rinneohara.ssyx.vo.acl.AdminQueryVo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public interface IndexService extends IService<Admin> {

    public IPage<Admin> selectPage(Page<Admin> page,AdminQueryVo adminQueryVo);

    Map<String, Object> findRoleByAdminId(Long adminId);

    void doAssign(Long adminId, Long[] assignedRoleId);
}
