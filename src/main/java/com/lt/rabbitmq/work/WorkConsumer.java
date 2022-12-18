package com.lt.rabbitmq.work;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @description:
 * @author: ~Teng~
 * @date: 2022/12/18 18:02
 */
@Component
public class WorkConsumer {

    @RabbitListener(queuesToDeclare = @Queue(name = "work", durable = "false"))
    public void getMessage1(Object msg, Channel channel, Message message) throws IOException {
        System.out.println("接收到消息1：" + msg);
        // 手动 ack
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

    @RabbitListener(queuesToDeclare = @Queue(name = "work", durable = "false"))
    public void getMessage2(Object msg, Channel channel, Message message) throws IOException {
        System.out.println("接收到消息2：" + msg);
        // 手动 ack
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }
}
