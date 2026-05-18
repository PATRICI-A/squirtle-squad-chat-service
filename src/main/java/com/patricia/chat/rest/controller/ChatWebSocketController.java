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

/**
 * WebSocket controller for handling real-time chat messages via STOMP.
 * Listens for incoming messages to a Parche and broadcasts them to subscribers.
 */
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

    /**
     * Handles incoming messages from clients for a specific Parche.
     * Persists the message using the use case and broadcasts the result to all
     * users subscribed to the Parche's topic.
     *
     * @param parcheId the UUID of the Parche where the message is sent
     * @param request the payload containing message text or media
     * @param principal the authenticated user principal
     */
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

    /**
     * Handles incoming private messages from clients for a specific friend.
     * Persists the message using the use case and broadcasts the result to both
     * the sender and the receiver.
     *
     * @param friendId the UUID of the friend
     * @param request the payload containing message text or media
     * @param principal the authenticated user principal
     */
    @MessageMapping("/friends/{friendId}/messages")
    public void handlePrivateMessage(@DestinationVariable UUID friendId,
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

        Message message = sendMessageUseCase.sendPrivateMessage(
                senderId, senderName, friendId,
                request.getContent(), request.getImageUrl());

        MessageResponse response = messageMapper.toResponse(message);

        // Envía al destinatario
        messagingTemplate.convertAndSend("/topic/friends/" + friendId, response);
        
        // Envía al remitente (para que se actualice su vista)
        messagingTemplate.convertAndSend("/topic/friends/" + senderId, response);
    }
}