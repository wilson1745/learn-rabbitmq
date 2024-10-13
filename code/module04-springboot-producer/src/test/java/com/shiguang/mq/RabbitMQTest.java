package com.shiguang.mq;

/**
 * Created By Shiguang On 2024/10/11 16:49
 */

import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class RabbitMQTest {

    public static final String EXCHANGE_DIRECT = "exchange.direct.order";
    public static final String ROUTING_KEY = "order";
    public static final String QUEUE_NAME = "queue.order";
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Test
    public void test01SendMessage(){
        String message = "Hello Rabbit!!";
        rabbitTemplate.convertAndSend(EXCHANGE_DIRECT,ROUTING_KEY,message);
    }

}
