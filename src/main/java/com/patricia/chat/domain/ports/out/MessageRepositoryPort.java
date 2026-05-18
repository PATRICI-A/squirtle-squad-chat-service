package com.patricia.chat.domain.ports.out;

import com.patricia.chat.domain.model.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.UUID;

public interface MessageRepositoryPort {
    Message save(Message message);
    Page<Message> findByParcheId(UUID parcheId, Pageable pageable);
    Page<Message> findPrivateMessages(UUID user1, UUID user2, Pageable pageable);
}
