package com.lt.rabbitmq.qubsub;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @description:
 * @author: ~Teng~
 * @date: 2022/12/18 18:07
 */
@SpringBootTest
public class PubSubProducer {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Test
    public void publish() {
        rabbitTemplate.convertAndSend("boot-pubsub-exchange", "", "广播模式");
    }

}
