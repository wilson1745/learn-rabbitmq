package com.shiguang.mq;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created By Shiguang On 2024/10/12 15:36
 */
@SpringBootTest
@Slf4j
public class RabbitMQTest {
    public static final String EXCHANGE_NAME = "exchange.tx.dragon";
    public static final String ROUTING_KEY = "routing.key.tx.dragon";

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Test
    @Transactional
    //@Rollback(value = false) //junit 默认都是回滚事务的,所以想提交事务,需要设置为false
    public void testSendMessageTx(){
        // 1、 发送一条消息
        rabbitTemplate.convertAndSend(EXCHANGE_NAME, ROUTING_KEY, "hello rabbitmq tx message 1");

        // 2、抛出异常
        log.info("do bad: "+ 10/0);

        // 3、发送第二条消息
        rabbitTemplate.convertAndSend(EXCHANGE_NAME, ROUTING_KEY, "hello rabbitmq tx message 2");
    }
}
