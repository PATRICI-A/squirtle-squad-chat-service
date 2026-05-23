package com.patricia.chat.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

@Schema(
        name = "ConnectionRequest",
        description = """
                Payload for sending a new connection request (friend request) to another user. \
                Contains only the UUID of the recipient. The requester ID is extracted from the \
                JWT token and is not required in the request body."""
)
public class ConnectionRequestDto {

    @NotNull(message = "El ID del destinatario es obligatorio")
    @Schema(
            description = "UUID of the user to whom the connection request should be sent",
            example = "550e8400-e29b-41d4-a716-446655440000",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private UUID addresseeId;

    public ConnectionRequestDto() {}

    public UUID getAddresseeId()              { return addresseeId; }
    public void setAddresseeId(UUID id)       { this.addresseeId = id; }
}