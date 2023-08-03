package com.rinneohara.ssyx.acl.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mysql.cj.protocol.x.ReusableOutputStream;
import com.rinneohara.ssyx.acl.service.IndexService;
import com.rinneohara.ssyx.common.result.Result;
import com.rinneohara.ssyx.model.acl.Admin;
import com.rinneohara.ssyx.model.acl.Role;
import com.rinneohara.ssyx.vo.acl.AdminQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.A;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @PROJECT_NAME: rinne-ssyx-parent
 * @DESCRIPTION:
 * @USER: Administrator
 * @DATE: 2023/8/2 13:07
 */

@CrossOrigin
@RestController
@Slf4j
@Api(tags = "用户管理")
@RequestMapping("/admin/acl/user")
public class AdminController {
    @Resource
    private IndexService adminService;

    //获取根据用户id获取他所拥有的角色
    @ApiOperation("获取全部角色+根据用户id获取他的角色")
    @GetMapping("/toAssign/{adminId}")
    public Result toAssignByAdminId(@PathVariable Long adminId){
        Map<String,Object> map=adminService.findRoleByAdminId(adminId);
        return Result.ok(map);
    }

    @ApiOperation("分配角色")
    @PostMapping("/doAssign")
    public Result doAssign(@RequestParam Long adminId,
                           @RequestParam Long[] roleId){
        adminService.doAssign(adminId,roleId);
       return Result.ok("分配角色成功");
    }


    @ApiOperation("获取用户分页功能")
    @GetMapping("/{page}/{limit}")
    public Result pageInfo(@PathVariable Long page,
                           @PathVariable Long limit,
                           AdminQueryVo adminQueryVo){
        Page<Admin> pageRule=new Page<>(page,limit);
        IPage<Admin> adminIPage = adminService.selectPage(pageRule,adminQueryVo);
        return Result.ok(adminIPage);
    }

    @ApiOperation("根据用户id查找")
    @GetMapping("/get/{id}")
    public Result getAdminById(@PathVariable Long id){
        return Result.ok(adminService.getById(id));
    }

    @ApiOperation(value = "新增管理用户")
    @PostMapping("/save")
    public Result saveAdmin(@RequestBody Admin admin){
        adminService.save(admin);
        return Result.ok("管理用户保存成功");
    }
    @ApiOperation(value = "修改管理用户")
    @PutMapping("/update")
    public Result updateAdmin(@RequestBody Admin admin){
        adminService.updateById(admin);
        return Result.ok("成功修改管理用户");
    }
    @ApiOperation(value = "删除管理用户")
    @DeleteMapping("/remove/{id}")
    public Result removeAdminById(@PathVariable Long id){
        adminService.removeById(id);
        return Result.ok("成功删除管理用户");
    }


    @ApiOperation(value = "根据id列表删除管理用户")
    @DeleteMapping("/batchRemove")
    public Result batchRemoveAdmin(@RequestBody List<Long> idList){
        adminService.removeByIds(idList);
        return Result.ok("成功批量删除管理用户");
    }
}
