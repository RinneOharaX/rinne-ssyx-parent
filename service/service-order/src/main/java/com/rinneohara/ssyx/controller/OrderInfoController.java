package com.rinneohara.ssyx.controller;


import com.rinneohara.ssyx.common.auth.ThreadLocalUtils;
import com.rinneohara.ssyx.common.result.Result;
import com.rinneohara.ssyx.service.OrderInfoService;
import com.rinneohara.ssyx.vo.order.OrderSubmitVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * 订单 前端控制器
 * </p>
 *
 * @author rinne
 * @since 2023-10-07
 */
@Api(value = "Order管理", tags = "Order管理")
@RestController
@RequestMapping(value="/api/order")
public class OrderInfoController {
    @Autowired
    private OrderInfoService orderInfoService;

    @ApiOperation("确认订单")
    @GetMapping("auth/confirmOrder")
    public Result confirm() {
        return Result.ok(orderInfoService.confirmOrder());
    }



    @ApiOperation("生成订单")
    @PostMapping("auth/submitOrder")
    public Result submitOrder(@RequestBody OrderSubmitVo orderParamVo, HttpServletRequest request) {
        // 获取到用户Id
        Long userId = ThreadLocalUtils.getUserId();
        return Result.ok(orderInfoService.submitOrder(orderParamVo));
    }

    @ApiOperation("获取订单详情")
    @GetMapping("auth/getOrderInfoById/{orderId}")
    public Result getOrderInfoById(@PathVariable("orderId") Long orderId){
        return Result.ok(orderInfoService.getOrderInfoById(orderId));
    }
}

