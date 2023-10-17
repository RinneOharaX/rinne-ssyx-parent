package com.rinneohara.ssyx.controller;

import com.rabbitmq.client.Channel;
import com.rinneohara.ssyx.constant.RabbitMqConst;
import com.rinneohara.ssyx.service.SkuInfoService;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * @PROJECT_NAME: rinne-ssyx-parent
 * @DESCRIPTION:
 * @USER: Administrator
 * @DATE: 2023/10/16 16:09
 */
@Component
public class StockReceiver {

    @Resource
    private SkuInfoService skuInfoService;

    /**
     * 扣减库存成功，更新订单状态
     * @param orderNo
     * @throws IOException
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = RabbitMqConst.QUEUE_MINUS_STOCK, durable = "true"),
            exchange = @Exchange(value = RabbitMqConst.EXCHANGE_ORDER_DIRECT),
            key = {RabbitMqConst.ROUTING_MINUS_STOCK}
    ))
    public void minusStock(String orderNo, Message message, Channel channel) throws IOException {
        if (!StringUtils.isEmpty(orderNo)){
            skuInfoService.minusStock(orderNo);
        }
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }
}
