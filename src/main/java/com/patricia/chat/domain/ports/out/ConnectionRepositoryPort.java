package com.patricia.chat.domain.ports.out;

import com.patricia.chat.domain.model.Connection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ConnectionRepositoryPort {
    Connection save(Connection connection);
    Optional<Connection> findById(UUID id);
    boolean existsBetween(UUID userA, UUID userB);
    List<Connection> findByUserId(UUID userId);
    List<Connection> findPendingByAddresseeId(UUID addresseeId);
}
