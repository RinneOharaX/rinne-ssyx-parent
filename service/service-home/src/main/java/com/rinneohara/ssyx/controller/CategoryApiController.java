package com.rinneohara.ssyx.controller;

import com.rinneohara.ssyx.client.ProductFeignClient;
import com.rinneohara.ssyx.common.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @PROJECT_NAME: rinne-ssyx-parent
 * @DESCRIPTION:
 * @USER: Administrator
 * @DATE: 2023/9/13 14:56
 */
@Api(tags = "商品分类")
@RestController
@RequestMapping("api/home")
public class CategoryApiController {
    @Resource
    private ProductFeignClient productFeignClient;

    @ApiOperation(value = "获取分类信息")
    @GetMapping("/category")
    public Result index() {
        return Result.ok(productFeignClient.findAllCategoryList());
    }
}
