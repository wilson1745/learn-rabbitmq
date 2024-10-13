package com.shiguang.mq.stream;

import com.rabbitmq.stream.Consumer;
import com.rabbitmq.stream.Environment;
import com.rabbitmq.stream.OffsetSpecification;

import java.util.concurrent.CountDownLatch;

/**
 * Created By Shiguang On 2024/10/13 20:45
 */
public class TestConsumerOffset {
    public static void main(String[] args) throws InterruptedException {
        Environment environment = Environment.builder()
                .host("192.168.10.66")
                .port(33333)
                .username("shiguang")
                .password("123456")
                .build();

        CountDownLatch countDownLatch = new CountDownLatch(1);

        Consumer consumer = environment.consumerBuilder()
                .stream("stream.shiguang.test")
                .offset(OffsetSpecification.first())
                .messageHandler((offset, message) -> {
                    byte[] bodyAsBinary = message.getBodyAsBinary();
                    String messageContent = new String(bodyAsBinary);
                    System.out.println("[消费者端] messagecontent = " + messageContent);
                    countDownLatch.countDown();
                })
                .build();

        countDownLatch.await();
        consumer.close();
    }
}
