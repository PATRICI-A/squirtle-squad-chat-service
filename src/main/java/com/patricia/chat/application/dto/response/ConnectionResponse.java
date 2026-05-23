package com.patricia.chat.application.dto.response;

import com.patricia.chat.domain.model.ConnectionStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.UUID;

@Schema(
        name = "ConnectionResponse",
        description = """
                Represents a connection (friend relationship) between two users. Contains the \
                connection ID, both participants' IDs, the current status, and timestamps for \
                creation and last update. Used in responses for sending requests, responding to \
                requests, and listing active/pending connections."""
)
public class ConnectionResponse {

    @Schema(
            description = "Unique identifier of the connection",
            example = "550e8400-e29b-41d4-a716-446655440000",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private UUID id;

    @Schema(
            description = "UUID of the user who initiated the connection request",
            example = "660e8400-e29b-41d4-a716-446655440001",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private UUID requesterId;

    @Schema(
            description = "UUID of the user who received the connection request",
            example = "770e8400-e29b-41d4-a716-446655440002",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private UUID addresseeId;

    @Schema(
            description = """
                    Current state of the connection:
                    - `PENDING` — Request sent, awaiting response
                    - `ACCEPTED` — Request accepted, active connection
                    - `REJECTED` — Request declined (terminal state)
                    - `BLOCKED` — User blocked (terminal state)""",
            example = "ACCEPTED",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private ConnectionStatus status;

    @Schema(
            description = "ISO-8601 timestamp of when the connection request was created",
            example = "2025-06-15T10:30:00.000Z",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private LocalDateTime createdAt;

    @Schema(
            description = "ISO-8601 timestamp of the last update (e.g., when ACCEPTED or REJECTED)",
            example = "2025-06-15T14:20:00.000Z",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
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