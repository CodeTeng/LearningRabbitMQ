package com.lt.rabbitmq.topic;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @description:
 * @author: ~Teng~
 * @date: 2022/12/18 18:20
 */
@SpringBootTest
public class TopicProducer {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Test
    public void publish() {
        rabbitTemplate.convertAndSend("boot-topic-exchange", "slow.red.dog", "红色大狼狗！！");
//        rabbitTemplate.convertAndSend("boot-topic-exchange", "black.dog.and.cat", "黑色狗和猫！！");
    }

}
