package com.patricia.chat.domain.ports.in;

import com.patricia.chat.domain.model.Message;
import java.util.UUID;

public interface SendMessageUseCase {
    /**
     * Valida que el remitente sea miembro del parche,
     * persiste el mensaje y lo retorna listo para broadcast.
     */
    Message sendMessage(UUID parcheId, UUID senderId, String senderName,
                        String content, String imageUrl);

    Message sendPrivateMessage(UUID senderId, String senderName, UUID receiverId,
                               String content, String imageUrl);
}
