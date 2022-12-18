package com.lt.rabbitmq.topic;

import com.lt.rabbitmq.utils.RabbitMqUtil;
import com.rabbitmq.client.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * @description:
 * @author: ~Teng~
 * @date: 2022/12/18 17:33
 */
public class TopicTest {

    /**
     * 交换机名称
     */
    private static final String EXCHANGE_NAME = "topic-exchange";

    /**
     * *  匹配不多不少恰好一个词
     * #  匹配一个或多个词
     */
    private static final String ROUTING_KEY1 = "*.red.*";
    private static final String ROUTING_KEY2 = "fast.#";
    private static final String ROUTING_KEY3 = "*.*.rabbit";

    @Test
    public void publish() throws Exception {
        Connection connection = RabbitMqUtil.getConnection();
        Channel channel = connection.createChannel();
        // 声明交换机
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC);
        // 发布消息到exchange，同时指定路由的规则
        channel.basicPublish(EXCHANGE_NAME, "fast.red.monkey", null, "红快猴子".getBytes());
        channel.basicPublish(EXCHANGE_NAME, "slow.black.dog", null, "黑慢狗".getBytes());
        channel.basicPublish(EXCHANGE_NAME, "fast.white.cat", null, "快白猫".getBytes());
        System.out.println("生产者发布消息成功！");
        // 释放资源
        channel.close();
        connection.close();
    }

    @Test
    public void consume1() throws Exception {
        Connection connection = RabbitMqUtil.getConnection();
        Channel channel = connection.createChannel();
        // 声明交换机
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC);
        // 创建临时队列
        String queueName = channel.queueDeclare().getQueue();
        // 绑定队列和交换机
        channel.queueBind(queueName, EXCHANGE_NAME, ROUTING_KEY1);
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
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC);
        // 创建临时队列
        String queueName = channel.queueDeclare().getQueue();
        // 绑定队列和交换机
        channel.queueBind(queueName, EXCHANGE_NAME, ROUTING_KEY2);
        channel.queueBind(queueName, EXCHANGE_NAME, ROUTING_KEY3);
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
