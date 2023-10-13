package com.rinneohara.ssyx.service;

import com.rabbitmq.client.AMQP;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @PROJECT_NAME: rinne-ssyx-parent
 * @DESCRIPTION:
 * @USER: Administrator
 * @DATE: 2023/8/16 16:17
 */
@Service
public class RabbitService {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    public boolean sendMessage(String exchange,String routingKey,Object message){
        rabbitTemplate.convertAndSend(exchange,routingKey,message);

        return true;
    }


    //延迟发送数据的方法
    public boolean sendDelayMessage(String exchange,String routingKey, Object message, int delayTime){

        //  在发送消息的时候设置延迟时间
        rabbitTemplate.convertAndSend(exchange, routingKey, message, new MessagePostProcessor() {
            @Override
            public Message postProcessMessage(Message message) throws AmqpException {
                //  设置一个延迟时间
                message.getMessageProperties().setDelay(delayTime*1000);
                return message;
            }
        });
        return true;
    }
}
