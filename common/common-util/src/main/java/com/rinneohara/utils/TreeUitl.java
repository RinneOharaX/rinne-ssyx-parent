package com.rinneohara.utils;

import com.rinneohara.ssyx.model.acl.Permission;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @PROJECT_NAME: rinne-ssyx-parent
 * @DESCRIPTION:
 * @USER: Administrator
 * @DATE: 2023/8/3 16:42
 */

//处理树形结构的数据，通过递归找到子数据，判断递归是否结束的依据是是否存在子数据
//这种结构类似于链表，最后返回头结点就可以了
public class TreeUitl {
    public static  List<Permission> getTreeType(List<Permission> permissions){
        List<Permission> rootlist=new ArrayList<>();
        for (Permission permission:permissions){
            if (permission.getPid()==0){
                getchildren(permissions,permission);
                rootlist.add(permission);
            }
        }
        return rootlist;
    }

    private static void getchildren(List<Permission> permissions,Permission permission) {
        List<Permission> childrenList=new ArrayList<>();
        for(Permission permissionChild:permissions){
            if (Objects.equals(permission.getId(), permissionChild.getPid())){
                childrenList.add(permissionChild);
            }
            if (!childrenList.isEmpty()) {
                getchildren(permissions, permissionChild);
            }
        }
        permission.setChildren(childrenList);
    }
}
