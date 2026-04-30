package com.patricia.chat.domain.ports.in;

import com.patricia.chat.domain.model.Connection;
import com.patricia.chat.domain.model.ConnectionStatus;
import java.util.UUID;

public interface RespondConnectionRequestUseCase {
    Connection respond(UUID connectionId, UUID addresseeId, ConnectionStatus decision);
}
