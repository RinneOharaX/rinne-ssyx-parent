package com.rinneohara.ssyx.controller;

import com.rinneohara.ssyx.model.order.OrderInfo;
import com.rinneohara.ssyx.service.OrderInfoService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @PROJECT_NAME: rinne-ssyx-parent
 * @DESCRIPTION:
 * @USER: Administrator
 * @DATE: 2023/10/16 16:15
 */
@RestController
@RequestMapping("/api/inner/order")
public class OrderInnerController {

    @Resource
    OrderInfoService orderInfoService;

    @GetMapping("/getOrderInfoByOrderNo/{orderNo}")
    public OrderInfo getOrderInfoByOrderNo(@PathVariable String orderNo){
     return orderInfoService.getOrderInfoByOrderNo(orderNo);
    }
}
