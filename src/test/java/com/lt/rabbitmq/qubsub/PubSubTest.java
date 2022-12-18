package com.lt.rabbitmq.qubsub;

import com.lt.rabbitmq.utils.RabbitMqUtil;
import com.rabbitmq.client.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * @description: 发布订阅模型  广播模型  要使用交换机了    fanout : 扇出  广播
 * @author: ~Teng~
 * @date: 2022/12/18 16:52
 */
public class PubSubTest {

    private static final String EXCHANGE_NAME = "pubsub-exchange";

    @Test
    public void publish() throws Exception {
        Connection connection = RabbitMqUtil.getConnection();
        Channel channel = connection.createChannel();
        // Ps：exchange是不会帮你将消息持久化到本地的，Queue才会帮你持久化消息。
        // 创建交换机 - 绑定某一个队列
        // 参数1： exchange  交换机的名称
        // 参数2： 指定exchange  交换机的类型
        // FANOUT - pubsub  广播类型 ,   DIRECT - Routing , TOPIC - Topics
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.FANOUT);
        // 第一次 发布消息 到 交换机
        // 广播模式下路由key 是没有用的 routingKey 没有意义  所以空着不写
        String msg = "Hello-PubSub！";
        channel.basicPublish(EXCHANGE_NAME, "", null, msg.getBytes(StandardCharsets.UTF_8));
        // 第二次 发布消息 到 交换机
        channel.basicPublish(EXCHANGE_NAME, "", null, msg.getBytes(StandardCharsets.UTF_8));
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
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.FANOUT);
        // 创建临时队列
        String queueName = channel.queueDeclare().getQueue();
        // 绑定交换机和队列
        channel.queueBind(queueName, EXCHANGE_NAME, "");
        // 指定当前消费者一次消费多少消息
        channel.basicQos(1);
        // 设置一个回调，消费队列中的消息 指定手动 ack
        DefaultConsumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
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
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.FANOUT);
        // 获取临时队列
        String queueName = channel.queueDeclare().getQueue();
        // 绑定交换机和队列
        channel.queueBind(queueName, EXCHANGE_NAME, "");
        // 设置消费者一次消费多少消息
        channel.basicQos(1);
        // 设置回调 进行消费
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
