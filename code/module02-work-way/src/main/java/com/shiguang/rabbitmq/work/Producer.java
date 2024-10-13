package com.shiguang.rabbitmq.work;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.shiguang.rabbitmq.util.ConnectionUtil;

/**
 * Created By Shiguang On 2024/10/11 12:30
 */
public class Producer {
    public static final String QUEUE_NAME = "work_queue";
    public static void main(String[] args) throws Exception {
        Connection connection = ConnectionUtil.getConnection();

        Channel channel = connection.createChannel();

        channel.queueDeclare(QUEUE_NAME, true, false, false, null);

        for (int i = 1; i <= 10; i++) {
            String body = i + "hello rabbitmq";
            channel.basicPublish("", QUEUE_NAME, null, body.getBytes());
        }

        channel.close();

    }
}
