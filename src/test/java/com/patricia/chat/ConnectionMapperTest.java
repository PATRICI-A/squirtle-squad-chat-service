package com.patricia.chat;

import com.patricia.chat.application.dto.response.ConnectionResponse;
import com.patricia.chat.application.mapper.ConnectionMapper;
import com.patricia.chat.domain.model.Connection;
import com.patricia.chat.domain.model.ConnectionStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class ConnectionMapperTest {

    @Test
    void toResponse_mapsAllFields() {
        Connection connection = new Connection();
        UUID id = UUID.randomUUID();
        UUID requester = UUID.randomUUID();
        UUID addressee = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        connection.setId(id);
        connection.setRequesterId(requester);
        connection.setAddresseeId(addressee);
        connection.setStatus(ConnectionStatus.ACCEPTED);
        connection.setCreatedAt(now);
        connection.setUpdatedAt(now.plusMinutes(1));

        ConnectionMapper mapper = new ConnectionMapper();
        ConnectionResponse resp = mapper.toResponse(connection);

        assertEquals(id, resp.getId());
        assertEquals(requester, resp.getRequesterId());
        assertEquals(addressee, resp.getAddresseeId());
        assertEquals(ConnectionStatus.ACCEPTED, resp.getStatus());
        assertEquals(now, resp.getCreatedAt());
        assertEquals(now.plusMinutes(1), resp.getUpdatedAt());
    }
}
