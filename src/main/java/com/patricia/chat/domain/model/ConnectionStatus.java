package com.patricia.chat.domain.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
        name = "ConnectionStatus",
        description = """
                Represents the current state of a connection (friend relationship) between two users. \
                The status determines what actions are allowed and how the connection appears in \
                the user's friend list or pending requests screen.

                **State transitions:**
                - `PENDING` → `ACCEPTED` (recipient accepts)
                - `PENDING` → `REJECTED` (recipient declines)
                - `ACCEPTED` → `BLOCKED` (either user blocks the other)
                - Any state → `BLOCKED` (blocking overrides all other states)

                **Terminal states:** `REJECTED` and `BLOCKED` — no further transitions possible."""
)
public enum ConnectionStatus {

    @Schema(
            description = """
                    Request has been sent but not yet responded to. The recipient can either \
                    accept or reject. The request appears in the recipient's pending list and \
                    in the sender's outgoing requests list."""
    )
    PENDING,

    @Schema(
            description = """
                    Request has been accepted by the recipient. The connection is active, and \
                    both users can send private messages and view each other's social activity. \
                    Appears in the active connections list for both users."""
    )
    ACCEPTED,

    @Schema(
            description = """
                    Request has been declined by the recipient. This is a terminal state — no \
                    further actions are possible on this connection. The requester may send a \
                    new request after a cooldown period."""
    )
    REJECTED,

    @Schema(
            description = """
                    One user has blocked the other. This is a terminal state that overrides all \
                    other states. Blocked users cannot send messages or connection requests to \
                    each other. The blocked user does not appear in the blocker's lists."""
    )
    BLOCKED
}