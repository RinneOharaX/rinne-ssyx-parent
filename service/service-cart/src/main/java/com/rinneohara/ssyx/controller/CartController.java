package com.rinneohara.ssyx.controller;

import com.rinneohara.ssyx.client.ActivityFeignClient;
import com.rinneohara.ssyx.common.auth.ThreadLocalUtils;
import com.rinneohara.ssyx.common.result.Result;
import com.rinneohara.ssyx.model.order.CartInfo;
import com.rinneohara.ssyx.service.CartService;
import com.rinneohara.ssyx.vo.order.OrderConfirmVo;
import io.swagger.annotations.ApiOperation;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @PROJECT_NAME: rinne-ssyx-parent
 * @DESCRIPTION:
 * @USER: Administrator
 * @DATE: 2023/9/20 15:38
 */
@RequestMapping("/api/cart")
public class CartController {


    @Autowired
    private CartService cartService;

    @Autowired
    private ActivityFeignClient activityFeignClient;

    @GetMapping("activityCartList")
    public Result activityCartList(HttpServletRequest request) {
        // 获取用户Id
        Long userId = ThreadLocalUtils.getUserId();
        List<CartInfo> cartInfoList = cartService.getCartList(userId);

        OrderConfirmVo orderTradeVo = activityFeignClient.findCartActivityAndCoupon(cartInfoList, userId);
        return Result.ok(orderTradeVo);
    }


    @ApiOperation("展示购物车列表")
    @GetMapping("/cartList")
    public Result cartList(HttpServletRequest request) {
        // 获取用户Id
        Long userId = ThreadLocalUtils.getUserId();
        List<CartInfo> cartInfoList = cartService.getCartList(userId);
        return Result.ok(cartInfoList);
    }


    @ApiOperation("添加至购物车")
    @GetMapping("/addToCart/{skuId}/{skuNum}")
    public Result addToCart(@PathVariable Long skuId,
                            @PathVariable Integer skuNum){
        Long userId = ThreadLocalUtils.getUserId();
        cartService.addToCart(userId,skuId,skuNum);
        return Result.ok("添加至购物车成功");
    }



    @ApiOperation("根据商品id删除购物车内的对应商品")
    @DeleteMapping("deleteCart/{skuId}")
    public Result deleteCart(@PathVariable("skuId") Long skuId,
                             HttpServletRequest request) {
        // 如何获取userId
        Long userId = ThreadLocalUtils.getUserId();
        cartService.deleteCart(skuId, userId);
        return Result.ok("删除成功");
    }

    @ApiOperation(value="清空购物车")
    @DeleteMapping("deleteAllCart")
    public Result deleteAllCart(HttpServletRequest request){
        // 如何获取userId
        Long userId = ThreadLocalUtils.getUserId();
        cartService.deleteAllCart(userId);
        return Result.ok("清空成功");
    }

    @ApiOperation(value="批量删除购物车")
    @PostMapping("batchDeleteCart")
    public Result batchDeleteCart(@RequestBody List<Long> skuIdList, HttpServletRequest request){
        // 如何获取userId
        Long userId = ThreadLocalUtils.getUserId();
        cartService.batchDeleteCart(skuIdList, userId);
        return Result.ok("批量删除成功");
    }


}
