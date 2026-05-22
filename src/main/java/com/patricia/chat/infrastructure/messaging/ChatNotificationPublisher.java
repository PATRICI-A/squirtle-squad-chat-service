package com.patricia.chat.infrastructure.messaging;

import com.patricia.chat.domain.model.Message;
import com.patricia.chat.infrastructure.config.RabbitMQConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
public class ChatNotificationPublisher {

    private final RabbitTemplate rabbitTemplate;

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

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE,
                RabbitMQConfig.ROUTING_KEY,
                event
        );

        log.info("Publishing chat notification to exchange: {}", RabbitMQConfig.EXCHANGE);
    }
}