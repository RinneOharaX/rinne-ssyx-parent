package com.rinneohara.ssyx.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.rinneohara.ssyx.common.auth.ThreadLocalUtils;
import com.rinneohara.ssyx.common.result.Result;
import com.rinneohara.ssyx.model.order.OrderInfo;
import com.rinneohara.ssyx.service.OrderInfoService;
import com.rinneohara.ssyx.vo.order.OrderUserQueryVo;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @PROJECT_NAME: rinne-ssyx-parent
 * @DESCRIPTION:
 * @USER: Administrator
 * @DATE: 2023/10/17 12:43
 */
@RestController
@RequestMapping("/api/order")
public class OrderApiController {
    @Resource
    private OrderInfoService orderService;

    @ApiOperation(value = "获取用户订单分页列表")
    @GetMapping("/auth/findUserOrderPage/{page}/{limit}")
    public Result findUserOrderPage(
            @ApiParam(name = "page", value = "当前页码", required = true)
            @PathVariable Long page,

            @ApiParam(name = "limit", value = "每页记录数", required = true)
            @PathVariable Long limit,

            @ApiParam(name = "orderVo", value = "查询对象", required = false)
            OrderUserQueryVo orderUserQueryVo) {
        Long userId = ThreadLocalUtils.getUserId();
        orderUserQueryVo.setUserId(userId);
        Page<OrderInfo> pageParam = new Page<>(page, limit);
        IPage<OrderInfo> pageModel = orderService.findUserOrderPage(pageParam, orderUserQueryVo);
        return Result.ok(pageModel);
    }
}
