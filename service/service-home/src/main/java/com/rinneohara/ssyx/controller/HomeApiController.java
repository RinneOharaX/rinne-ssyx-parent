package com.rinneohara.ssyx.controller;

import com.rinneohara.ssyx.common.result.Result;
import com.rinneohara.ssyx.service.HomeService;
import com.rinneohara.utils.JwtHelper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @PROJECT_NAME: rinne-ssyx-parent
 * @DESCRIPTION:
 * @USER: Administrator
 * @DATE: 2023/9/7 16:38
 */

@RestController
@Api("首页功能模块")
@RequestMapping("/api/home")
public class HomeApiController {

    @Autowired
    private HomeService homeService;

    @ApiOperation(value = "获取首页数据")
    @GetMapping("index")
    public Result getIndexInfo(HttpServletRequest request){
        String token = request.getHeader("token");
        Long userId = JwtHelper.getUserId(token);
        return Result.ok(homeService.getDataInFo(userId));
    }
}
