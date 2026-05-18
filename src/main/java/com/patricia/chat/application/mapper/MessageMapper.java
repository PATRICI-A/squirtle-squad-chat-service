package com.patricia.chat.application.mapper;

import com.patricia.chat.application.dto.response.MessageResponse;
import com.patricia.chat.domain.model.Message;
import org.springframework.stereotype.Component;

@Component
public class MessageMapper {

    public MessageResponse toResponse(Message message) {
        MessageResponse response = new MessageResponse();
        response.setId(message.getId());
        response.setParcheId(message.getParcheId());
        response.setSenderId(message.getSenderId());
        response.setReceiverId(message.getReceiverId());
        response.setSenderName(message.getSenderName());
        response.setContent(message.getContent());
        response.setType(message.getType());
        response.setImageUrl(message.getImageUrl());
        response.setSentAt(message.getSentAt());
        return response;
    }
}
