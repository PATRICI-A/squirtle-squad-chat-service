package com.patricia.chat.infrastructure.adapters.persistence.entity;

import com.patricia.chat.domain.model.ConnectionStatus;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.index.Indexed;

import java.time.LocalDateTime;
import java.util.UUID;

@Document(collection = "connections")
public class ConnectionEntity {

    @Id
    private UUID id;

    @Indexed
    @Field("requester_id")
    private UUID requesterId;

    @Indexed
    @Field("addressee_id")
    private UUID addresseeId;

    private ConnectionStatus status;

    @Field("created_at")
    private LocalDateTime createdAt;

    @Field("updated_at")
    private LocalDateTime updatedAt;

    // Getters y setters
    public UUID getId()                             { return id; }
    public void setId(UUID id)                      { this.id = id; }

    public UUID getRequesterId()                    { return requesterId; }
    public void setRequesterId(UUID id)             { this.requesterId = id; }

    public UUID getAddresseeId()                    { return addresseeId; }
    public void setAddresseeId(UUID id)             { this.addresseeId = id; }

    public ConnectionStatus getStatus()             { return status; }
    public void setStatus(ConnectionStatus status)  { this.status = status; }

    public LocalDateTime getCreatedAt()             { return createdAt; }
    public void setCreatedAt(LocalDateTime t)       { this.createdAt = t; }

    public LocalDateTime getUpdatedAt()             { return updatedAt; }
    public void setUpdatedAt(LocalDateTime t)       { this.updatedAt = t; }
}