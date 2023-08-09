package com.rinneohara.ssyx.acl.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.rinneohara.ssyx.model.acl.Permission;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface PermissionService extends IService<Permission> {
   List<Permission> queryAllMenu();

   public boolean removeChildById(Long id);
}
