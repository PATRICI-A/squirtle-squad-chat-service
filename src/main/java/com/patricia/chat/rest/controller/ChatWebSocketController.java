package com.patricia.chat.rest.controller;

import com.patricia.chat.application.dto.request.SendMessageRequest;
import com.patricia.chat.application.dto.response.MessageResponse;
import com.patricia.chat.application.mapper.MessageMapper;
import com.patricia.chat.domain.model.Message;
import com.patricia.chat.domain.ports.in.SendMessageUseCase;
import com.patricia.chat.infrastructure.config.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.UUID;

/**
 * Controlador WebSocket para el chat de parches (RF10).
 *
 * Flujo:
 *  Cliente conecta a /ws/chat (STOMP)
 *  Cliente se suscribe a /topic/parches/{parcheId}
 *  Cliente envía mensaje a /app/parches/{parcheId}/messages
 *  Servidor valida, persiste y hace broadcast a /topic/parches/{parcheId}
 */
@Controller
public class ChatWebSocketController {

    private final SendMessageUseCase sendMessageUseCase;
    private final SimpMessagingTemplate messagingTemplate;
    private final MessageMapper messageMapper;
    private final JwtUtil jwtUtil;

    public ChatWebSocketController(SendMessageUseCase sendMessageUseCase,
                                   SimpMessagingTemplate messagingTemplate,
                                   MessageMapper messageMapper,
                                   JwtUtil jwtUtil) {
        this.sendMessageUseCase = sendMessageUseCase;
        this.messagingTemplate  = messagingTemplate;
        this.messageMapper      = messageMapper;
        this.jwtUtil            = jwtUtil;
    }

    @MessageMapping("/parches/{parcheId}/messages")
    public void handleMessage(@DestinationVariable UUID parcheId,
                              @Valid @Payload SendMessageRequest request,
                              Principal principal) {

        // El Principal fue seteado por el interceptor JWT en WebSocketConfig
        UUID senderId   = UUID.fromString(principal.getName());
        // El nombre se recupera del token al momento de la conexión (simplificado aquí)
        String senderName = "Usuario"; // En producción, guardarlo en sesión o en el token

        Message message = sendMessageUseCase.sendMessage(
                parcheId, senderId, senderName,
                request.getContent(), request.getImageUrl());

        MessageResponse response = messageMapper.toResponse(message);

        // Broadcast a todos los suscriptores del canal del parche
        messagingTemplate.convertAndSend("/topic/parches/" + parcheId, response);
    }
}
