package com.patricia.chat.rest.controller;

import com.patricia.chat.application.dto.request.SendMessageRequest;
import com.patricia.chat.application.dto.response.MessageResponse;
import com.patricia.chat.application.mapper.MessageMapper;
import com.patricia.chat.domain.model.Message;
import com.patricia.chat.domain.ports.in.GetMessageHistoryUseCase;
import com.patricia.chat.domain.ports.in.SendMessageUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.UUID;

/**
 * REST controller for retrieving chat message histories.
 * Provides paginated access to the messages of a specific Parche group or private chat.
 */
@RestController
@Tag(
        name = "Messages",
        description = """
                Manages retrieval of chat message histories for both group chats (Parches) and \
                private conversations. All endpoints require a valid JWT Bearer token and verify \
                that the requesting user has access to the target conversation: active membership \
                for Parche groups or an established connection (ACCEPTED status) for private chats. \
                Messages are returned in chronological order (oldest first) with pagination support \
                to efficiently load large conversation histories."""
)
@SecurityRequirement(name = "bearerAuth")
public class MessageController {

    private final GetMessageHistoryUseCase getMessageHistoryUseCase;
    private final SendMessageUseCase sendMessageUseCase;
    private final MessageMapper messageMapper;
    private final SimpMessagingTemplate messagingTemplate;

    public MessageController(GetMessageHistoryUseCase getMessageHistoryUseCase,
                             SendMessageUseCase sendMessageUseCase,
                             MessageMapper messageMapper,
                             SimpMessagingTemplate messagingTemplate) {
        this.getMessageHistoryUseCase = getMessageHistoryUseCase;
        this.sendMessageUseCase       = sendMessageUseCase;
        this.messageMapper            = messageMapper;
        this.messagingTemplate        = messagingTemplate;
    }

    @PostMapping("/api/parches/{parcheId}/messages")
    public ResponseEntity<MessageResponse> sendMessage(
            @PathVariable UUID parcheId,
            @Valid @RequestBody SendMessageRequest request,
            Authentication auth) {

        UUID senderId = UUID.fromString(auth.getName());
        String senderName = extractSenderName(auth);

        Message message = sendMessageUseCase.sendMessage(
                parcheId,
                senderId,
                senderName,
                request.getContent(),
                request.getImageUrl()
        );

        MessageResponse response = messageMapper.toResponse(message);
        messagingTemplate.convertAndSend("/topic/parches/" + parcheId, response);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            operationId = "getParcheMessageHistory",
            summary = "Get paginated message history for a Parche group chat",
            description = """
                    Retrieves a paginated list of chat messages for a specific Parche group. \
                    Messages are sorted chronologically from oldest to newest, allowing clients \
                    to load conversation history incrementally.

                    **Access control:** The requesting user must be an active member of the Parche. \
                    Users who have left the Parche or were never members cannot access the message \
                    history.

                    **Pagination:** Default page is 0 (first page) with 20 messages per page. \
                    Maximum page size is 100 messages to prevent performance issues. \
                    Use the `page` and `size` parameters to navigate through the conversation.

                    **Use case:** This endpoint is called when a user opens a Parche chat screen. \
                    The client should first load page 0 to show the most recent messages (since \
                    sorting is ascending, the last message on the page is the newest) and then \
                    load previous pages as the user scrolls up.

                    **Identity resolution:** The user ID is extracted from the JWT `sub` claim \
                    via Spring Security's `Authentication` object."""
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = """
                            Message history retrieved successfully. Returns a paginated response \
                            containing the requested page of messages. The response includes metadata \
                            (total elements, total pages, etc.) for client-side pagination controls. \
                            Returns an empty page if no messages exist in the conversation.""",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Page.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = """
                            Invalid pagination parameters. Common causes: negative page number, \
                            size less than 1, or size greater than maximum allowed (100).""",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = """
                            No valid JWT Bearer token was provided, the token has expired, or the \
                            token signature is invalid. Re-authenticate and retry with a fresh token.""",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = """
                            The authenticated user does not have access to this Parche's message history. \
                            This occurs when the user is not an active member of the specified Parche.""",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = """
                            No Parche exists with the provided `parcheId` UUID. Verify that the ID \
                            is correct and the Parche has not been deleted.""",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = """
                            An unexpected server error occurred while retrieving the message history. \
                            Retry the request; if the problem persists, contact platform support.""",
                    content = @Content(mediaType = "application/json")
            )
    })
    @GetMapping("/api/parches/{parcheId}/messages")
    public ResponseEntity<Page<MessageResponse>> getHistory(
            @Parameter(
                    description = "UUID of the Parche whose message history is being requested",
                    required = true,
                    example = "550e8400-e29b-41d4-a716-446655440000"
            )
            @PathVariable UUID parcheId,

            @Parameter(
                    description = """
                            Page number to retrieve (zero-based index). Page 0 contains the oldest \
                            messages. Use this parameter to navigate through the conversation history.""",
                    example = "0",
                    schema = @Schema(type = "integer", minimum = "0", defaultValue = "0")
            )
            @RequestParam(defaultValue = "0") int page,

            @Parameter(
                    description = """
                            Maximum number of messages to return per page. Valid range: 1 to 100. \
                            Default is 20. Larger values improve efficiency for wide screens but \
                            increase response payload size.""",
                    example = "20",
                    schema = @Schema(type = "integer", minimum = "1", maximum = "100", defaultValue = "20")
            )
            @RequestParam(defaultValue = "20") int size,

            @Parameter(hidden = true) Authentication auth) {

        UUID requesterId = UUID.fromString(auth.getName());
        PageRequest pageable = PageRequest.of(page, size, Sort.by("sentAt").ascending());

        Page<MessageResponse> result = getMessageHistoryUseCase
                .getHistory(parcheId, requesterId, pageable)
                .map(messageMapper::toResponse);

        return ResponseEntity.ok(result);
    }

    @Operation(
            operationId = "getPrivateMessageHistory",
            summary = "Get paginated message history for a private chat",
            description = """
                    Retrieves a paginated list of chat messages from a private conversation between \
                    the authenticated user and a friend. Messages are sorted chronologically from \
                    oldest to newest.

                    **Access control:** The requesting user must have an active connection with the \
                    friend. Both users must have accepted the connection request (status = ACCEPTED). \
                    Users who have blocked each other cannot access message history.

                    **Pagination:** Default page is 0 (first page) with 20 messages per page. \
                    Maximum page size is 100 messages. Use the `page` and `size` parameters to \
                    navigate through the conversation.

                    **Use case:** This endpoint is called when a user opens a private chat screen \
                    with a friend. The client should first load page 0 (oldest messages) and then \
                    load subsequent pages as the user scrolls up.

                    **Identity resolution:** The requester ID is extracted from the JWT `sub` claim. \
                    The friend ID is provided as a path parameter."""
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = """
                            Private message history retrieved successfully. Returns a paginated \
                            response containing the requested page of messages. Returns an empty \
                            page if no messages have been exchanged with this friend yet.""",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Page.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = """
                            Invalid pagination parameters. Common causes: negative page number, \
                            size less than 1, or size greater than maximum allowed (100).""",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = """
                            No valid JWT Bearer token was provided or the token has expired. \
                            Re-authenticate and retry with a fresh token.""",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = """
                            The authenticated user does not have access to this private chat. \
                            This occurs when: (1) there is no active connection between the users, \
                            (2) the connection is still PENDING, or (3) one user has blocked the other.""",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = """
                            No user exists with the provided `friendId` UUID. Verify that the ID \
                            is correct and the user account is active.""",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = """
                            An unexpected server error occurred while retrieving the message history. \
                            Retry the request or contact support if the issue persists.""",
                    content = @Content(mediaType = "application/json")
            )
    })
    @GetMapping("/api/friends/{friendId}/messages")
    public ResponseEntity<Page<MessageResponse>> getPrivateHistory(
            @Parameter(
                    description = "UUID of the friend whose private chat history is being requested",
                    required = true,
                    example = "660e8400-e29b-41d4-a716-446655440001"
            )
            @PathVariable UUID friendId,

            @Parameter(
                    description = """
                            Page number to retrieve (zero-based index). Page 0 contains the oldest \
                            messages. Use this parameter to navigate through the conversation history.""",
                    example = "0",
                    schema = @Schema(type = "integer", minimum = "0", defaultValue = "0")
            )
            @RequestParam(defaultValue = "0") int page,

            @Parameter(
                    description = """
                            Maximum number of messages to return per page. Valid range: 1 to 100. \
                            Default is 20. Larger values improve efficiency for wide screens but \
                            increase response payload size.""",
                    example = "20",
                    schema = @Schema(type = "integer", minimum = "1", maximum = "100", defaultValue = "20")
            )
            @RequestParam(defaultValue = "20") int size,

            @Parameter(hidden = true) Authentication auth) {

        UUID requesterId = UUID.fromString(auth.getName());
        PageRequest pageable = PageRequest.of(page, size, Sort.by("sentAt").ascending());

        Page<MessageResponse> result = getMessageHistoryUseCase
                .getPrivateHistory(requesterId, friendId, pageable)
                .map(messageMapper::toResponse);

        return ResponseEntity.ok(result);
    }

    private String extractSenderName(Authentication auth) {
        if (auth instanceof UsernamePasswordAuthenticationToken token
                && token.getDetails() instanceof String email
                && !email.isBlank()) {
            return email;
        }
        return "Usuario";
    }
}
