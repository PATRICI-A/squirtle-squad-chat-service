package com.patricia.chat.application.usecase;

import com.patricia.chat.domain.model.Connection;
import com.patricia.chat.domain.ports.in.GetConnectionsUseCase;
import com.patricia.chat.domain.ports.out.ConnectionRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class GetConnectionsUseCaseImpl implements GetConnectionsUseCase {

    private final ConnectionRepositoryPort connectionRepository;

    public GetConnectionsUseCaseImpl(ConnectionRepositoryPort connectionRepository) {
        this.connectionRepository = connectionRepository;
    }

    @Override
    public List<Connection> getConnections(UUID userId) {
        return connectionRepository.findByUserId(userId);
    }

    @Override
    public List<Connection> getPendingRequests(UUID userId) {
        return connectionRepository.findPendingByAddresseeId(userId);
    }
}
