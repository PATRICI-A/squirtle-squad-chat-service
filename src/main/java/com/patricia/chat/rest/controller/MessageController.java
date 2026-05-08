package com.patricia.chat.rest.controller;

import com.patricia.chat.application.dto.response.MessageResponse;
import com.patricia.chat.application.mapper.MessageMapper;
import com.patricia.chat.domain.ports.in.GetMessageHistoryUseCase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Endpoints REST para consultar el historial de mensajes de un parche.
 */
@RestController
@RequestMapping("/api/parches/{parcheId}/messages")
public class MessageController {

    private final GetMessageHistoryUseCase getMessageHistoryUseCase;
    private final MessageMapper messageMapper;

    public MessageController(GetMessageHistoryUseCase getMessageHistoryUseCase,
                             MessageMapper messageMapper) {
        this.getMessageHistoryUseCase = getMessageHistoryUseCase;
        this.messageMapper            = messageMapper;
    }

    /**
     * GET /api/parches/{parcheId}/messages?page=0&size=20
     * Retorna el historial paginado de mensajes del parche.
     * Solo accesible para miembros activos.
     */
    @GetMapping
    public ResponseEntity<Page<MessageResponse>> getHistory(
            @PathVariable UUID parcheId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Authentication auth) {

        UUID requesterId = UUID.fromString(auth.getName());
        PageRequest pageable = PageRequest.of(page, size, Sort.by("sentAt").ascending());

        Page<MessageResponse> result = getMessageHistoryUseCase
                .getHistory(parcheId, requesterId, pageable)
                .map(messageMapper::toResponse);

        return ResponseEntity.ok(result);
    }
}
