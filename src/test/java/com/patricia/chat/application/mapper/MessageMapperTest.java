package com.patricia.chat.application.mapper;

import com.patricia.chat.application.dto.response.MessageResponse;
import com.patricia.chat.application.mapper.MessageMapper;
import com.patricia.chat.domain.model.Message;
import com.patricia.chat.domain.model.MessageType;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class MessageMapperTest {

    private final MessageMapper mapper = new MessageMapper();

    @Test
    void toResponse_mapsAllFields() {
        Message message = new Message();
        UUID id = UUID.randomUUID();
        UUID parche = UUID.randomUUID();
        UUID sender = UUID.randomUUID();
        LocalDateTime sentAt = LocalDateTime.now();

        message.setId(id);
        message.setParcheId(parche);
        message.setSenderId(sender);
        message.setSenderName("Alice");
        message.setContent("Hello");
        message.setType(MessageType.TEXT);
        message.setImageUrl(null);
        message.setSentAt(sentAt);

        MessageResponse resp = mapper.toResponse(message);

        assertEquals(id, resp.getId());
        assertEquals(parche, resp.getParcheId());
        assertEquals(sender, resp.getSenderId());
        assertEquals("Alice", resp.getSenderName());
        assertEquals("Hello", resp.getContent());
        assertEquals(MessageType.TEXT, resp.getType());
        assertNull(resp.getImageUrl());
        assertEquals(sentAt, resp.getSentAt());
        assertNull(resp.getReceiverId());
    }

    @Test
    void toResponse_mapsReceiverIdForPrivateMessage() {
        Message message = new Message();
        UUID id = UUID.randomUUID();
        UUID sender = UUID.randomUUID();
        UUID receiver = UUID.randomUUID();
        LocalDateTime sentAt = LocalDateTime.now();

        message.setId(id);
        message.setSenderId(sender);
        message.setReceiverId(receiver);
        message.setSenderName("Bob");
        message.setContent("Mensaje privado");
        message.setType(MessageType.TEXT);
        message.setSentAt(sentAt);

        MessageResponse resp = mapper.toResponse(message);

        assertEquals(sender, resp.getSenderId());
        assertEquals(receiver, resp.getReceiverId());
        assertEquals("Mensaje privado", resp.getContent());
        assertEquals(MessageType.TEXT, resp.getType());
    }
}

