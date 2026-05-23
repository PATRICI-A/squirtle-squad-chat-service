package com.patricia.chat.rest.controller;

import com.patricia.chat.application.dto.request.ConnectionRequestDto;
import com.patricia.chat.application.dto.request.RespondConnectionDto;
import com.patricia.chat.application.dto.response.ConnectionResponse;
import com.patricia.chat.application.mapper.ConnectionMapper;
import com.patricia.chat.domain.ports.in.GetConnectionsUseCase;
import com.patricia.chat.domain.ports.in.RespondConnectionRequestUseCase;
import com.patricia.chat.domain.ports.in.SendConnectionRequestUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
@Tag(
        name = "Connections",
        description = """
                Manages user-to-user connections (friend requests) on the PATRICI.A platform. \
                Supports the complete friend request lifecycle: sending requests, accepting/rejecting \
                pending requests, and listing active connections or pending incoming requests. \
                All endpoints require a valid JWT Bearer token and resolve the authenticated \
                user's identity from the token's `sub` claim.

                **Connection states:**
                - `PENDING` — Request sent, waiting for recipient's response
                - `ACCEPTED` — Recipient accepted, connection is active for chat and social features
                - `REJECTED` — Recipient declined the request (terminal state)
                - `BLOCKED` — One user blocked the other (terminal state)

                **Idempotency:** Sending a duplicate connection request to the same user returns \
                the existing `PENDING` request instead of creating a duplicate."""
)
@SecurityRequirement(name = "bearerAuth")
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

    @Operation(
            operationId = "sendConnectionRequest",
            summary = "Send a new connection request (friend request)",
            description = """
                    Sends a new connection request from the authenticated user to another platform user. \
                    The recipient will receive a notification and can accept or reject the request via \
                    the PATCH /{connectionId} endpoint.

                    **Validation rules:**
                    - Users cannot send a request to themselves
                    - Users cannot send a request if an existing connection already exists in any state \
                      (PENDING, ACCEPTED, REJECTED, BLOCKED)
                    - If a `PENDING` request already exists, the endpoint returns the existing request \
                      instead of creating a duplicate (idempotent behaviour)

                    **Result:** The created connection is initially in `PENDING` state. The response \
                    includes the `connectionId` which the recipient will use to respond.

                    **Post-condition:** A notification event is published to the message exchange \
                    for the notification service to deliver a real-time alert to the recipient.

                    **Identity resolution:** The requester ID is extracted from the JWT `sub` claim."""
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = """
                            Connection request created successfully. The response body contains the \
                            connection details in `PENDING` state. The recipient can now accept or \
                            reject the request using the returned `connectionId`.""",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ConnectionResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = """
                            Invalid request payload. Common causes: the `addresseeId` field is missing, \
                            the UUID format is invalid, or the user is trying to send a request to themselves.""",
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
                            The authenticated user does not have permission to send connection requests. \
                            This may occur if the user account is deactivated or has restricted privileges.""",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = """
                            No user exists with the provided `addresseeId`. Verify that the UUID \
                            is correct and the target user account is active.""",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = """
                            A connection already exists between the two users in a terminal state \
                            (`ACCEPTED`, `REJECTED`, or `BLOCKED`). Once a connection is accepted, \
                            a new request cannot be sent unless the existing connection is removed. \
                            For `REJECTED` or `BLOCKED` states, a waiting period may apply before \
                            a new request can be sent.""",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = """
                            An unexpected server error occurred while creating the connection request. \
                            Retry the request; if the problem persists, contact platform support.""",
                    content = @Content(mediaType = "application/json")
            )
    })
    @PostMapping("/request")
    public ResponseEntity<ConnectionResponse> sendRequest(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = """
                            Payload containing the UUID of the user to whom the connection request \
                            should be sent. The `addresseeId` must be a valid UUID of an existing user.""",
                    required = true,
                    content = @Content(schema = @Schema(implementation = ConnectionRequestDto.class))
            )
            @Valid @RequestBody ConnectionRequestDto dto,
            @Parameter(hidden = true) Authentication auth) {

        UUID requesterId = UUID.fromString(auth.getName());
        ConnectionResponse response = connectionMapper.toResponse(
                sendConnectionRequestUseCase.sendRequest(requesterId, dto.getAddresseeId()));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            operationId = "respondToConnectionRequest",
            summary = "Accept or reject a pending connection request",
            description = """
                    Responds to a pending connection request received by the authenticated user. \
                    The recipient can either accept (`ACCEPTED`) or reject (`REJECTED`) the request.

                    **Access control:** Only the recipient (addressee) of the request can respond. \
                    The requesting user cannot modify the request after it has been sent.

                    **Valid status transitions:**
                    - `PENDING` → `ACCEPTED` — Connection becomes active; both users can now \
                      send private messages and see each other's social activity
                    - `PENDING` → `REJECTED` — Request is declined; the connection cannot be \
                      accepted later; the requesting user may send a new request after a cooldown period

                    **Post-conditions:**
                    - If `ACCEPTED`: A notification is sent to the original requester
                    - If `ACCEPTED`: Both users appear in each other's active connections list
                    - If `REJECTED`: The request is archived; no further actions are possible

                    **Idempotency:** Responding to a request that has already been processed \
                    returns the existing response without changes."""
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = """
                            Connection request response processed successfully. The response body \
                            contains the updated connection with the new status (`ACCEPTED` or `REJECTED`).""",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ConnectionResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = """
                            Invalid request payload. Common causes: the `status` field is missing, \
                            contains an invalid value (must be `ACCEPTED` or `REJECTED`), or the \
                            connection is already in a terminal state (`ACCEPTED` or `REJECTED`).""",
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
                            The authenticated user is not authorized to respond to this connection request. \
                            Only the recipient (addressee) of the original request can accept or reject it.""",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = """
                            No connection request exists with the provided `connectionId` UUID. \
                            Verify that the ID is correct and the request has not been deleted.""",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = """
                            The connection request is not in `PENDING` state. Once a request has \
                            been accepted or rejected, it cannot be modified again.""",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = """
                            An unexpected server error occurred while processing the response. \
                            Retry the request or contact support if the issue persists.""",
                    content = @Content(mediaType = "application/json")
            )
    })
    @PatchMapping("/{connectionId}")
    public ResponseEntity<ConnectionResponse> respond(
            @Parameter(
                    description = "UUID of the connection request to respond to",
                    required = true,
                    example = "550e8400-e29b-41d4-a716-446655440000"
            )
            @PathVariable UUID connectionId,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = """
                            Payload containing the response status. Valid values: `ACCEPTED` to accept \
                            the friend request, or `REJECTED` to decline it. Once rejected, the request \
                            cannot be accepted later.""",
                    required = true,
                    content = @Content(schema = @Schema(implementation = RespondConnectionDto.class))
            )
            @Valid @RequestBody RespondConnectionDto dto,
            @Parameter(hidden = true) Authentication auth) {

        UUID addresseeId = UUID.fromString(auth.getName());
        ConnectionResponse response = connectionMapper.toResponse(
                respondConnectionRequestUseCase.respond(connectionId, addresseeId, dto.getStatus()));
        return ResponseEntity.ok(response);
    }

    @Operation(
            operationId = "getActiveConnections",
            summary = "Get all active (ACCEPTED) connections for the authenticated user",
            description = """
                    Returns a list of all active connections where the authenticated user and the \
                    other user have both accepted the friend request (status = `ACCEPTED`). These \
                    connections are eligible for private messaging and appear in the user's friend list.

                    **Use case:** This endpoint is called when loading the user's friends list \
                    in the mobile app or web UI. The list is used to populate the contacts tab \
                    and to enable starting private conversations.

                    **Empty result:** Returns an empty list `[]` if the user has no active connections — \
                    never HTTP 404.

                    **Identity resolution:** The user ID is extracted from the JWT `sub` claim."""
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = """
                            Active connections retrieved successfully. Returns an array of connections \
                            with status `ACCEPTED`. Returns an empty array `[]` if the user has no \
                            friends yet — never a 404.""",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ConnectionResponse.class, type = "array")
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = """
                            No valid JWT Bearer token was provided or the token has expired. \
                            Re-authenticate and retry.""",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = """
                            An unexpected server error occurred while retrieving the connections. \
                            Retry the request or contact support if the issue persists.""",
                    content = @Content(mediaType = "application/json")
            )
    })
    @GetMapping
    public ResponseEntity<List<ConnectionResponse>> getConnections(
            @Parameter(hidden = true) Authentication auth) {
        UUID userId = UUID.fromString(auth.getName());
        List<ConnectionResponse> connections = getConnectionsUseCase.getConnections(userId)
                .stream().map(connectionMapper::toResponse).toList();
        return ResponseEntity.ok(connections);
    }

    @Operation(
            operationId = "getPendingConnectionRequests",
            summary = "Get all pending connection requests received by the authenticated user",
            description = """
                    Returns a list of all incoming connection requests that are still in `PENDING` \
                    state, waiting for the authenticated user's response.

                    **Use case:** This endpoint is called when loading the friend requests screen. \
                    The user can see who has sent them a request and decide to accept or reject each one.

                    **Empty result:** Returns an empty list `[]` if the user has no pending requests — \
                    never HTTP 404.

                    **Identity resolution:** The user ID is extracted from the JWT `sub` claim."""
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = """
                            Pending connection requests retrieved successfully. Returns an array of \
                            connections with status `PENDING`. Returns an empty array `[]` if the user \
                            has no incoming requests — never a 404.""",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ConnectionResponse.class, type = "array")
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = """
                            No valid JWT Bearer token was provided or the token has expired. \
                            Re-authenticate and retry.""",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = """
                            An unexpected server error occurred while retrieving pending requests. \
                            Retry the request or contact support if the issue persists.""",
                    content = @Content(mediaType = "application/json")
            )
    })
    @GetMapping("/pending")
    public ResponseEntity<List<ConnectionResponse>> getPending(
            @Parameter(hidden = true) Authentication auth) {
        UUID userId = UUID.fromString(auth.getName());
        List<ConnectionResponse> pending = getConnectionsUseCase.getPendingRequests(userId)
                .stream().map(connectionMapper::toResponse).toList();
        return ResponseEntity.ok(pending);
    }
}