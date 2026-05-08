package com.patricia.chat;

import com.patricia.chat.domain.model.Message;
import com.patricia.chat.domain.model.MessageType;
import com.patricia.chat.infrastructure.adapters.persistence.entity.MessageEntity;
import com.patricia.chat.infrastructure.adapters.persistence.mapper.MessagePersistenceMapper;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class MessagePersistenceMapperTest {

    @Test
    void toEntity_and_toDomain_roundtrip() {
        Message message = new Message();
        UUID id = UUID.randomUUID();
        UUID parche = UUID.randomUUID();
        UUID sender = UUID.randomUUID();
        LocalDateTime sentAt = LocalDateTime.now();

        message.setId(id);
        message.setParcheId(parche);
        message.setSenderId(sender);
        message.setSenderName("Bob");
        message.setContent("Hi there");
        message.setType(MessageType.IMAGE);
        message.setImageUrl("http://img");
        message.setSentAt(sentAt);

        MessagePersistenceMapper mapper = new MessagePersistenceMapper();
        MessageEntity entity = mapper.toEntity(message);

        assertEquals(id, entity.getId());
        assertEquals(parche, entity.getParcheId());
        assertEquals(sender, entity.getSenderId());
        assertEquals("Bob", entity.getSenderName());
        assertEquals("Hi there", entity.getContent());
        assertEquals(MessageType.IMAGE, entity.getType());
        assertEquals("http://img", entity.getImageUrl());
        assertEquals(sentAt, entity.getSentAt());

        Message round = mapper.toDomain(entity);
        assertEquals(entity.getId(), round.getId());
        assertEquals(entity.getParcheId(), round.getParcheId());
        assertEquals(entity.getSenderId(), round.getSenderId());
        assertEquals(entity.getSenderName(), round.getSenderName());
        assertEquals(entity.getContent(), round.getContent());
        assertEquals(entity.getType(), round.getType());
        assertEquals(entity.getImageUrl(), round.getImageUrl());
        assertEquals(entity.getSentAt(), round.getSentAt());
    }
}
