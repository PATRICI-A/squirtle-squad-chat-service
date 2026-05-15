package com.patricia.chat.infrastructure.adapters.persistence.mapper;

import com.patricia.chat.domain.model.Connection;
import com.patricia.chat.domain.model.ConnectionStatus;
import com.patricia.chat.infrastructure.adapters.persistence.entity.ConnectionEntity;
import com.patricia.chat.infrastructure.adapters.persistence.mapper.ConnectionPersistenceMapper;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class ConnectionPersistenceMapperTest {

    @Test
    void toEntity_and_toDomain_roundtrip() {
        Connection c = new Connection();
        UUID id = UUID.randomUUID();
        UUID req = UUID.randomUUID();
        UUID add = UUID.randomUUID();
        LocalDateTime created = LocalDateTime.now().minusDays(1);
        LocalDateTime updated = LocalDateTime.now();

        c.setId(id);
        c.setRequesterId(req);
        c.setAddresseeId(add);
        c.setStatus(ConnectionStatus.ACCEPTED);
        c.setCreatedAt(created);
        c.setUpdatedAt(updated);

        ConnectionPersistenceMapper mapper = new ConnectionPersistenceMapper();
        ConnectionEntity e = mapper.toEntity(c);

        assertEquals(id, e.getId());
        assertEquals(req, e.getRequesterId());
        assertEquals(add, e.getAddresseeId());
        assertEquals(ConnectionStatus.ACCEPTED, e.getStatus());
        assertEquals(created, e.getCreatedAt());
        assertEquals(updated, e.getUpdatedAt());

        Connection round = mapper.toDomain(e);
        assertEquals(e.getId(), round.getId());
        assertEquals(e.getRequesterId(), round.getRequesterId());
        assertEquals(e.getAddresseeId(), round.getAddresseeId());
        assertEquals(e.getStatus(), round.getStatus());
        assertEquals(e.getCreatedAt(), round.getCreatedAt());
        assertEquals(e.getUpdatedAt(), round.getUpdatedAt());
    }
}
