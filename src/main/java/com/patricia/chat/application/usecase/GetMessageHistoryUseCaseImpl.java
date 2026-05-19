package com.patricia.chat.application.usecase;

import com.patricia.chat.domain.exceptions.UnauthorizedChatAccessException;
import com.patricia.chat.domain.model.Message;
import com.patricia.chat.domain.ports.in.GetMessageHistoryUseCase;
import com.patricia.chat.domain.ports.out.MessageRepositoryPort;
import com.patricia.chat.domain.ports.out.ParcheServicePort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class GetMessageHistoryUseCaseImpl implements GetMessageHistoryUseCase {

    private final MessageRepositoryPort messageRepository;
    private final ParcheServicePort parcheService;
    private final com.patricia.chat.domain.ports.out.ConnectionRepositoryPort connectionRepository;

    public GetMessageHistoryUseCaseImpl(MessageRepositoryPort messageRepository,
                                        ParcheServicePort parcheService,
                                        com.patricia.chat.domain.ports.out.ConnectionRepositoryPort connectionRepository) {
        this.messageRepository = messageRepository;
        this.parcheService     = parcheService;
        this.connectionRepository = connectionRepository;
    }

    @Override
    public Page<Message> getHistory(UUID parcheId, UUID requesterId, Pageable pageable) {
        if (!parcheService.isMember(parcheId, requesterId)) {
            throw new UnauthorizedChatAccessException(requesterId.toString(), parcheId.toString());
        }
        return messageRepository.findByParcheId(parcheId, pageable);
    }

    @Override
    public Page<Message> getPrivateHistory(UUID requesterId, UUID friendId, Pageable pageable) {
        if (!connectionRepository.hasActiveConnection(requesterId, friendId)) {
            throw new UnauthorizedChatAccessException("No tienes una conexión activa con el usuario " + friendId);
        }
        return messageRepository.findPrivateMessages(requesterId, friendId, pageable);
    }
}
