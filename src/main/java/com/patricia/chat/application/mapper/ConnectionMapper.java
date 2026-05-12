package com.patricia.chat.application.mapper;

import com.patricia.chat.application.dto.response.ConnectionResponse;
import com.patricia.chat.domain.model.Connection;
import org.springframework.stereotype.Component;

@Component
public class ConnectionMapper {

    public ConnectionResponse toResponse(Connection connection) {
        ConnectionResponse response = new ConnectionResponse();
        response.setId(connection.getId());
        response.setRequesterId(connection.getRequesterId());
        response.setAddresseeId(connection.getAddresseeId());
        response.setStatus(connection.getStatus());
        response.setCreatedAt(connection.getCreatedAt());
        response.setUpdatedAt(connection.getUpdatedAt());
        return response;
    }
}
