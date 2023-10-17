package com.rinneohara.ssyx.controller;

import com.rabbitmq.client.Channel;
import com.rinneohara.ssyx.constant.RabbitMqConst;
import com.rinneohara.ssyx.service.OrderInfoService;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;

/**
 * @PROJECT_NAME: rinne-ssyx-parent
 * @DESCRIPTION:
 * @USER: Administrator
 * @DATE: 2023/10/16 16:05
 */
@Component
public class OrderReceiver {

    @Autowired
    private OrderInfoService orderInfoService;

    /**
     * 订单支付，更改订单状态与通知扣减库存
     * @param orderNo
     * @throws IOException
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = RabbitMqConst.QUEUE_ORDER_PAY, durable = "true"),
            exchange = @Exchange(value = RabbitMqConst.EXCHANGE_PAY_DIRECT),
            key = {RabbitMqConst.ROUTING_PAY_SUCCESS}
    ))
    public void orderPay(String orderNo, Message message, Channel channel) throws IOException {
        if (!StringUtils.isEmpty(orderNo)){
            // 支付成功！ 修改订单状态为已支付
            orderInfoService.orderPay(orderNo);
        }
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }
}
