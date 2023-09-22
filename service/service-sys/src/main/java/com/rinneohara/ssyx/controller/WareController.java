package com.rinneohara.ssyx.controller;

import com.rinneohara.ssyx.common.result.Result;
import com.rinneohara.ssyx.service.WareService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @PROJECT_NAME: rinne-ssyx-parent
 * @DESCRIPTION:
 * @USER: Administrator
 * @DATE: 2023/8/9 15:25
 */

@Api(value = "Ware管理", tags = "Ware管理")
@RestController
@RequestMapping(value="/admin/sys/ware")
public class WareController {
    @Resource
    private WareService wareService;

    @ApiOperation(value = "获取全部仓库")
    @GetMapping("/findAllList")
    public Result findAllList() {
        return Result.ok(wareService.list());
    }
}
