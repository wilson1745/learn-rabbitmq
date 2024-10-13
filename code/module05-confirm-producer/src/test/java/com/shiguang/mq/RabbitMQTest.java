package com.shiguang.mq;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created By Shiguang On 2024/10/11 20:16
 */

@SpringBootTest
public class RabbitMQTest {
    public static final String EXCHANGE_DIRECT = "exchange.direct.order";
    public static final String EXCHANGE_TIMEOUT = "exchange.test.timeout";
    public static final String ROUTING_KEY = "order";
    public static final String ROUTING_KEY_TIMEOUT = "routing.key.test.timeout";
    public static final String EXCHANGE_NORMAL = "exchange.normal.video";
    public static final String ROUTING_KEY_DEAD_LETTER = "routing.key.dead.letter.video";
    public static final String EXCHANGE_DEAD_LETTER = "exchange.dead.letter.video";
    public static final String ROUTING_KEY_NORMAL = "routing.key.normal.video";
    public static final String EXCHANGE_DELAY = "exchange.test.delay";
    public static final String ROUTING_KEY_DELAY = "routing.key.test.delay";
    public static final String EXCHANGE_PRIORITY = "exchange.test.priority";
    public static final String ROUTING_KEY_PRIORITY = "routing.key.test.priority";

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Test
    public void test01SendMessage() {
        String message = "Message Confirm Test !!";
//        rabbitTemplate.convertAndSend(EXCHANGE_DIRECT,ROUTING_KEY,message);
//        rabbitTemplate.convertAndSend(EXCHANGE_DIRECT + "~", ROUTING_KEY, message);
        rabbitTemplate.convertAndSend(EXCHANGE_DIRECT, ROUTING_KEY + "~", message);
    }

    @Test
    public void test02SendMessage() {
        for (int i = 0; i < 100; i++) {
            String message = "Test Rrefetch!!" + i;
            rabbitTemplate.convertAndSend(EXCHANGE_DIRECT, ROUTING_KEY, message);
        }
    }

    @Test
    public void test03SendMessage() {
        String message = "Test Timeout!!";
        rabbitTemplate.convertAndSend(EXCHANGE_TIMEOUT, ROUTING_KEY_TIMEOUT, message);
    }

    @Test
    public void test04SendMessage() {

        // 创建消息后置处理器对象
        MessagePostProcessor processor = message -> {
            // 设置消息的过期时间为 7 秒
            message.getMessageProperties().setExpiration("7000");
            return message;
        };

        String message = "Test Timeout!!";

        rabbitTemplate.convertAndSend(EXCHANGE_TIMEOUT, ROUTING_KEY_TIMEOUT, message, processor);
    }

    @Test
    public void testSendRejectMessage() {
        rabbitTemplate.convertAndSend(EXCHANGE_NORMAL, ROUTING_KEY_DEAD_LETTER, "测试死信情况1:消息被拒绝");
    }


    @Test
    public void testSendMultiMessage() {
        for (int i = 0; i < 20; i++) {
            rabbitTemplate.
                    convertAndSend(
                            EXCHANGE_NORMAL,
                            ROUTING_KEY_NORMAL,
                            "测试死信情况2:数量超过队列最大容量" + i);
        }
    }

    @Test
    public void testSendDelayMessage() {
        for (int i = 0; i < 8; i++) {
            rabbitTemplate.
                    convertAndSend(
                            EXCHANGE_NORMAL,
                            ROUTING_KEY_NORMAL,
                            "测试死信情况3:消息超时未消费" + i);
        }
    }


    @Test
    public void sendDelayMessageByPlugin() {
        // 创建消息后置处理器对象
        MessagePostProcessor processor = message -> {
            // x-delay: 消息的过期时间 (单位:毫秒)
            // 安装 rabbitmq_delayed_message_exchange 插件才生效
            message.getMessageProperties().setHeader("x-delay", 10000);
            return message;
        };

        rabbitTemplate.
                convertAndSend(
                        EXCHANGE_DELAY,
                        ROUTING_KEY_DELAY,
                        "Test Delay Message By Plugin" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()),
                        processor);
    }


    @Test
    public void testSendPriorityMessage() {
        rabbitTemplate.
                convertAndSend(
                        EXCHANGE_PRIORITY,
                        ROUTING_KEY_PRIORITY,
                        "Test Priority Message 3",message -> {
                            //消息本身的优先级数据,不能超过队列配置的最大值 x-max-priority
                            message.getMessageProperties().setPriority(3);
                            return message;
                        });
    }


}
