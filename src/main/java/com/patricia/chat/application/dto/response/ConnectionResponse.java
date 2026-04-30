package com.patricia.chat.application.dto.response;

import com.patricia.chat.domain.model.ConnectionStatus;
import java.time.LocalDateTime;
import java.util.UUID;

public class ConnectionResponse {

    private UUID id;
    private UUID requesterId;
    private UUID addresseeId;
    private ConnectionStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ConnectionResponse() {}

    // Getters y setters
    public UUID getId()                            { return id; }
    public void setId(UUID id)                     { this.id = id; }

    public UUID getRequesterId()                   { return requesterId; }
    public void setRequesterId(UUID id)            { this.requesterId = id; }

    public UUID getAddresseeId()                   { return addresseeId; }
    public void setAddresseeId(UUID id)            { this.addresseeId = id; }

    public ConnectionStatus getStatus()            { return status; }
    public void setStatus(ConnectionStatus s)      { this.status = s; }

    public LocalDateTime getCreatedAt()            { return createdAt; }
    public void setCreatedAt(LocalDateTime t)      { this.createdAt = t; }

    public LocalDateTime getUpdatedAt()            { return updatedAt; }
    public void setUpdatedAt(LocalDateTime t)      { this.updatedAt = t; }
}
