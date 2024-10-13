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
    public static final String QUEUE_NAME = "fed.queue.demo";
    @RabbitListener(queues = {QUEUE_NAME})
    public void processPriorityMessage(String dataString, Message message, Channel channel) throws IOException {
        log.info("[消费端] 内容: " + dataString);
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

}
