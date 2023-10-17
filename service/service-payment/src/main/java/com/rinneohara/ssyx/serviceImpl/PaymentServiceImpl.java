package com.rinneohara.ssyx.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rinneohara.ssyx.client.OrderFeignClient;
import com.rinneohara.ssyx.common.exception.MyException;
import com.rinneohara.ssyx.common.result.ResultCodeEnum;
import com.rinneohara.ssyx.constant.RabbitMqConst;
import com.rinneohara.ssyx.enums.PaymentStatus;
import com.rinneohara.ssyx.enums.PaymentType;
import com.rinneohara.ssyx.mapper.PaymentInfoMapper;
import com.rinneohara.ssyx.model.order.OrderInfo;
import com.rinneohara.ssyx.model.order.PaymentInfo;
import com.rinneohara.ssyx.service.PaymentService;
import com.rinneohara.ssyx.service.RabbitService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

/**
 * @PROJECT_NAME: rinne-ssyx-parent
 * @DESCRIPTION:
 * @USER: Administrator
 * @DATE: 2023/10/16 16:01
 */
@Service
@Slf4j
public class PaymentServiceImpl extends ServiceImpl<PaymentInfoMapper,PaymentInfo> implements PaymentService {

    @Resource
    private PaymentInfoMapper paymentInfoMapper;

    @Resource
    private OrderFeignClient orderFeignClient;

    @Resource
    private RabbitService rabbitService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public PaymentInfo savePaymentInfo(String orderNo, PaymentType paymentType) {
        OrderInfo order = orderFeignClient.getOrderInfoByOrderNo(orderNo);
        if(null == order) {
            throw new MyException(ResultCodeEnum.DATA_ERROR);
        }
        // 保存交易记录
        PaymentInfo paymentInfo = new PaymentInfo();
        paymentInfo.setCreateTime(new Date());
        paymentInfo.setOrderId(order.getId());
        paymentInfo.setPaymentType(paymentType);
        paymentInfo.setUserId(order.getUserId());
        paymentInfo.setOrderNo(order.getOrderNo());
        paymentInfo.setPaymentStatus(PaymentStatus.UNPAID);
        String subject = "test";
        paymentInfo.setSubject(subject);
        //paymentInfo.setTotalAmount(order.getTotalAmount());
        paymentInfo.setTotalAmount(new BigDecimal("0.01"));

        paymentInfoMapper.insert(paymentInfo);
        return paymentInfo;
    }

    @Override
    public PaymentInfo getPaymentInfo(String orderNo, PaymentType paymentType) {
        LambdaQueryWrapper<PaymentInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PaymentInfo::getOrderNo, orderNo);
        queryWrapper.eq(PaymentInfo::getPaymentType, paymentType);
        return paymentInfoMapper.selectOne(queryWrapper);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void paySuccess(String orderNo, PaymentType paymentType, Map<String,String> paramMap) {
        LambdaQueryWrapper<PaymentInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PaymentInfo::getOrderNo, orderNo);
        queryWrapper.eq(PaymentInfo::getPaymentType, paymentType);
        PaymentInfo paymentInfo = paymentInfoMapper.selectOne(queryWrapper);
        if (paymentInfo.getPaymentStatus() != PaymentStatus.UNPAID) {
            return;
        }

        PaymentInfo paymentInfoUpd = new PaymentInfo();
        paymentInfoUpd.setPaymentStatus(PaymentStatus.PAID);
        String tradeNo = paymentType == PaymentType.WEIXIN ? paramMap.get("ransaction_id") : paramMap.get("trade_no");
        paymentInfoUpd.setTradeNo(tradeNo);
        paymentInfoUpd.setCallbackTime(new Date());
        paymentInfoUpd.setCallbackContent(paramMap.toString());
        paymentInfoMapper.update(paymentInfoUpd, new LambdaQueryWrapper<PaymentInfo>().eq(PaymentInfo::getOrderNo, orderNo));
        // 表示交易成功！

        //发送消息
        rabbitService.sendMessage(RabbitMqConst.EXCHANGE_PAY_DIRECT, RabbitMqConst.ROUTING_PAY_SUCCESS, orderNo);
    }
}
