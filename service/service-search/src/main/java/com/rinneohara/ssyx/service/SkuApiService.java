package com.rinneohara.ssyx.service;

import org.springframework.stereotype.Service;

@Service
public interface SkuApiService {
    void upperSku(Long skuId);

    void lowerGoods(Long skuId);
}
