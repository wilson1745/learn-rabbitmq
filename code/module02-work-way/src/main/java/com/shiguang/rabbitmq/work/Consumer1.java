package com.shiguang.rabbitmq.work;

import com.rabbitmq.client.*;
import com.shiguang.rabbitmq.util.ConnectionUtil;

import java.io.IOException;

/**
 * Created By Shiguang On 2024/10/11 12:46
 */
public class Consumer1 {
    static final String QUEUE_NAME = "work_queue";

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
            }
        };

        channel.basicConsume(QUEUE_NAME, true, consumer);
    }
}
