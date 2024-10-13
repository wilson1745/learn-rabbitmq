package com.shiguang.mq.stream;

import com.rabbitmq.stream.Environment;

/**
 * Created By Shiguang On 2024/10/13 20:58
 */
public class TestConsumer {
    public static void main(String[] args) {
        Environment environment = Environment.builder()
                .host("192.168.10.66")
                .port(33333)
                .username("shiguang")
                .password("123456")
                .build();

        environment.consumerBuilder()
                .stream("stream.shiguang.test")
                .name("stream.shiguang.test.consumer")
                .autoTrackingStrategy()
                .builder()
                .messageHandler((offset, message) -> {
                    byte[] bodyAsBinary = message.getBodyAsBinary();
                    String messageContent = new String(bodyAsBinary);
                    System.out.println("[消费者端] messagecontent = " + messageContent + " offset = " + offset.offset());
                })
                .build();
    }
}
