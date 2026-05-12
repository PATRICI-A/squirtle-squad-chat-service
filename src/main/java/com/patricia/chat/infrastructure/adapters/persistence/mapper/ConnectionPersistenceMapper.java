package com.patricia.chat.infrastructure.adapters.persistence.mapper;

import com.patricia.chat.domain.model.Connection;
import com.patricia.chat.infrastructure.adapters.persistence.entity.ConnectionEntity;
import org.springframework.stereotype.Component;

@Component
public class ConnectionPersistenceMapper {

    public ConnectionEntity toEntity(Connection connection) {
        ConnectionEntity entity = new ConnectionEntity();
        entity.setId(connection.getId());
        entity.setRequesterId(connection.getRequesterId());
        entity.setAddresseeId(connection.getAddresseeId());
        entity.setStatus(connection.getStatus());
        entity.setCreatedAt(connection.getCreatedAt());
        entity.setUpdatedAt(connection.getUpdatedAt());
        return entity;
    }

    public Connection toDomain(ConnectionEntity entity) {
        Connection connection = new Connection();
        connection.setId(entity.getId());
        connection.setRequesterId(entity.getRequesterId());
        connection.setAddresseeId(entity.getAddresseeId());
        connection.setStatus(entity.getStatus());
        connection.setCreatedAt(entity.getCreatedAt());
        connection.setUpdatedAt(entity.getUpdatedAt());
        return connection;
    }
}
