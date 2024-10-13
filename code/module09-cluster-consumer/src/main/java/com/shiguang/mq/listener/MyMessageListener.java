package com.shiguang.mq.listener;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Created By Shiguang On 2024/10/11 16:02
 */
@Component
@Slf4j
public class MyMessageListener {
    public static final String QUEUE_CLUSTER = "queue.cluster.test";
    public static final String QUEUE_QUORUM_TEST = "queue.quorum.test";

    @RabbitListener(queues = {QUEUE_CLUSTER})
    public void processPriorityMessage(String dataString, Message message, Channel channel) throws IOException {
        log.info("[消费者端] 消息内容: " + dataString);
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

    @RabbitListener(queues = {QUEUE_QUORUM_TEST})
    public void processQuorumMessage(String dataString, Message message, Channel channel) throws IOException {
        log.info("[消费者端] 消息内容: " + dataString);
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

}
