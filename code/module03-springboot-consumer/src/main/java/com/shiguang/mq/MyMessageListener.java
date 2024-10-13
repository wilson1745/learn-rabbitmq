package com.shiguang.mq;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * Created By Shiguang On 2024/10/11 16:02
 */
@Component
@Slf4j
public class MyMessageListener {
    public static final String EXCHANGE_DIRECT = "exchange.direct.order";
    public static final String ROUTING_KEY = "order";
    public static final String QUEUE_NAME = "queue.order";

    // 写法一: 监听 + 在 RabbitMQ 服务器上创建交换机、队列、绑定关系
//    @RabbitListener(bindings = @QueueBinding(
//            value = @Queue(value = QUEUE_NAME, durable = "true"),
//            exchange = @Exchange(value = EXCHANGE_DIRECT),
//            key = {ROUTING_KEY}
//    ))
//    public void processMessage(String dataString, Message message, Channel channel) {
//        log.info("消费端接收到消息：{}", dataString);
//        System.out.println("消费端接收到消息：" + dataString);
//    }

    // 写法二: 只监听
    @RabbitListener(queues = QUEUE_NAME)
    public void processMessage(String dataString, Message message, Channel channel) {
        log.info("消费端接收到消息：{}", dataString);
        System.out.println("消费端接收到消息：" + dataString);
    }
}
