package com.patricia.chat.rest.controller;

import com.patricia.chat.application.dto.response.MessageResponse;
import com.patricia.chat.application.mapper.MessageMapper;
import com.patricia.chat.domain.ports.in.GetMessageHistoryUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * REST controller for retrieving chat message histories.
 * Provides paginated access to the messages of a specific Parche group.
 */
@RestController
@Tag(name = "Messages", description = "Endpoints for retrieving chat histories")
public class MessageController {

    private final GetMessageHistoryUseCase getMessageHistoryUseCase;
    private final MessageMapper messageMapper;

    public MessageController(GetMessageHistoryUseCase getMessageHistoryUseCase,
                             MessageMapper messageMapper) {
        this.getMessageHistoryUseCase = getMessageHistoryUseCase;
        this.messageMapper            = messageMapper;
    }

    /**
     * Retrieves the paginated message history of a Parche.
     * The requester must be an active member of the Parche.
     * 
     * @param parcheId the UUID of the Parche
     * @param page the page number to retrieve (0-indexed)
     * @param size the maximum number of messages per page
     * @param auth the current user's authentication
     * @return a paginated response containing the messages sorted chronologically
     */
    @Operation(summary = "Get message history", description = "Retrieves a paginated list of chat messages for a specific Parche.")
    @GetMapping("/api/parches/{parcheId}/messages")
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

    /**
     * Retrieves the paginated message history of a private chat with a friend.
     * The requester must have an active connection with the friend.
     * 
     * @param friendId the UUID of the friend
     * @param page the page number to retrieve (0-indexed)
     * @param size the maximum number of messages per page
     * @param auth the current user's authentication
     * @return a paginated response containing the messages sorted chronologically
     */
    @Operation(summary = "Get private message history", description = "Retrieves a paginated list of chat messages for a private chat with a friend.")
    @GetMapping("/api/friends/{friendId}/messages")
    public ResponseEntity<Page<MessageResponse>> getPrivateHistory(
            @PathVariable UUID friendId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Authentication auth) {

        UUID requesterId = UUID.fromString(auth.getName());
        PageRequest pageable = PageRequest.of(page, size, Sort.by("sentAt").ascending());

        Page<MessageResponse> result = getMessageHistoryUseCase
                .getPrivateHistory(requesterId, friendId, pageable)
                .map(messageMapper::toResponse);

        return ResponseEntity.ok(result);
    }
}
