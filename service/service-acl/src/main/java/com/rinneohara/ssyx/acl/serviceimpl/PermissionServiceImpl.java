package com.rinneohara.ssyx.acl.serviceimpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rinneohara.ssyx.acl.mapper.PermissionMapper;
import com.rinneohara.ssyx.acl.service.PermissionService;
import com.rinneohara.ssyx.model.acl.Permission;
import com.rinneohara.utils.TreeUitl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
    public  List<Permission> queryAllMenu() {
        List<Permission> permissions = permissionMapper.selectList(null);
        List<Permission> treeType = TreeUitl.getTreeType(permissions);
        return treeType;
    }

    public boolean removeChildById(Long id) {
        List<Long> idList = new ArrayList<>();
        this.selectChildListById(id, idList);
        idList.add(id);
        baseMapper.deleteBatchIds(idList);
        return true;
    }

    private void selectChildListById(Long id, List<Long> idList) {
        List<Permission> childList = baseMapper.selectList(new QueryWrapper<Permission>().eq("pid", id).select("id"));
        childList.stream().forEach(item -> {
            idList.add(item.getId());
            this.selectChildListById(item.getId(), idList);
        });
    }
}
