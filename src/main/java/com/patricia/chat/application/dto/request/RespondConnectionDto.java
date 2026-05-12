package com.patricia.chat.application.dto.request;

import com.patricia.chat.domain.model.ConnectionStatus;
import jakarta.validation.constraints.NotNull;

public class RespondConnectionDto {

    @NotNull(message = "La decisión es obligatoria (ACCEPTED o REJECTED)")
    private ConnectionStatus status;

    public RespondConnectionDto() {}

    public ConnectionStatus getStatus()          { return status; }
    public void setStatus(ConnectionStatus s)    { this.status = s; }
}
