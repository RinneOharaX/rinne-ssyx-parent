package com.rinneohara.ssyx.acl.controller;

import com.rinneohara.ssyx.acl.service.PermissionService;
import com.rinneohara.ssyx.acl.service.RolePermissionService;
import com.rinneohara.ssyx.common.result.Result;
import com.rinneohara.ssyx.model.acl.Permission;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @PROJECT_NAME: rinne-ssyx-parent
 * @DESCRIPTION:
 * @USER: Administrator
 * @DATE: 2023/8/3 16:40
 */

@RestController
@RequestMapping("/admin/acl/permission")
@Api(tags = "菜单管理")
public class PermissionController {
    @Autowired
    private PermissionService permissionService;

    @Autowired
    private RolePermissionService rolePermissionService;

    @ApiOperation(value = "获取菜单")
    @GetMapping
    public Result index() {
        List<Permission> permission = permissionService.queryAllMenu();
        return Result.ok(permission);
    }

    @ApiOperation(value = "新增菜单")
    @PostMapping("/save")
    public Result savePermission(@RequestBody Permission permission){
        permissionService.save(permission);
        return  Result.ok("新增菜单成功");
    }



    @ApiOperation(value = "修改菜单")
    @PutMapping("/update")
    public Result updatePermission(@RequestBody Permission permission){
        permissionService.updateById(permission);
        return Result.ok("修改菜单成功");
    }

    @ApiOperation(value = "递归删除菜单")
    @DeleteMapping("/remove/{id}")
    public Result removePermissionById(@PathVariable Long id){
        permissionService.removeChildById(id);
        return Result.ok("删除菜单成功");
    }


    @ApiOperation("找到指定角色id已有的权限")
    @GetMapping("/toAssign/{roleId}")
    public Result toAssignPermission(@PathVariable Long roleId){
       Map<String,Object> rolePermissionMap = rolePermissionService.selectByRoleId(roleId);
        return Result.ok(rolePermissionMap);
    }


    //doAssign(roleId, permissionId) {
    //    return request({
    //      url: `${api_name}/doAssign`,
    //      method: "post",
    //      params: {roleId, permissionId}
    //    })


    @ApiOperation("分配权限")
    @PostMapping("/doAssign")
    public Result doAssign(@RequestParam Long roleId,
                           @RequestParam Long[] permissionId){
        rolePermissionService.doAssign(roleId,permissionId);
        return Result.ok("分配权限成功");

    }
}
