package com.rinneohara.ssyx.client;

import com.rinneohara.ssyx.common.result.Result;
import com.rinneohara.ssyx.model.activity.CouponInfo;
import com.rinneohara.ssyx.model.order.CartInfo;
import com.rinneohara.ssyx.vo.order.CartInfoVo;
import com.rinneohara.ssyx.vo.order.OrderConfirmVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@FeignClient("service-activity")
public interface ActivityFeignClient {

    //根据skuId列表获取促销信息
    @PostMapping("/api/activity/inner/findActivity")
    public Map<Long, List<String>> findActivity(@RequestBody List<Long> skuIdList);

//    @ApiOperation("根据Es查询到的商品，得到商品参与的活动名字")
//    @PostMapping("/api/activity/inner/findActivityNameBySkuList")
//    public List<String> findActivityNameBySkuList(@RequestBody List<Long> skuIdList);

    @ApiOperation(value = "根据skuId获取促销与优惠券信息")
    @GetMapping("/api/activity/inner/findActivityAndCoupon/{id}/{userId}")
    public Map<String, Object> findActivityAndCoupon(@PathVariable("id") Long skuId, @PathVariable("userId") Long userId);

    @PostMapping("/api/activity/inner/findCartActivityAndCoupon/{userId}")
    public OrderConfirmVo findCartActivityAndCoupon(@RequestBody List<CartInfo> cartInfoList, @PathVariable Long userId);


    @PostMapping("/api/activity/inner/findCartActivityList")
    public List<CartInfoVo> findCartActivityList(@RequestBody List<CartInfo> cartInfoList);

    @PostMapping("/api/activity/inner/findRangeSkuIdList/{couponId}")
    public CouponInfo findRangeSkuIdList(@RequestBody List<CartInfo> cartInfoList,
                                         @PathVariable Long couponId);

    @GetMapping("/api/activity/inner/updateCouponInfoUseStatus/{couponId}/{userId}/{orderId}")
    public Result updateCouponInfoUseStatus (@PathVariable Long couponId,
                                             @PathVariable Long userId,
                                             @PathVariable Long orderId);
}
