package com.rinneohara.ssyx.controller;

import com.rinneohara.ssyx.client.ProductFeignClient;
import com.rinneohara.ssyx.common.result.Result;
import com.rinneohara.ssyx.model.search.SkuEs;
import com.rinneohara.ssyx.service.SkuApiService;
import com.rinneohara.ssyx.vo.search.SkuEsQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.statement.create.table.Index;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @PROJECT_NAME: rinne-ssyx-parent
 * @DESCRIPTION:
 * @USER: Administrator
 * @DATE: 2023/8/16 12:03
 */
@RestController
@Slf4j
@RequestMapping("/api/search/sku")
public class SkuApiController {
    @Autowired
    SkuApiService skuApiService;

    @ApiOperation(value = "获取爆品商品")
    @GetMapping("/inner/findHotSkuList")
    public List<SkuEs> findHotSkuList() {
        return skuApiService.findHotSkuList();
    }

    @ApiOperation(value = "搜索对应分类商品+分页")
    @GetMapping("/{page}/{limit}")
    public Result getList(@PathVariable Integer page,
                          @PathVariable Integer limit,
                          SkuEsQueryVo skuEsQueryVo){
        Pageable pageable = PageRequest.of(page, limit);
        Page<SkuEs> pageModel = skuApiService.search(pageable, skuEsQueryVo);
        return Result.ok(pageModel);
    }

    @ApiOperation(value = "更新商品incrHotScore")
    @GetMapping("inner/incrHotScore/{skuId}")
    public Boolean incrHotScore(@PathVariable("skuId") Long skuId) {
        // 调用服务层
       skuApiService.incrHotScore(skuId);
        return true;
    }
}
