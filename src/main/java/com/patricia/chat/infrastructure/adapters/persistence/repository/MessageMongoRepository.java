package com.patricia.chat.infrastructure.adapters.persistence.repository;

import com.patricia.chat.infrastructure.adapters.persistence.entity.MessageEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.UUID;

public interface MessageMongoRepository extends MongoRepository<MessageEntity, UUID> {
    Page<MessageEntity> findByParcheIdOrderBySentAtAsc(UUID parcheId, Pageable pageable);
}