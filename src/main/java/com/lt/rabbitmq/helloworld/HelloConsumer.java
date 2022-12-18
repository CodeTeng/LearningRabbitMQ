package com.lt.rabbitmq.helloworld;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @description: 消费者
 * @author: ~Teng~
 * @date: 2022/12/18 17:51
 */
@Component
public class HelloConsumer {

    /**
     * 在配置类中创建queue在这里引用即可从simpleQueue队列中消费消费
     * RabbitListener注解也可以在类上使用
     */
    @RabbitListener(queues = "simpleQueue")
    public void receive(String msg, Channel channel, Message message) throws IOException {
        System.out.println("消费者接收到的消息是:" + msg);
        // 手动 ack
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

//    /**
//     * 使用queuesToDeclare声明队列并从这个队列中消费消息---这种方式可以不使用配置类
//     */
//    @RabbitListener(queuesToDeclare = @Queue(name = "simpleQueue"))
//    public void receive2(String msg) {
//        System.out.println("消费者接收到的消息是:" + msg);
//    }
}
