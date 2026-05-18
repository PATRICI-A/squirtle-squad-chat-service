package com.patricia.chat.infrastructure.adapters.persistence.repository;

import com.patricia.chat.infrastructure.adapters.persistence.entity.MessageEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.UUID;

public interface MessageMongoRepository extends MongoRepository<MessageEntity, UUID> {
    Page<MessageEntity> findByParcheIdOrderBySentAtAsc(UUID parcheId, Pageable pageable);

    @org.springframework.data.mongodb.repository.Query("{ '$or': [ { 'sender_id': ?0, 'receiver_id': ?1 }, { 'sender_id': ?1, 'receiver_id': ?0 } ] }")
    Page<MessageEntity> findPrivateChatHistory(UUID userId1, UUID userId2, Pageable pageable);
}