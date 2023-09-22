package com.rinneohara.ssyx.acl.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.rinneohara.ssyx.acl.serviceimpl.IndexServiceImpl;
import com.rinneohara.ssyx.common.exception.MyException;
import com.rinneohara.ssyx.common.result.Result;
import com.rinneohara.ssyx.model.acl.Admin;
import com.rinneohara.utils.MD5;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @PROJECT_NAME: rinne-ssyx-parent
 * @DESCRIPTION:
 * @USER: Administrator
 * @DATE: 2023/8/1 12:39
 */


@Api(tags = "登陆接口")
@RequestMapping("/admin/acl/index")
@RestController
public class IndexController {
    private static String name;
    @Resource
    IndexServiceImpl indexService;

    @ApiOperation("登陆")
    @PostMapping("/login")
    public Result login(@RequestBody Admin admin) {
        String username = admin.getUsername();
        Admin user = indexService.getOne(new QueryWrapper<Admin>().eq("username", username));
        String password = MD5.encrypt(admin.getPassword());
        if (user.getPassword().equals(password)){
            Map<String,String> hashMap=new HashMap<>();
            hashMap.put("token","token");
            name=user.getName();
            return Result.ok(hashMap);
        }else {
            throw new MyException("账号或者密码错误", 100);
        }
    }
    @ApiOperation("登陆信息")
    @GetMapping("/info")
    public Result loginInfo(){
        Map<String,String> hashmap=new HashMap<>();
        hashmap.put("name",name);
        hashmap.put("avatar","https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif");
        return Result.ok(hashmap);
    }

    @ApiOperation("登出")
    @PostMapping("logout")
    public Result logout(){
        return Result.ok(null);
    }
}

