package com.rinneohara.ssyx.client;

import com.rinneohara.ssyx.model.order.CartInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

/**
 * @PROJECT_NAME: rinne-ssyx-parent
 * @DESCRIPTION:
 * @USER: Administrator
 * @DATE: 2023/10/7 14:36
 */
@FeignClient("service-cart")
public interface CartFeignClient {
    @GetMapping("/getCartInfo/{userId}")
    public List<CartInfo> getCartInfo(@PathVariable("userId") Long userId);
}
