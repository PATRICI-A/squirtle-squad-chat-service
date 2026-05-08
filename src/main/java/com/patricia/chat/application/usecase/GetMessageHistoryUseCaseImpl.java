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

    public GetMessageHistoryUseCaseImpl(MessageRepositoryPort messageRepository,
                                        ParcheServicePort parcheService) {
        this.messageRepository = messageRepository;
        this.parcheService     = parcheService;
    }

    @Override
    public Page<Message> getHistory(UUID parcheId, UUID requesterId, Pageable pageable) {
        if (!parcheService.isMember(parcheId, requesterId)) {
            throw new UnauthorizedChatAccessException(requesterId.toString(), parcheId.toString());
        }
        return messageRepository.findByParcheId(parcheId, pageable);
    }
}
