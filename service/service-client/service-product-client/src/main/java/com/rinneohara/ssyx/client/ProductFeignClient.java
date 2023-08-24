package com.rinneohara.ssyx.client;

import com.rinneohara.ssyx.model.product.Category;
import com.rinneohara.ssyx.model.product.SkuInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @PROJECT_NAME: rinne-ssyx-parent
 * @DESCRIPTION:
 * @USER: Administrator
 * @DATE: 2023/8/16 11:59
 */
@FeignClient("service-product")
public interface ProductFeignClient {
    @GetMapping("/api/product/inner/getCategory/{categoryId}")
    public Category getCategory(@PathVariable Long categoryId);

    @GetMapping("/api/product/inner/getSku/{skuId}")
    public SkuInfo getSkuInfo(@PathVariable Long skuId);

    @GetMapping("/api/product/inner/getSkuList")
    public List<SkuInfo> getSkuInfoList(@RequestBody List<Long> skuList);

    @GetMapping("/api/product/inner/getSkuName/{keyword}")
    public List<SkuInfo> getSkuInfoListByKeyword(@PathVariable String keyword);

    @GetMapping("/api/product/inner/getCategory/{categoryIdList}")
    public List<Category> getCategoryList(@PathVariable List<Long> categoryIdList);

}
