package com.patricia.chat.domain.ports.in;

import com.patricia.chat.domain.model.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.UUID;

public interface GetMessageHistoryUseCase {
    /**
     * Retorna el historial paginado de mensajes de un parche.
     * Solo accesible para miembros del parche.
     */
    Page<Message> getHistory(UUID parcheId, UUID requesterId, Pageable pageable);

    /**
     * Retorna el historial paginado de mensajes de un chat privado.
     * Solo accesible si hay conexión activa.
     */
    Page<Message> getPrivateHistory(UUID requesterId, UUID friendId, Pageable pageable);
}
