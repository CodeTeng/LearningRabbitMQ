package com.lt.rabbitmq.helloworld;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @description:
 * @author: ~Teng~
 * @date: 2022/12/18 17:57
 */
@SpringBootTest
public class HelloProduce {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Test
    public void publish() {
        rabbitTemplate.convertAndSend("simpleQueue", "SpringBoot整合MQ发送的消息");
    }
}
