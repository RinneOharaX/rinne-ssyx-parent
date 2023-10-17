package com.rinneohara.ssyx.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.rinneohara.ssyx.enums.PaymentType;
import com.rinneohara.ssyx.model.order.PaymentInfo;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public interface PaymentService extends IService<PaymentInfo> {

    /**
     * 保存交易记录
     * @param orderNo
     * @param paymentType 支付类型（1：微信 2：支付宝）
     */
    PaymentInfo savePaymentInfo(String orderNo, PaymentType paymentType);

    PaymentInfo getPaymentInfo(String orderNo, PaymentType paymentType);

    //支付成功
    void paySuccess(String orderNo,PaymentType paymentType, Map<String,String> paramMap);
}
