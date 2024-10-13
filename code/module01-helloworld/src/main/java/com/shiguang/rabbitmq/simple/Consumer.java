package com.shiguang.rabbitmq.simple;

import com.rabbitmq.client.*;

import java.io.IOException;

/**
 * Created By Shiguang On 2024/10/11 11:17
 */
public class Consumer {
    public static void main(String[] args) throws Exception {
        // 1、创建一个ConnectionFactory，并设置主机名、端口号、虚拟主机、用户名和密码。
        ConnectionFactory factory = new ConnectionFactory();
        // 2、设置连接参数。
        factory.setHost("192.168.10.66");
        factory.setPort(5672);
        factory.setVirtualHost("/");
        factory.setUsername("guest");
        factory.setPassword("123456");

        // 3、通过工厂创建连接。
        Connection connection = factory.newConnection();

        // 4、通过连接创建通道。
        Channel channel = connection.createChannel();

        // 5、创建队列，并指定队列名称、是否持久化、是否独占、是否自动删除、其他参数。
        // 生产者已经创建了队列，这里不需要再创建
//        channel.queueDeclare("simple.queue", true, false, false, null);

        // 6、消费消息
        DefaultConsumer consumer = new DefaultConsumer(channel) {

            // consumerTag 消费者标签，用来标识消费者，在监听消息时使用。
            // envelope 消息的元数据，包括交换机、路由键、投递模式等。
            // properties 消息的属性，如消息的优先级、过期时间等。
            // body 消息的正文，即要发送的数据。
            @Override
            public void handleDelivery(String consumerTag,
                                       Envelope envelope,
                                       AMQP.BasicProperties properties,
                                       byte[] body) throws IOException {
                System.out.println("consumerTag: " + consumerTag);
                System.out.println("Exchange: " + envelope.getExchange());
                System.out.println("RoutingKey: " + envelope.getRoutingKey());
                System.out.println("properties: " + properties);
                System.out.println("body: " + new String(body));
            }
        };

        // 7、注册消费者，指定队列名称、是否自动应答、消费者。
        channel.basicConsume("simple_queue", true, consumer);

        // 8、关闭资源
        channel.close();
        connection.close();

    }
}
