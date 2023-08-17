package com.rinneohara.ssyx.receiver;

import com.rabbitmq.client.Channel;
import com.rinneohara.ssyx.constant.RabbitMqConst;
import com.rinneohara.ssyx.service.SkuApiService;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @PROJECT_NAME: rinne-ssyx-parent
 * @DESCRIPTION:
 * @USER: Administrator
 * @DATE: 2023/8/17 13:25
 */

@Component
public class SkuReceiver {
    @Autowired
    SkuApiService skuApiService;


    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = RabbitMqConst.QUEUE_GOODS_UPPER,durable = "true"),
            exchange = @Exchange(value = RabbitMqConst.EXCHANGE_GOODS_DIRECT),
            key = {RabbitMqConst.ROUTING_GOODS_UPPER}
    ))
    public void upperSku(Long skuId,Message message,Channel channel) throws IOException {
            if (skuId!=null) {
                skuApiService.upperSku(skuId);
            }
        /**
         * 第一个参数：表示收到的消息的标号
         * 第二个参数：如果为true表示可以签收多个消息
         */
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = RabbitMqConst.QUEUE_GOODS_LOWER, durable = "true"),
            exchange = @Exchange(value = RabbitMqConst.EXCHANGE_GOODS_DIRECT),
            key = {RabbitMqConst.ROUTING_GOODS_LOWER}
    ))
    public void lowerSku(Long skuId, Message message, Channel channel) throws IOException, IOException {
        if (null != skuId) {
            skuApiService.lowerGoods(skuId);
        }
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }
}
