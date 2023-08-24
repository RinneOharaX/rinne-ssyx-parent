package com.rinneohara.ssyx.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.rinneohara.ssyx.model.product.Category;
import com.rinneohara.ssyx.model.product.SkuInfo;
import com.rinneohara.ssyx.service.CategoryService;
import com.rinneohara.ssyx.service.SkuInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.elasticsearch.client.license.LicensesStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @PROJECT_NAME: rinne-ssyx-parent
 * @DESCRIPTION:
 * @USER: Administrator
 * @DATE: 2023/8/16 11:37
 */
@RestController
@CrossOrigin
@RequestMapping("/api/product")
public class ProductInnerController {
    @Autowired
    CategoryService categoryService;

    @Autowired
    SkuInfoService skuInfoService;

    @ApiOperation(value = "根据分类id获取分类信息")
    @GetMapping("inner/getCategory/{categoryId}")
    public Category getCategory(@PathVariable Long categoryId) {
        return categoryService.getById(categoryId);
    }

    @ApiOperation(value = "根据skuId获取sku信息")
    @GetMapping("inner/getSku/{skuId}")
    public SkuInfo getSkuInfo(@PathVariable Long skuId){
        return skuInfoService.getById(skuId);
    }

    @ApiOperation("根据skuidList获取skuList")
    @GetMapping("inner/getSkuList")
    public List<SkuInfo> getSkuInfoList(@RequestBody List<Long> skuList){
        List<SkuInfo> skuInfoList = skuInfoService.getBaseMapper().selectBatchIds(skuList);
        return skuInfoList;
    }

    @ApiOperation("根据关键字获取相关sku名称")
    @GetMapping("inner/getSkuName/{keyword}")
    public List<SkuInfo> getSkuInfoListByKeyword(@PathVariable String keyword){
        List<SkuInfo> skuInfoList = skuInfoService.getBaseMapper().selectList(new LambdaQueryWrapper<SkuInfo>().like(SkuInfo::getSkuName, keyword));
        return skuInfoList;
    }

    @GetMapping("/inner/getCategory/{categoryIdList}")
    public List<Category> getCategoryList(@PathVariable List<Long> categoryIdList){
        List<Category> categoryList = categoryService.listByIds(categoryIdList);
        return categoryList;
    }
}
