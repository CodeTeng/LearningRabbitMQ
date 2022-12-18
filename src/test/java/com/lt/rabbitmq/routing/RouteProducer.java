package com.lt.rabbitmq.routing;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @description:
 * @author: ~Teng~
 * @date: 2022/12/18 18:16
 */
@SpringBootTest
public class RouteProducer {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Test
    public void publish() {
        rabbitTemplate.convertAndSend("boot-route-exchange", "info", "发送的是info的key的路由信息");
    }
}
