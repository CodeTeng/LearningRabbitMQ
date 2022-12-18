package com.lt.rabbitmq.topic;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @description:
 * @author: ~Teng~
 * @date: 2022/12/18 18:18
 */
@Component
public class TopicConsumer {

    @RabbitListener(bindings = {
            @QueueBinding(
                    value = @Queue,
                    exchange = @Exchange(value = "boot-topic-exchange", type = "topic"),
                    key = {"*.red.*", "black.*.#"}
            )
    })
    public void getMessage1(Object msg, Channel channel, Message message) throws IOException {
        System.out.println("接收到消息1：" + msg);
        // 手动 ack
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

    @RabbitListener(bindings = {
            @QueueBinding(
                    value = @Queue,
                    exchange = @Exchange(value = "boot-topic-exchange", type = "topic"),
                    key = {"black.*.#"}
            )
    })
    public void getMessage2(Object msg, Channel channel, Message message) throws IOException {
        System.out.println("接收到消息2：" + msg);
        // 手动 ack
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }
}
