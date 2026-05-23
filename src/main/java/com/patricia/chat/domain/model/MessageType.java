package com.patricia.chat.domain.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
        name = "MessageType",
        description = """
                Classifies the content type of a chat message. Determines how the client should \
                render the message in the UI. Each type may include additional fields in the \
                message payload (e.g., `IMAGE` includes an `imageUrl` field).

                **Rendering guidelines:**
                - `TEXT` → Display `content` as plain text (support basic formatting if desired)
                - `IMAGE` → Render the image from `imageUrl`, with `content` as optional alt text"""
)
public enum MessageType {

    @Schema(description = "Standard text message. The message content is stored in the `content` field.")
    TEXT,

    @Schema(description = """
            Image message. The image URL is stored in the `imageUrl` field. \
            The `content` field may contain optional alt text or a caption.""")
    IMAGE
}