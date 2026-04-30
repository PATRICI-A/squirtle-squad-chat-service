package com.patricia.chat.application.dto.request;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public class ConnectionRequestDto {

    @NotNull(message = "El ID del destinatario es obligatorio")
    private UUID addresseeId;

    public ConnectionRequestDto() {}

    public UUID getAddresseeId()              { return addresseeId; }
    public void setAddresseeId(UUID id)       { this.addresseeId = id; }
}
