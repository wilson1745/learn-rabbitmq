package com.shiguang.mq.config;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ReturnedMessage;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

/**
 * Created By Shiguang On 2024/10/13 16:15
 */

@Configuration
@Slf4j
public class MQProducerAckConfig implements RabbitTemplate.ConfirmCallback, RabbitTemplate.ReturnsCallback {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @PostConstruct
    public void init() {
        rabbitTemplate.setConfirmCallback(this);
        rabbitTemplate.setReturnsCallback(this);
    }

    /**
     * 消息发送到交换机成功或失败时调用这个方法
     *
     * @param correlationData 用于关联消息的唯一标识符
     * @param ack             表示消息是否被成功确认
     * @param cause           如果消息确认失败，这里会包含失败的原因
     */
    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        if (ack) {
            log.info("消息发送到交换机成功！数据: " + correlationData);
        } else {
            log.info("消息发送到交换机失败！ 数据: " + correlationData + " 错误原因: " + cause);
        }

    }

    /**
     * 当消息无法路由到队列时调用这个方法
     *
     * @param returnedMessage 包含无法路由的消息的详细信息
     */
    @Override
    public void returnedMessage(ReturnedMessage returnedMessage) {
        log.info("returnedMessage() 回调函数 消息主体: " + new String(returnedMessage.getMessage().getBody()));
        log.info("returnedMessage() 回调函数 应答码: " + returnedMessage.getReplyCode());
        log.info("returnedMessage() 回调函数 描述: " + returnedMessage.getReplyText());
        log.info("returnedMessage() 回调函数 消息使用的交换器 exchange: " + returnedMessage.getExchange());
        log.info("returnedMessage() 回调函数 消息使用的路由键 routing: " + returnedMessage.getRoutingKey());
    }
}
