package com.lt.rabbitmq.work;

import com.lt.rabbitmq.utils.RabbitMqUtil;
import com.rabbitmq.client.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * @description: Work 模型： 按劳分配  能者多劳
 * 现在有两个消费者：消费者1处理消息处理的慢2秒一个
 * 消费者2处理消息处理的快1秒一个，但是在自动确认模式下
 * channel.basicConsume("Work",true,consumer); Autoack  true，生产者发送了100条消息 会一下子全被接受 在web页面看不到被消费的过程
 * 而且消费者轮流依次消费 没有出现谁消费的快 就多消费一点的情况
 * 如果改成 手动确认模式生产者发送的 100 条消息会逐渐被消费 在web页面能看到被消费的过程而且哪个消费者消费的快就会多消费
 * 实际场景 我们希望 能者多劳 处理消息快的消费者 多处理一些
 * 所以不建议使用消息的自动确认应该改为手动确认
 * @author: ~Teng~
 * @date: 2022/12/18 16:14
 */
public class WorkTest {

    private static final String QUEUE_NAME = "Work";

    /**
     * 生产者
     */
    @Test
    public void publish() throws Exception {
        // 1. 获取Connection
        Connection connection = RabbitMqUtil.getConnection();
        // 2. 创建Channel
        Channel channel = connection.createChannel();
        // 3. 发布消息到exchange，同时指定路由的规则
        String msg = "Hello-Work!";
        // 参数1：指定exchange，使用""。
        // 参数2：指定路由的规则，使用具体的队列名称。
        // 参数3：指定传递的消息所携带的properties，使用null。
        // 参数4：指定发布的具体消息，byte[]类型
        for (int i = 0; i < 100; i++) {
            channel.basicPublish("", QUEUE_NAME, null, (i + msg).getBytes(StandardCharsets.UTF_8));
        }
        // Ps：exchange是不会帮你将消息持久化到本地的，Queue才会帮你持久化消息。
        System.out.println("生产者发布消息成功");
        // 4. 释放资源
        channel.close();
        connection.close();
    }

    /**
     * 消费者1 能力较弱 2s处理一个消息
     */
    @Test
    public void consume1() throws Exception {
        // 1. 获取Connection
        Connection connection = RabbitMqUtil.getConnection();
        // 2. 创建Channel
        Channel channel = connection.createChannel();

        // 3. 管道绑定队列
        // 参数1：queue - 指定队列的名称
        // 参数2：durable - 当前队列是否需要持久化（true）
        // 参数3：exclusive - 是否排外（conn.close() - 当前队列会被自动删除，当前队列只能被一个消费者消费）
        // 参数4：autoDelete - 如果这个队列没有消费者在消费，队列自动删除
        // 参数5：arguments - 指定当前队列的其他信息
        channel.queueDeclare(QUEUE_NAME, true, false, false, null);
        // 1. 指定当前消费者，一次消费多少个消息，没有过来的消息，还在队列中保存，这样设置，不会造成消息丢失
        // 因为假如不指定一次消费一条消息就有可能有多条消息到达消费者此时消费者一旦宕机到达消费者的消息也就丢了
        // 所以消息从队列到消费者一次来一条免得过来多条消息在半路丢了
        channel.basicQos(1);     // 不要一次性的把消息都给消费者容易丢失一次给一条安全

        // 4. 设置一个回调，消费队列中的消息 指定手动 ack
        DefaultConsumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("消费者1号接收到消息：" + new String(body, StandardCharsets.UTF_8));
                // 2. 手动ack
                // 参数1：long类型  标识队列中哪个具体的消息
                // 参数2：boolean 类型 是否开启多个消息同时确认
                channel.basicAck(envelope.getDeliveryTag(), false);
                // 处理完了消息,手动确认一下,队列再删除这个消息;这种机制保证消息永不丢失
                // 队列给消费者 一条消息,消费者收到消息,处理完了之后手动确认,确认了之后,队列才把消息删除,保证消息永不丢失
                // 而且消费者确认一个消息,队列发送一个消息,消费者确认的快,队列发送的快,能者多劳
            }
        };
        // 参数1：queue - 指定消费哪个队列
        // 参数2：autoAck - 指定是否自动ACK （true，接收到消息后，会立即告诉RabbitMQ）
        // 参数3：consumer - 指定消费回调
        channel.basicConsume(QUEUE_NAME, false, consumer);
        System.out.println("消费者开始监听队列！");
        System.in.read();
        // 5. 释放资源
        channel.close();
        connection.close();
    }

    /**
     * 消费者2 能力较强 1s处理一个消息
     */
    @Test
    public void consume2() throws Exception {
        Connection connection = RabbitMqUtil.getConnection();
        Channel channel = connection.createChannel();
        // 绑定队列
        channel.queueDeclare(QUEUE_NAME, true, false, false, null);
        // 指定当前消费者，一次消费多少个消息
        channel.basicQos(1);
        // 设置一个回调，消费队列中的消息 指定手动 ack
        DefaultConsumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                System.out.println("消费者2号接收到消息：" + new String(body, StandardCharsets.UTF_8));
                // 手动 ack
                channel.basicAck(envelope.getDeliveryTag(), false);
            }
        };
        channel.basicConsume(QUEUE_NAME, false, consumer);
        System.out.println("消费者开始监听队列！");
        System.in.read();
        // 5. 释放资源
        channel.close();
        connection.close();
    }
}
