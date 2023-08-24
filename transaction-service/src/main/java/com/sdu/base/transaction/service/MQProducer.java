package com.sdu.base.transaction.service;

import com.sdu.base.common.annotation.TransactionalExecute;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 发送mq工具类
 */
@Component
public class MQProducer {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void sendMsg(String exchange, String routingKey, Message message, CorrelationData correlationData) {
        rabbitTemplate.convertAndSend(exchange, routingKey, message, correlationData);
    }

    /**
     * 发送可靠消息，在事务提交后保证发送成功
     */
    @TransactionalExecute
    public void sendSecureMsg(String exchange, String routingKey, Message message, CorrelationData correlationData) {
        rabbitTemplate.convertAndSend(exchange, routingKey, message, correlationData);
    }
}
