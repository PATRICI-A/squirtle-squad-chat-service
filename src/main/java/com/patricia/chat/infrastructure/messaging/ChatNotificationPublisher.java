package com.patricia.chat.infrastructure.messaging;

import com.patricia.chat.domain.model.Message;
import com.patricia.chat.infrastructure.messaging.dto.ChatMessageEventDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChatNotificationPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange.chat}")
    private String chatExchange;

    @Value("${rabbitmq.routing-key.chat-message}")
    private String chatMessageRoutingKey;

    public void publishChatMessage(Message message) {
        if (message.getReceiverId() == null) return;

        ChatMessageEventDto event = ChatMessageEventDto.builder()
                .recipientUserId(message.getReceiverId())
                .senderName(message.getSenderName())
                .conversationId(message.getId())
                .build();

        rabbitTemplate.convertAndSend(chatExchange, chatMessageRoutingKey, event);
    }
}

