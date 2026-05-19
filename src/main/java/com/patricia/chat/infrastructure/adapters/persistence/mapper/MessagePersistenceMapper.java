package com.patricia.chat.infrastructure.adapters.persistence.mapper;

import com.patricia.chat.domain.model.Message;
import com.patricia.chat.infrastructure.adapters.persistence.entity.MessageEntity;
import org.springframework.stereotype.Component;

@Component
public class MessagePersistenceMapper {

    public MessageEntity toEntity(Message message) {
        MessageEntity entity = new MessageEntity();
        entity.setId(message.getId());
        entity.setParcheId(message.getParcheId());
        entity.setSenderId(message.getSenderId());
        entity.setReceiverId(message.getReceiverId());
        entity.setSenderName(message.getSenderName());
        entity.setContent(message.getContent());
        entity.setType(message.getType());
        entity.setImageUrl(message.getImageUrl());
        entity.setSentAt(message.getSentAt());
        return entity;
    }

    public Message toDomain(MessageEntity entity) {
        Message message = new Message();
        message.setId(entity.getId());
        message.setParcheId(entity.getParcheId());
        message.setSenderId(entity.getSenderId());
        message.setReceiverId(entity.getReceiverId());
        message.setSenderName(entity.getSenderName());
        message.setContent(entity.getContent());
        message.setType(entity.getType());
        message.setImageUrl(entity.getImageUrl());
        message.setSentAt(entity.getSentAt());
        return message;
    }
}
