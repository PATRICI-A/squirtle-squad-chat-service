package com.patricia.chat.rest.controller;

import com.patricia.chat.application.dto.request.ConnectionRequestDto;
import com.patricia.chat.application.dto.request.RespondConnectionDto;
import com.patricia.chat.application.dto.response.ConnectionResponse;
import com.patricia.chat.application.mapper.ConnectionMapper;
import com.patricia.chat.domain.ports.in.GetConnectionsUseCase;
import com.patricia.chat.domain.ports.in.RespondConnectionRequestUseCase;
import com.patricia.chat.domain.ports.in.SendConnectionRequestUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST controller for managing student connections (Friend Requests).
 * Provides endpoints for sending, accepting, rejecting and listing connections.
 */
@RestController
@RequestMapping("/api/connections")
@Tag(name = "Connections", description = "Endpoints for managing user connections and friend requests")
public class ConnectionController {

    private final SendConnectionRequestUseCase sendConnectionRequestUseCase;
    private final RespondConnectionRequestUseCase respondConnectionRequestUseCase;
    private final GetConnectionsUseCase getConnectionsUseCase;
    private final ConnectionMapper connectionMapper;

    public ConnectionController(SendConnectionRequestUseCase sendConnectionRequestUseCase,
                                RespondConnectionRequestUseCase respondConnectionRequestUseCase,
                                GetConnectionsUseCase getConnectionsUseCase,
                                ConnectionMapper connectionMapper) {
        this.sendConnectionRequestUseCase    = sendConnectionRequestUseCase;
        this.respondConnectionRequestUseCase = respondConnectionRequestUseCase;
        this.getConnectionsUseCase           = getConnectionsUseCase;
        this.connectionMapper                = connectionMapper;
    }

    /**
     * Sends a new connection request to another user.
     * 
     * @param dto the request payload containing the addressee's UUID
     * @param auth the current user's authentication
     * @return the created connection response in PENDING state
     */
    @Operation(summary = "Send connection request", description = "Sends a new friend request to a specified user.")
    @PostMapping("/request")
    public ResponseEntity<ConnectionResponse> sendRequest(
            @Valid @RequestBody ConnectionRequestDto dto,
            Authentication auth) {

        UUID requesterId = UUID.fromString(auth.getName());
        ConnectionResponse response = connectionMapper.toResponse(
                sendConnectionRequestUseCase.sendRequest(requesterId, dto.getAddresseeId()));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Responds to an existing connection request.
     * 
     * @param connectionId the ID of the connection to respond to
     * @param dto the response payload with ACCEPTED or REJECTED status
     * @param auth the current user's authentication
     * @return the updated connection response
     */
    @Operation(summary = "Respond to connection", description = "Accepts or rejects a pending connection request.")
    @PatchMapping("/{connectionId}")
    public ResponseEntity<ConnectionResponse> respond(
            @PathVariable UUID connectionId,
            @Valid @RequestBody RespondConnectionDto dto,
            Authentication auth) {

        UUID addresseeId = UUID.fromString(auth.getName());
        ConnectionResponse response = connectionMapper.toResponse(
                respondConnectionRequestUseCase.respond(connectionId, addresseeId, dto.getStatus()));
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves all active (ACCEPTED) connections for the authenticated user.
     * 
     * @param auth the current user's authentication
     * @return a list of active connections
     */
    @Operation(summary = "Get active connections", description = "Lists all accepted friend connections for the current user.")
    @GetMapping
    public ResponseEntity<List<ConnectionResponse>> getConnections(Authentication auth) {
        UUID userId = UUID.fromString(auth.getName());
        List<ConnectionResponse> connections = getConnectionsUseCase.getConnections(userId)
                .stream().map(connectionMapper::toResponse).toList();
        return ResponseEntity.ok(connections);
    }

    /**
     * Retrieves all pending connection requests received by the authenticated user.
     * 
     * @param auth the current user's authentication
     * @return a list of pending connection requests
     */
    @Operation(summary = "Get pending connections", description = "Lists all friend requests awaiting the user's response.")
    @GetMapping("/pending")
    public ResponseEntity<List<ConnectionResponse>> getPending(Authentication auth) {
        UUID userId = UUID.fromString(auth.getName());
        List<ConnectionResponse> pending = getConnectionsUseCase.getPendingRequests(userId)
                .stream().map(connectionMapper::toResponse).toList();
        return ResponseEntity.ok(pending);
    }
}
