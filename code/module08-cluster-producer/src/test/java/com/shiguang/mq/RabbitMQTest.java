package com.shiguang.mq;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


/**
 * Created By Shiguang On 2024/10/11 20:16
 */

@SpringBootTest
public class RabbitMQTest {
    public static final String EXCHANGE_CLUSTER_TEST = "exchange.cluster.test";
    public static final String ROUTING_KEY_CLUSTER_TEST = "routing.key.cluster.test";
    public static final String EXCHANGE_QUORUM_TEST = "exchange.quorum.test";
    public static final String ROUTING_KEY_QUORUM_TEST = "routing.key.quorum.test";

    @Autowired
    private RabbitTemplate rabbitTemplate;


    @Test
    public void testSendMessage() {

        String message = "Test Send Message By Cluster !!";

        rabbitTemplate.convertAndSend(EXCHANGE_CLUSTER_TEST, ROUTING_KEY_CLUSTER_TEST, message);
    }

    @Test
    public void testSendMessageToQuorum() {

        String message = "Test Send Message By Quorum @@@!!";

        rabbitTemplate.convertAndSend(EXCHANGE_QUORUM_TEST, ROUTING_KEY_QUORUM_TEST, message);
    }


}
