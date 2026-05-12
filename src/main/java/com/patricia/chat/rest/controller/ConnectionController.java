package com.patricia.chat.rest.controller;

import com.patricia.chat.application.dto.request.ConnectionRequestDto;
import com.patricia.chat.application.dto.request.RespondConnectionDto;
import com.patricia.chat.application.dto.response.ConnectionResponse;
import com.patricia.chat.application.mapper.ConnectionMapper;
import com.patricia.chat.domain.ports.in.GetConnectionsUseCase;
import com.patricia.chat.domain.ports.in.RespondConnectionRequestUseCase;
import com.patricia.chat.domain.ports.in.SendConnectionRequestUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Endpoints REST para gestión de conexiones entre estudiantes (RF07).
 */
@RestController
@RequestMapping("/api/connections")
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

    /** POST /api/connections/request — Enviar solicitud de conexión */
    @PostMapping("/request")
    public ResponseEntity<ConnectionResponse> sendRequest(
            @Valid @RequestBody ConnectionRequestDto dto,
            Authentication auth) {

        UUID requesterId = UUID.fromString(auth.getName());
        ConnectionResponse response = connectionMapper.toResponse(
                sendConnectionRequestUseCase.sendRequest(requesterId, dto.getAddresseeId()));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /** PATCH /api/connections/{connectionId} — Aceptar o rechazar solicitud */
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

    /** GET /api/connections — Listar conexiones activas del usuario */
    @GetMapping
    public ResponseEntity<List<ConnectionResponse>> getConnections(Authentication auth) {
        UUID userId = UUID.fromString(auth.getName());
        List<ConnectionResponse> connections = getConnectionsUseCase.getConnections(userId)
                .stream().map(connectionMapper::toResponse).toList();
        return ResponseEntity.ok(connections);
    }

    /** GET /api/connections/pending — Ver solicitudes pendientes recibidas */
    @GetMapping("/pending")
    public ResponseEntity<List<ConnectionResponse>> getPending(Authentication auth) {
        UUID userId = UUID.fromString(auth.getName());
        List<ConnectionResponse> pending = getConnectionsUseCase.getPendingRequests(userId)
                .stream().map(connectionMapper::toResponse).toList();
        return ResponseEntity.ok(pending);
    }
}
