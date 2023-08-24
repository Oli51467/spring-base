package com.sdu.base.transaction.listener;

import com.rabbitmq.client.Channel;
import com.sdu.base.transaction.service.TransMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 系统死信监听的类，可配置是否监听死信队列
 */
@Component
@Slf4j
@ConditionalOnProperty("rabbix.dlxEnabled")
public abstract class AbstractDlxListener implements ChannelAwareMessageListener {

    @Autowired
    private TransMessageService transMessageService;

    public abstract boolean receiveMessage(Message message) throws IOException;

    @Override
    public void onMessage(Message message, Channel channel) throws Exception {
        String messageBody = new String(message.getBody());
        log.error("dead letter! message: {}", message);

        MessageProperties properties = message.getMessageProperties();
        boolean save = false;
        try {
            save = receiveMessage(message);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally{
            channel.basicAck(properties.getDeliveryTag(), false);
            if (save) {
                transMessageService.handleMessageDead(properties.getMessageId(), properties.getReceivedExchange(),
                        properties.getReceivedRoutingKey(), properties.getConsumerQueue(), messageBody);
            }
        }
    }
}
