package com.patricia.chat.application.usecase;

import com.patricia.chat.domain.exceptions.UnauthorizedChatAccessException;
import com.patricia.chat.domain.model.Message;
import com.patricia.chat.domain.model.MessageType;
import com.patricia.chat.domain.ports.in.SendMessageUseCase;
import com.patricia.chat.domain.ports.out.MessageRepositoryPort;
import com.patricia.chat.domain.ports.out.ParcheServicePort;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class SendMessageUseCaseImpl implements SendMessageUseCase {

    private final MessageRepositoryPort messageRepository;
    private final ParcheServicePort parcheService;

    public SendMessageUseCaseImpl(MessageRepositoryPort messageRepository,
                                  ParcheServicePort parcheService) {
        this.messageRepository = messageRepository;
        this.parcheService     = parcheService;
    }

    @Override
    public Message sendMessage(UUID parcheId, UUID senderId, String senderName,
                               String content, String imageUrl) {

        // 1. Verificar membresía en el parche
        if (!parcheService.isMember(parcheId, senderId)) {
            throw new UnauthorizedChatAccessException(senderId.toString(), parcheId.toString());
        }

        // 2. Determinar tipo de mensaje
        MessageType type = (imageUrl != null && !imageUrl.isBlank())
                ? MessageType.IMAGE
                : MessageType.TEXT;

        // 3. Crear y persistir
        Message message = new Message(parcheId, senderId, senderName, content, type);
        message.setImageUrl(imageUrl);

        return messageRepository.save(message);
    }
}
