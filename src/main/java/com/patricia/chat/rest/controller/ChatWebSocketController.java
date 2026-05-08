package com.patricia.chat.rest.controller;

import com.patricia.chat.application.dto.request.SendMessageRequest;
import com.patricia.chat.application.dto.response.MessageResponse;
import com.patricia.chat.application.mapper.MessageMapper;
import com.patricia.chat.domain.model.Message;
import com.patricia.chat.domain.ports.in.SendMessageUseCase;
import jakarta.validation.Valid;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.security.Principal;
import java.util.UUID;

@Controller
public class ChatWebSocketController {

    private final SendMessageUseCase sendMessageUseCase;
    private final SimpMessagingTemplate messagingTemplate;
    private final MessageMapper messageMapper;

    public ChatWebSocketController(SendMessageUseCase sendMessageUseCase,
                                   SimpMessagingTemplate messagingTemplate,
                                   MessageMapper messageMapper) {
        this.sendMessageUseCase = sendMessageUseCase;
        this.messagingTemplate  = messagingTemplate;
        this.messageMapper      = messageMapper;
    }

    @MessageMapping("/parches/{parcheId}/messages")
    public void handleMessage(@DestinationVariable UUID parcheId,
                              @Valid @Payload SendMessageRequest request,
                              Principal principal) {

        UUID senderId = principal != null
                ? UUID.fromString(principal.getName())
                : UUID.randomUUID();

        String senderName = "Usuario";
        if (principal instanceof UsernamePasswordAuthenticationToken auth
                && auth.getDetails() instanceof String email) {
            senderName = email;
        }

        Message message = sendMessageUseCase.sendMessage(
                parcheId, senderId, senderName,
                request.getContent(), request.getImageUrl());

        MessageResponse response = messageMapper.toResponse(message);

        messagingTemplate.convertAndSend("/topic/parches/" + parcheId, response);
    }
}