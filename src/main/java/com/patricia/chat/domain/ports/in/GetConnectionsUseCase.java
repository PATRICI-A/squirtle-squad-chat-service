package com.patricia.chat.domain.ports.in;

import com.patricia.chat.domain.model.Connection;
import java.util.List;
import java.util.UUID;

public interface GetConnectionsUseCase {
    List<Connection> getConnections(UUID userId);
    List<Connection> getPendingRequests(UUID userId);
}
