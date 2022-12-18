package com.lt.rabbitmq.route;

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
 * @date: 2022/12/18 18:12
 */
@Component
public class RouteConsumer {

    @RabbitListener(bindings = {
            @QueueBinding(value = @Queue, //创建临时队列
                    exchange = @Exchange(value = "boot-route-exchange", type = "direct"), key = {"info", "error"})
    })
    public void getMessage1(Object msg, Channel channel, Message message) throws IOException {
        System.out.println("消费者1：" + msg);
        // 手动 ack
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

    @RabbitListener(bindings = {
            @QueueBinding(value = @Queue,
                    exchange = @Exchange(value = "boot-route-exchange", type = "direct"), key = {"info"})
    })
    public void getMessage2(Object msg, Channel channel, Message message) throws IOException {
        System.out.println("消费者2：" + msg);
        // 手动 ack
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }
}
