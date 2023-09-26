package com.rinneohara.ssyx.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.rinneohara.ssyx.model.activity.ActivityInfo;
import com.rinneohara.ssyx.model.activity.ActivityRule;
import com.rinneohara.ssyx.model.activity.ActivitySku;
import com.rinneohara.ssyx.model.order.CartInfo;
import com.rinneohara.ssyx.model.search.SkuEs;
import com.rinneohara.ssyx.service.ActivityInfoService;
import com.rinneohara.ssyx.service.ActivityRuleService;
import com.rinneohara.ssyx.service.ActivitySkuService;
import com.rinneohara.ssyx.vo.order.OrderConfirmVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.spring.web.json.Json;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @PROJECT_NAME: rinne-ssyx-parent
 * @DESCRIPTION:
 * @USER: Administrator
 * @DATE: 2023/9/8 15:12
 */
@RestController
@RequestMapping("/api/activity")
public class ActivityInnerController {
    @Autowired
    private ActivityInfoService activityInfoService;


    @ApiOperation(value = "根据skuId列表获取促销信息")
    @PostMapping("inner/findActivity")
    public Map<Long, List<String>> findActivity(@RequestBody List<Long> skuIdList) {
        return activityInfoService.findActivity(skuIdList);
    }

    @ApiOperation("根据Es查询到的商品，得到商品参与的活动名字")
    @PostMapping("inner/findActivityNameBySkuList")
    public Map<Long, List<String>> findActivityNameBySkuList(@RequestBody List<Long> skuIdList) {
        return activityInfoService.findActivity(skuIdList);
    }

    @ApiOperation(value = "根据skuId获取促销与优惠券信息")
    @GetMapping("inner/findActivityAndCoupon/{id}/{userId}")
    public Map<String, Object> findActivityAndCoupon(@PathVariable Long id, @PathVariable("userId") Long userId) {
        return activityInfoService.findActivityAndCoupon(id, userId);
    }

    @PostMapping("inner/findCartActivityAndCoupon/{userId}")
    public OrderConfirmVo findCartActivityAndCoupon(@RequestBody  List<CartInfo> cartInfoList,
                                             @PathVariable("userId") Long userId){
        return activityInfoService.findCartActivityAndCoupon(cartInfoList,userId);
    };
}
