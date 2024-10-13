package com.shiguang.mq.listener;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created By Shiguang On 2024/10/11 16:02
 */
@Component
@Slf4j
public class MyMessageListener {
    public static final String QUEUE_NAME = "queue.order";
    public static final String QUEUE_NORMAL = "queue.normal.video";
    public static final String QUEUE_DEAD_LETTER = "queue.dead.letter.video";
    public static final String QUEUE_DELAY = "queue.test.delay";
    public static final String QUEUE_PRIORITY = "queue.test.priority";

    // @RabbitListener(queues = {QUEUE_NAME})
    public void processMessage(String dataString, Message message, Channel channel) throws IOException {
        // 获取当前消息的唯一标识
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        try {
            // 核心操作
            log.info("消费端接收到消息：{}", dataString);
//            System.out.println(1/0);
            // 核心操作成功,返回 ACK 信息
            // deliveryTag: 消息的唯一标识,64 位的长整型,消息往消费端投递时,会分配一个唯一的 deliveryTag 值
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            // 获取当前消息是否是重复投递的,true 说明当前消息已经重试过一次了, false 说明当前消息是第一次投递
            Boolean redelivered = message.getMessageProperties().getRedelivered();

            // 核心操作失败,返回 NACK 信息是否重新入队,true 表示重新入队, false 表示丢弃
            // requeue:
            if (redelivered) {
                // 如果当前消息已经是重复投递的,则说明此前已经重试过一次了,则不再重试过了,直接丢弃
                channel.basicNack(deliveryTag, false, false);
            } else {
                // 如果当前消息不是重复投递的,则说明此前没有重试过一次,则重试过一次,重新入队
                channel.basicNack(deliveryTag, false, true);
            }

            throw new RuntimeException(e);
        }
    }

    @RabbitListener(queues = {QUEUE_NAME})
    public void processDelayMessagey(String dataString, Message message, Channel channel) throws IOException, InterruptedException {
        // 核心操作
        log.info("消费端接收到消息：{}", dataString);

        TimeUnit.SECONDS.sleep(1); //延迟 1 秒

        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

    /**
     * 监听正常队列
     */
    @RabbitListener(queues = {QUEUE_NORMAL})
    public void processNormalMessage(Message message, Channel channel) throws IOException {
        // 监听正常队列,但是拒绝消息
        log.info("★[normal] 消息接收到,但我拒绝。");
        channel.basicReject(message.getMessageProperties().getDeliveryTag(), false);
    }

    /**
     * 监听死信队列
     */
    @RabbitListener(queues = {QUEUE_DEAD_LETTER})
    public void processDeadMessage(String dataString, Message message, Channel channel) throws IOException {
        //监听死信队列
        log.info("★[dead letter] dataString = " + dataString);
        log.info("★[dead1 etter] 我是死信监听方法,我接收到了死信消息");
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

    @RabbitListener(queues = {QUEUE_DELAY})
    public void processDelayMessage(String dataString, Message message, Channel channel) throws IOException {
        //监听死信队列
        log.info("[delay message] [消息本身] " + dataString);
        log.info("[delay message] [当前时间] " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

    @RabbitListener(queues = {QUEUE_PRIORITY})
    public void processPriorityMessage(String dataString, Message message, Channel channel) throws IOException {
        log.info("[priority]: " + dataString);
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

}
