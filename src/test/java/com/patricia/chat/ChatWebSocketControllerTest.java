package com.patricia.chat.rest.controller;

import com.patricia.chat.application.dto.request.SendMessageRequest;
import com.patricia.chat.application.dto.response.MessageResponse;
import com.patricia.chat.application.mapper.MessageMapper;
import com.patricia.chat.domain.model.Message;
import com.patricia.chat.domain.ports.in.SendMessageUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.security.Principal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatWebSocketControllerTest {

    @Mock SendMessageUseCase sendMessageUseCase;
    @Mock SimpMessagingTemplate messagingTemplate;
    @Mock MessageMapper messageMapper;

    @InjectMocks ChatWebSocketController controller;

    @Test
    void handleMessage_withPrincipal_sendsToTopic() {
        UUID parcheId = UUID.randomUUID();
        UUID senderId = UUID.randomUUID();

        SendMessageRequest request = new SendMessageRequest();
        request.setContent("Hola");
        request.setImageUrl(null);

        Message message = new Message();
        message.setId(UUID.randomUUID());
        MessageResponse response = new MessageResponse();
        response.setId(message.getId());

        Principal principal = () -> senderId.toString();

        when(sendMessageUseCase.sendMessage(eq(parcheId), eq(senderId), eq("Usuario"), eq("Hola"), isNull()))
                .thenReturn(message);
        when(messageMapper.toResponse(message)).thenReturn(response);

        controller.handleMessage(parcheId, request, principal);

        verify(messagingTemplate).convertAndSend("/topic/parches/" + parcheId, response);
    }

    @Test
    void handleMessage_withoutPrincipal_generatesSenderId() {
        UUID parcheId = UUID.randomUUID();

        SendMessageRequest request = new SendMessageRequest();
        request.setContent("Hola");
        request.setImageUrl(null);

        Message message = new Message();
        message.setId(UUID.randomUUID());
        MessageResponse response = new MessageResponse();
        response.setId(message.getId());

        when(sendMessageUseCase.sendMessage(any(), any(), anyString(), eq("Hola"), isNull())).thenReturn(message);
        when(messageMapper.toResponse(message)).thenReturn(response);

        controller.handleMessage(parcheId, request, null);

        ArgumentCaptor<UUID> senderCaptor = ArgumentCaptor.forClass(UUID.class);
        verify(sendMessageUseCase).sendMessage(eq(parcheId), senderCaptor.capture(), eq("Usuario"), eq("Hola"), isNull());
        assertThat(senderCaptor.getValue()).isNotNull();
        verify(messagingTemplate).convertAndSend("/topic/parches/" + parcheId, response);
    }
}

