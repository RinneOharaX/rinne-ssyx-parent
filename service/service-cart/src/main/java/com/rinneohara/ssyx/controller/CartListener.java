package com.rinneohara.ssyx.controller;

import com.rabbitmq.client.Channel;
import com.rinneohara.ssyx.constant.RabbitMqConst;
import com.rinneohara.ssyx.model.order.CartInfo;
import com.rinneohara.ssyx.service.CartService;
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
 * @DATE: 2023/10/11 14:34
 */
@Component
public class CartListener {
    @Autowired
    private CartService cartService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(RabbitMqConst.QUEUE_DELETE_CART),
            exchange = @Exchange(RabbitMqConst.EXCHANGE_ORDER_DIRECT),
            key = {RabbitMqConst.QUEUE_DELETE_CART}
    ))
    public void consumeMessage(Long userId, Message message, Channel channel) throws IOException {
        if (userId != null){
            cartService.deleteCheckedCart(userId);
        }
        channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
    }
}
