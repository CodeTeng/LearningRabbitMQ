package com.lt.rabbitmq.helloworld;

import com.lt.rabbitmq.utils.RabbitMqUtil;
import com.rabbitmq.client.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * @description:
 * @author: ~Teng~
 * @date: 2022/12/18 15:19
 */
public class HelloWorldTest {

    /**
     * 队列名称
     */
    private static final String QUEUE_NAME = "hello";


    /**
     * 生产者发布消息到队列
     */
    @Test
    public void publish() throws Exception {
        // 1. 获取 Connection
        Connection connection = RabbitMqUtil.getConnection();
        // 2. 创建Channel
        Channel channel = connection.createChannel();
        // 3. 发布消息到 exchange，同时指定路由规则
        String msg = "Hello-World222";
        // 发布消息
        // 参数1：指定 exchange 交换机，当前模式是 生产者---队列---消费者 模式
        // 参数2：指定路由的规则，使用具体的队列名称    队列名称
        // 参数3：指定传递消息所携带的 properties，使用 null
        // 比如：MessageProperties.PERSISTENT_BASIC 表示持久化消息
        // 参数4：指定发布的具体消息，byte[]类型
        // 第一个参数是 exchangeName (默认情况下代理服务器端是存在一个""名字的 exchange 的，
        // 因此如果不创建 exchange 的话我们可以直接将该参数设置成 ""，如果创建了 exchange 的话，
        // 我们需要将该参数设置成创建的 exchange 的名字)，第二个参数是路由键
        channel.basicPublish("", QUEUE_NAME, null, msg.getBytes(StandardCharsets.UTF_8));
        // Ps: exchange 是不会帮你将消息持久化到本地的，Queue 才会帮你持久化消息
        System.out.println("生产者发布消息成功！");
        // 4. 释放资源
        channel.close();
        connection.close();
    }

    /**
     * 消费者消费消息
     */
    @Test
    public void consume() throws Exception {
        // 1. 获取连接对象
        Connection connection = RabbitMqUtil.getConnection();
        // 2. 创建 channel
        Channel channel = connection.createChannel();
        // 3. 管道绑定队列
        // 参数1：queue - 指定队列的名称
        // 参数2：durable - 当前队列是否需要持久化
        //参数3：exclusive - 是否排外conn.close()-当前队列会被自动删除，当前队列只能被一个消费者消费
        //参数4：autoDelete - 如果这个队列没有消费者在消费，队列自动删除
        //参数5：arguments - 指定当前队列的其他信息
        channel.queueDeclare(QUEUE_NAME, true, false, false, null);
        /**
         * DefaultConsumer 是  Consumer 接口的实现类    接口中的定义的方法如下 这个不作为重点   了解即可
         * handleCancel：除了调用basicCancel的其他原因导致消息被取消时调用。
         * handleCancelOk：basicCancel调用导致的订阅取消时被调用。
         * handleConsumeOk：任意basicComsume调用导致消费者被注册时调用。
         * handleDelivery：消息接收时被调用。
         * handleRecoverOk：basic.recover-ok被接收时调用
         * handleShutdownSignal：当Channel与Conenction关闭的时候会调用。
         */
        // 参数1：queue - 指定消费哪个队列
        // 参数2：autoAck - 指定是否自动ACK 开启自动确认机制 （true，接收到消息后，会立即告诉RabbitMQ）
        // 参数3：consumer - 消费回调接口
        // 4. 设置一个回调，消费队列中的消息
        // 简易版自定义 Consumer
        // 只需要重写 DefaultConsumer 的 handleDelivery 方法即可取出消息，额外属性新增属性等操作
        channel.basicConsume(QUEUE_NAME, true, new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                System.out.println("接收到消息：" + new String(body, StandardCharsets.UTF_8));
            }
        });
        System.out.println("消费者开始监听队列！");
        // 不要让消费者线程结束 否则看不到监听效果了   如果没有这行代码  下面不要关闭通道和连接
        System.in.read();
        // 5. 释放资源
        channel.close();    // 不建议关闭 通道和来连接
        connection.close();
    }
}
