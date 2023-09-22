package com.rinneohara.ssyx.controller;

import com.rinneohara.ssyx.common.auth.ThreadLocalUtils;
import com.rinneohara.ssyx.common.result.Result;
import com.rinneohara.ssyx.service.ItemService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @PROJECT_NAME: rinne-ssyx-parent
 * @DESCRIPTION:
 * @USER: Administrator
 * @DATE: 2023/9/17 11:22
 */

@RestController
@Api("商品详情")
@RequestMapping("/api/home")
public class ItemApiController {

    @Resource
    ItemService itemService;


    @ApiOperation(value = "获取sku详细信息")
    @GetMapping("item/{id}")
    public Result index(@PathVariable Long id){
        Long userId = ThreadLocalUtils.getUserId();
       Map<String,Object> resultMap= itemService.item(id,userId);
        return Result.ok(resultMap);
    }
}
