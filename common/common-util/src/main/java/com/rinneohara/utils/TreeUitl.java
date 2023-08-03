package com.rinneohara.utils;

import com.rinneohara.ssyx.model.acl.Permission;

import java.util.List;

/**
 * @PROJECT_NAME: rinne-ssyx-parent
 * @DESCRIPTION:
 * @USER: Administrator
 * @DATE: 2023/8/3 16:42
 */
public class TreeUitl {
    public static List getTreeType(List<Permission> permissions){
        for (Permission permission:permissions){
            if (permission.getPid()==0){
                permission.getChildren();
            }
        }
    }
}
