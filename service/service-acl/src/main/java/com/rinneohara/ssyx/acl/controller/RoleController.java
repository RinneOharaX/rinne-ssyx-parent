package com.rinneohara.ssyx.acl.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rinneohara.ssyx.acl.service.RolePermissionService;
import com.rinneohara.ssyx.acl.service.RoleService;
import com.rinneohara.ssyx.acl.serviceimpl.RoleServiceImpl;
import com.rinneohara.ssyx.common.result.Result;
import com.rinneohara.ssyx.model.acl.Role;
import com.rinneohara.ssyx.vo.acl.RoleQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @PROJECT_NAME: rinne-ssyx-parent
 * @DESCRIPTION:
 * @USER: Administrator
 * @DATE: 2023/8/1 16:30
 */


@Api(tags = "角色管理")
@RestController
@RequestMapping("/admin/acl/role")
@Slf4j
@CrossOrigin
public class RoleController {
    @Resource
    private RoleService roleService;

    @Resource
    private RolePermissionService rolePermissionService;

    @ApiOperation("获取角色分页功能")
    @GetMapping("/{page}/{limit}")
    public Result pageInfo(@PathVariable Long page,
                           @PathVariable Long limit,
                           RoleQueryVo roleQueryVo){
        Page<Role> pageRule = new Page<>(page,limit);
        String roleName = roleQueryVo.getRoleName();
        LambdaQueryWrapper<Role> roleQueryWrapper = new LambdaQueryWrapper<>();
        if (roleName==null){
            roleQueryWrapper=null;
        }else {
            roleQueryWrapper.like(Role::getRoleName,roleName);
        }
        return Result.ok(roleService.getBaseMapper().selectPage(pageRule, roleQueryWrapper));

    }

    @ApiOperation("获取角色")
    @GetMapping("/get/{id}")
    public Result getRole(@PathVariable Long id){
        Role role = roleService.getById(id);
        if (role==null){
            return Result.fail("无指定id的角色");
        }
        return Result.ok(role);
    }

    @ApiOperation("新增角色")
    @PostMapping("/save")
    public Result saveRole(@RequestBody Role role){
         roleService.save(role);
         return Result.ok("新增角色成功");
    }

    @ApiOperation("根据ID删除单个角色")
    @DeleteMapping("/remove/{id}")
    public Result removeRoleById(@PathVariable Long id){
        return Result.ok(roleService.removeById(id));
    }

    @ApiOperation("修改角色信息")
    @PutMapping("/update")
    public Result updateRole(@RequestBody Role role){
        roleService.updateById(role);
        return Result.ok("修改角色成功");
    }

    @ApiOperation("根据列表删除")
    @DeleteMapping("batchRemove")
    public Result batchRemove(@RequestBody List<Long> idList){
        roleService.removeByIds(idList);
        return Result.ok("根据列表删除成功");
    }


}
