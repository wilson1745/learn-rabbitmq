package com.shiguang.rabbitmq.simple;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Created By Shiguang On 2024/10/11 10:05
 */
public class Producer {
    public static void main(String[] args) throws IOException, TimeoutException {
        // 创建连接工厂
        ConnectionFactory connectionFactory = new ConnectionFactory();

        // 设置主机地址
        connectionFactory.setHost("192.168.10.66");

        // 设置端口号: 默认为5672
        connectionFactory.setPort(5672);

        // 虚拟主机名称: 默认为/
        connectionFactory.setVirtualHost("/");

        // 设置连接用户名: 默认为guest
        connectionFactory.setUsername("guest");

        // 设置连接密码: 默认为guest
        connectionFactory.setPassword("123456");

        // 创建连接
        Connection connection = connectionFactory.newConnection();

        // 创建通道
        Channel channel = connection.createChannel();

        // 声明队列
        // queue 名称
        // durable 是否持久化
        // exclusive 是否独占本次连接,若为true,则队列仅在本次连接可见,连接关闭后,队列自动删除
        // autoDelete 是否自动删除,若为true,则当最后一个消费者断开连接后,队列会被删除
        // arguments 其他参数
        channel.queueDeclare("simple_queue", true, false, false, null);

        // 发布消息
        String message = "hello rabbitmq";

        // exchange 交换机名称
        // routingKey 路由键,用于将消息路由到指定的队列,如果没有指定,消息将发送到默认的交换机,默认的交换机名称为空字符串
        // props 消息属性,用于设置消息的属性,如消息的优先级、过期时间等
        // body 消息体,即要发送的消息内容
        channel.basicPublish("", "simple_queue", null, message.getBytes());

        System.out.println("消息已发送:" + message + "");

        // 删除队列
        // queueName 队列名称
//        channel.queueDelete("simple_queue");

        // 关闭资源
        channel.close();
        connection.close();

    }
}
