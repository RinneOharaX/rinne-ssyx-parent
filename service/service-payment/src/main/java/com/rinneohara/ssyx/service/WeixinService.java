package com.rinneohara.ssyx.service;

import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public interface WeixinService {

    /**
     * 根据订单号下单，生成支付链接
     * @param orderNo
     * @return
     */
    Map createJsapi(String orderNo);

    /**
     * 根据订单号去微信第三方查询支付状态
     * @param orderNo
     * @return
     */
    Map queryPayStatus(String orderNo, String paymentType);

}
