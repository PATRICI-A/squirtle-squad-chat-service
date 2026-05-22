package com.patricia.chat.infrastructure.messaging;

import com.patricia.chat.domain.model.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

import java.util.Map;

@Slf4j
@Component
public class ChatNotificationPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange.chat}")
    private String chatExchange;

    @Value("${rabbitmq.routing-key.chat:chat.notification}")
    private String routingKey;

    public ChatNotificationPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishPrivateMessageNotification(Message message) {
        Map<String, Object> event = Map.of(
                "type",       "CHAT_MESSAGE",
                "senderId",   message.getSenderId().toString(),
                "receiverId", message.getReceiverId().toString(),
                "senderName", message.getSenderName(),
                "content",    message.getContent(),
                "sentAt",     message.getSentAt().toString()
        );

        rabbitTemplate.convertAndSend(chatExchange, routingKey, event);
        log.info("Publishing chat notification to exchange: {}", chatExchange);
    }
}