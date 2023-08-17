//package com.rinneohara.ssyx.controller;
//
//import com.rinneohara.ssyx.client.ProductFeignClient;
//import com.rinneohara.ssyx.common.result.Result;
//import com.rinneohara.ssyx.service.SkuApiService;
//import io.swagger.annotations.Api;
//import io.swagger.annotations.ApiOperation;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.*;
//
//import javax.annotation.Resource;
//
///**
// * @PROJECT_NAME: rinne-ssyx-parent
// * @DESCRIPTION:
// * @USER: Administrator
// * @DATE: 2023/8/16 12:03
// */
//@RestController
//@Slf4j
//@CrossOrigin
//@Api(tags = "远程调用service-product接口")
//@RequestMapping("/api/search/sku")
//public class SkuApiController {
//    @Autowired
//    SkuApiService skuApiService;
//
//    @ApiOperation("上架商品")
//    @GetMapping("/inner/upperSku/{skuId}")
//    public Result upperGoods(@PathVariable Long skuId) {
//        skuApiService.upperSku(skuId);
//        return Result.ok("成功");
//    }
//
//    @ApiOperation(value = "下架商品")
//    @GetMapping("inner/lowerSku/{skuId}")
//    public Result lowerGoods(@PathVariable("skuId") Long skuId) {
//        skuApiService.lowerGoods(skuId);
//        return Result.ok("成功");
//    }
//}
