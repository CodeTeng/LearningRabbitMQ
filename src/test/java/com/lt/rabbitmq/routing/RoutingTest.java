package com.lt.rabbitmq.routing;

import com.lt.rabbitmq.utils.RabbitMqUtil;
import com.rabbitmq.client.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * @description: Exchange   ---  direct
 * @author: ~Teng~
 * @date: 2022/12/18 17:17
 */
public class RoutingTest {

    /**
     * 交换机名称
     */
    private static final String EXCHANGE_NAME = "routing-exchange";

    /**
     * Routing-key
     */
    private static final String ERROR_ROUTING_KEY = "ERROR";
    private static final String INFO_ROUTING_KEY = "INFO";


    @Test
    public void publish() throws Exception {
        Connection connection = RabbitMqUtil.getConnection();
        Channel channel = connection.createChannel();
        // 声明交换机
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);
        // 发布消息到exchange，同时指定路由的规则
        channel.basicPublish(EXCHANGE_NAME, ERROR_ROUTING_KEY, null, "ERROR".getBytes(StandardCharsets.UTF_8));
        channel.basicPublish(EXCHANGE_NAME, INFO_ROUTING_KEY, null, "INFO1".getBytes(StandardCharsets.UTF_8));
        channel.basicPublish(EXCHANGE_NAME, INFO_ROUTING_KEY, null, "INFO2".getBytes(StandardCharsets.UTF_8));
        channel.basicPublish(EXCHANGE_NAME, INFO_ROUTING_KEY, null, "INFO3".getBytes(StandardCharsets.UTF_8));
        System.out.println("生产者发布消息成功");
        channel.close();
        connection.close();
    }

    @Test
    public void consume1() throws Exception {
        Connection connection = RabbitMqUtil.getConnection();
        Channel channel = connection.createChannel();
        // 声明交换机
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);
        // 获取临时队列
        String queueName = channel.queueDeclare().getQueue();
        // 基于路由key绑定队列和交换机
        channel.queueBind(queueName, EXCHANGE_NAME, ERROR_ROUTING_KEY);
        // 指定当前消费者一次消费多少消息
        channel.basicQos(1);
        // 设置回调
        DefaultConsumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("消费者1号接收到消息：" + new String(body, StandardCharsets.UTF_8));
                // 手动 ack
                channel.basicAck(envelope.getDeliveryTag(), false);
            }
        };
        channel.basicConsume(queueName, false, consumer);
        System.out.println("消费者开始监听队列！");
        System.in.read();
        // 释放资源
        channel.close();
        connection.close();
    }

    @Test
    public void consume2() throws Exception {
        Connection connection = RabbitMqUtil.getConnection();
        Channel channel = connection.createChannel();
        // 声明交换机
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);
        // 获取临时队列
        String queueName = channel.queueDeclare().getQueue();
        // 基于路由key绑定交换机和队列
        channel.queueBind(queueName, EXCHANGE_NAME, INFO_ROUTING_KEY);
        channel.queueBind(queueName, EXCHANGE_NAME, ERROR_ROUTING_KEY);
        // 设置消费者一次消费多少消息
        channel.basicQos(1);
        // 设置回调
        DefaultConsumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("消费者2号接收到消息：" + new String(body, StandardCharsets.UTF_8));
                // 手动 ack
                channel.basicAck(envelope.getDeliveryTag(), false);
            }
        };
        channel.basicConsume(queueName, false, consumer);
        System.out.println("消费者开始监听队列！");
        System.in.read();
        // 释放资源
        channel.close();
        connection.close();
    }
}
