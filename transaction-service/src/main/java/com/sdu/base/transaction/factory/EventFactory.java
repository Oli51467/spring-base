package com.sdu.base.transaction.factory;

import com.sdu.base.transaction.event.AnnotationTransactionSendEvent;
import com.sdu.base.transaction.event.RabbitSendEvent;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.context.ApplicationEvent;
import org.springframework.stereotype.Component;

@Component
public class EventFactory {

    public ApplicationEvent createEvent(String event, String exchange, String routingKey, Message message, CorrelationData correlationData) {
        switch (event) {
            case "annotation":
                return new AnnotationTransactionSendEvent(this, exchange, routingKey, message, correlationData);
            default:
                return new RabbitSendEvent(this, exchange, routingKey, message, correlationData);
        }
    }
}
