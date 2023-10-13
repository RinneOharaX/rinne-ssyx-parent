package com.rinneohara.ssyx.controller;

import com.rinneohara.ssyx.model.order.CartInfo;
import com.rinneohara.ssyx.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @PROJECT_NAME: rinne-ssyx-parent
 * @DESCRIPTION:
 * @USER: Administrator
 * @DATE: 2023/10/7 14:16
 */
@RestController
public class CartInnerController {
    @Autowired
    CartService cartService;

    @GetMapping("/getCartInfo/{userId}")
    public List<CartInfo> getCartInfo(@PathVariable("userId") Long userId){
       return cartService.getCartList(userId);
    }
}
