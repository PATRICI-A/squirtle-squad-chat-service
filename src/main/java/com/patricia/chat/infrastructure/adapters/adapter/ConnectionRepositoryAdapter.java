package com.patricia.chat.infrastructure.adapters.adapter;

import com.patricia.chat.domain.model.Connection;
import com.patricia.chat.domain.model.ConnectionStatus;
import com.patricia.chat.domain.ports.out.ConnectionRepositoryPort;
import com.patricia.chat.infrastructure.adapters.persistence.mapper.ConnectionPersistenceMapper;
import com.patricia.chat.infrastructure.adapters.persistence.repository.ConnectionMongoRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class ConnectionRepositoryAdapter implements ConnectionRepositoryPort {

    private final ConnectionMongoRepository mongoRepository;
    private final ConnectionPersistenceMapper mapper;

    public ConnectionRepositoryAdapter(ConnectionMongoRepository mongoRepository,
                                       ConnectionPersistenceMapper mapper) {
        this.mongoRepository = mongoRepository;
        this.mapper          = mapper;
    }

    @Override
    public Connection save(Connection connection) {
        return mapper.toDomain(mongoRepository.save(mapper.toEntity(connection)));
    }

    @Override
    public Optional<Connection> findById(UUID id) {
        return mongoRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public boolean existsBetween(UUID userA, UUID userB) {
        return !mongoRepository.findBetween(userA, userB).isEmpty();
    }

    @Override
    public boolean hasActiveConnection(UUID userA, UUID userB) {
        return !mongoRepository.findAcceptedBetween(userA, userB).isEmpty();
    }

    @Override
    public List<Connection> findByUserId(UUID userId) {
        return mongoRepository.findAcceptedByUserId(userId)
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<Connection> findPendingByAddresseeId(UUID addresseeId) {
        return mongoRepository
                .findByAddresseeIdAndStatus(addresseeId, ConnectionStatus.PENDING)
                .stream().map(mapper::toDomain).toList();
    }
}