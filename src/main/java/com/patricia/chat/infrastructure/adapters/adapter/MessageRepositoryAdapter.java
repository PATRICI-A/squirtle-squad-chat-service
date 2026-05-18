package com.patricia.chat.infrastructure.adapters.adapter;

import com.patricia.chat.domain.model.Message;
import com.patricia.chat.domain.ports.out.MessageRepositoryPort;
import com.patricia.chat.infrastructure.adapters.persistence.mapper.MessagePersistenceMapper;
import com.patricia.chat.infrastructure.adapters.persistence.repository.MessageMongoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class MessageRepositoryAdapter implements MessageRepositoryPort {

    private final MessageMongoRepository mongoRepository;
    private final MessagePersistenceMapper mapper;

    public MessageRepositoryAdapter(MessageMongoRepository mongoRepository,
                                    MessagePersistenceMapper mapper) {
        this.mongoRepository = mongoRepository;
        this.mapper          = mapper;
    }

    @Override
    public Message save(Message message) {
        return mapper.toDomain(mongoRepository.save(mapper.toEntity(message)));
    }

    @Override
    public Page<Message> findByParcheId(UUID parcheId, Pageable pageable) {
        return mongoRepository
                .findByParcheIdOrderBySentAtAsc(parcheId, pageable)
                .map(mapper::toDomain);
    }

    @Override
    public Page<Message> findPrivateMessages(UUID user1, UUID user2, Pageable pageable) {
        return mongoRepository
                .findPrivateChatHistory(user1, user2, pageable)
                .map(mapper::toDomain);
    }
}