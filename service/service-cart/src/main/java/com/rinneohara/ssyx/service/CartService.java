package com.rinneohara.ssyx.service;

import com.rinneohara.ssyx.model.order.CartInfo;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @PROJECT_NAME: rinne-ssyx-parent
 * @DESCRIPTION:
 * @USER: Administrator
 * @DATE: 2023/9/20 15:45
 */
@Service
public interface CartService {
    void addToCart(Long userId, Long skuId, Integer skuNum);

    void deleteCart(Long skuId, Long userId);

    void deleteAllCart(Long userId);

    void batchDeleteCart(List<Long> skuIdList, Long userId);

    List<CartInfo> getCartList(Long userId);

    //根据用户id删除选中的用户记录
    void deleteCheckedCart(Long userId);
}
