package com.shiguang.rabbitmq.fanout;

import com.rabbitmq.client.*;
import com.shiguang.rabbitmq.util.ConnectionUtil;

import java.io.IOException;

/**
 * Created By Shiguang On 2024/10/11 13:32
 */
public class Consumer1 {
    static final String QUEUE_NAME = "fanout_queue1";

    public static void main(String[] args) throws Exception {

        Connection connection = ConnectionUtil.getConnection();

        Channel channel = connection.createChannel();

        channel.queueDeclare(QUEUE_NAME, true, false, false, null);
        DefaultConsumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag,
                                       Envelope envelope,
                                       AMQP.BasicProperties properties,
                                       byte[] body) throws IOException {
                System.out.println("Consumer1 Body: " + new String(body));
                System.out.println("队列1 消费者1 日志打印...");
            }
        };

        channel.basicConsume(QUEUE_NAME, true, consumer);
    }
}
