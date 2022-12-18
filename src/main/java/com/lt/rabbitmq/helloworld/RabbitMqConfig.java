package com.lt.rabbitmq.helloworld;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @description:
 * @author: ~Teng~
 * @date: 2022/12/18 17:50
 */
@Configuration
public class RabbitMqConfig {

    @Bean
    public Queue simpleQueue() {
        return new Queue("simpleQueue");
    }
}
