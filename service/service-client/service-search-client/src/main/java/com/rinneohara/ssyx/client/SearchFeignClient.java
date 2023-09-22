package com.rinneohara.ssyx.client;

import com.rinneohara.ssyx.model.search.SkuEs;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * @PROJECT_NAME: rinne-ssyx-parent
 * @DESCRIPTION:
 * @USER: Administrator
 * @DATE: 2023/9/8 14:58
 */
@FeignClient("service-search")
public interface SearchFeignClient {
    @GetMapping("/api/search/sku/inner/findHotSkuList")
    public List<SkuEs> findHotSkuList();

    /**
     * 更新商品incrHotScore
     * @param skuId
     * @return
     */
    @GetMapping("/api/search/sku/inner/incrHotScore/{skuId}")
    Boolean incrHotScore(@PathVariable("skuId") Long skuId);
}
