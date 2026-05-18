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
    private final com.patricia.chat.domain.ports.out.ConnectionRepositoryPort connectionRepository;

    public SendMessageUseCaseImpl(MessageRepositoryPort messageRepository,
                                  ParcheServicePort parcheService,
                                  com.patricia.chat.domain.ports.out.ConnectionRepositoryPort connectionRepository) {
        this.messageRepository = messageRepository;
        this.parcheService     = parcheService;
        this.connectionRepository = connectionRepository;
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

    @Override
    public Message sendPrivateMessage(UUID senderId, String senderName, UUID receiverId,
                                      String content, String imageUrl) {
        // 1. Verificar conexión activa
        if (!connectionRepository.hasActiveConnection(senderId, receiverId)) {
            throw new UnauthorizedChatAccessException("No tienes una conexión activa con el usuario " + receiverId);
        }

        // 2. Determinar tipo de mensaje
        MessageType type = (imageUrl != null && !imageUrl.isBlank())
                ? MessageType.IMAGE
                : MessageType.TEXT;

        // 3. Crear y persistir
        Message message = new Message(senderId, senderName, receiverId, content, type);
        message.setImageUrl(imageUrl);

        return messageRepository.save(message);
    }
}
