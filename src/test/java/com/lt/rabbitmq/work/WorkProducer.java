package com.lt.rabbitmq.work;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @description:
 * @author: ~Teng~
 * @date: 2022/12/18 18:04
 */
@SpringBootTest
public class WorkProducer {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Test
    public void publish() {
        for (int i = 0; i < 20; i++) {
            rabbitTemplate.convertAndSend("work", "work模型: " + i);
        }
    }
}
