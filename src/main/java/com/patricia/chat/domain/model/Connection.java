package com.patricia.chat.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entidad de dominio que representa una solicitud / conexión de amistad
 * entre dos estudiantes (RF07).
 */
public class Connection {

    private UUID id;
    private UUID requesterId;   // quien envía la solicitud
    private UUID addresseeId;   // quien la recibe
    private ConnectionStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Connection() {}

    public Connection(UUID requesterId, UUID addresseeId) {
        this.id          = UUID.randomUUID();
        this.requesterId = requesterId;
        this.addresseeId = addresseeId;
        this.status      = ConnectionStatus.PENDING;
        this.createdAt   = LocalDateTime.now();
        this.updatedAt   = LocalDateTime.now();
    }

    public void accept() {
        this.status    = ConnectionStatus.ACCEPTED;
        this.updatedAt = LocalDateTime.now();
    }

    public void reject() {
        this.status    = ConnectionStatus.REJECTED;
        this.updatedAt = LocalDateTime.now();
    }

    // Getters y setters
    public UUID getId()                          { return id; }
    public void setId(UUID id)                   { this.id = id; }

    public UUID getRequesterId()                 { return requesterId; }
    public void setRequesterId(UUID requesterId) { this.requesterId = requesterId; }

    public UUID getAddresseeId()                 { return addresseeId; }
    public void setAddresseeId(UUID addresseeId) { this.addresseeId = addresseeId; }

    public ConnectionStatus getStatus()              { return status; }
    public void setStatus(ConnectionStatus status)   { this.status = status; }

    public LocalDateTime getCreatedAt()              { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt){ this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt()              { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt){ this.updatedAt = updatedAt; }
}
