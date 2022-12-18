package com.lt.rabbitmq.utils;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/**
 * @description:
 * @author: ~Teng~
 * @date: 2022/12/18 14:44
 */
public class RabbitMqUtil {
    public static Connection getConnection() {
        // 创建连接mq 的连接工厂对象
        ConnectionFactory factory = new ConnectionFactory();
        // 设置 rabbitmq 的主机
        factory.setHost("124.220.194.38");
        // 设置端口号
        factory.setPort(5672);
        // 设置用户名
        factory.setUsername("test");
        // 设置密码
        factory.setPassword("test");
        // 设置连接哪个虚拟机
        factory.setVirtualHost("/test");
        // 创建Connection
        Connection conn = null;
        try {
            conn = factory.newConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 返回
        return conn;
    }
}
