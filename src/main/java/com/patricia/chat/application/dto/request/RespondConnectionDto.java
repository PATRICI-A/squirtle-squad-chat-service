package com.patricia.chat.application.dto.request;

import com.patricia.chat.domain.model.ConnectionStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(
        name = "RespondConnection",
        description = """
                Payload for responding to a pending connection request. The recipient can either \
                accept (ACCEPTED) or reject (REJECTED) the request. Once rejected, the request \
                cannot be accepted later — a new request must be sent."""
)
public class RespondConnectionDto {

    @NotNull(message = "La decisión es obligatoria (ACCEPTED o REJECTED)")
    @Schema(
            description = """
                    Decision on the connection request. Valid values:
                    - `ACCEPTED` — Accept the friend request, establishing an active connection
                    - `REJECTED` — Decline the request; the requester may send a new request later""",
            example = "ACCEPTED",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private ConnectionStatus status;

    public RespondConnectionDto() {}

    public ConnectionStatus getStatus()          { return status; }
    public void setStatus(ConnectionStatus s)    { this.status = s; }
}