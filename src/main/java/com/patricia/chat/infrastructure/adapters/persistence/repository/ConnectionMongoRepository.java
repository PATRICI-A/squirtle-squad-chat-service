package com.patricia.chat.infrastructure.adapters.persistence.repository;

import com.patricia.chat.domain.model.ConnectionStatus;
import com.patricia.chat.infrastructure.adapters.persistence.entity.ConnectionEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.UUID;

public interface ConnectionMongoRepository extends MongoRepository<ConnectionEntity, UUID> {

    @Query("{ '$or': [ { 'requester_id': ?0, 'addressee_id': ?1 }, { 'requester_id': ?1, 'addressee_id': ?0 } ] }")
    List<ConnectionEntity> findBetween(UUID userA, UUID userB);

    @Query("{ '$or': [ { 'requester_id': ?0 }, { 'addressee_id': ?0 } ], 'status': 'ACCEPTED' }")
    List<ConnectionEntity> findAcceptedByUserId(UUID userId);

    List<ConnectionEntity> findByAddresseeIdAndStatus(UUID addresseeId, ConnectionStatus status);
}