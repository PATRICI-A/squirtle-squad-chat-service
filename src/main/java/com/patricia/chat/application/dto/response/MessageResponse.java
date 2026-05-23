package com.patricia.chat.application.dto.response;

import com.patricia.chat.domain.model.MessageType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.UUID;

@Schema(
        name = "MessageResponse",
        description = """
                Represents a single chat message in either a Parche group chat or a private \
                conversation. Contains the message content, sender information, timestamp, and \
                optional image URL. For group messages, `parcheId` is populated and `receiverId` \
                is null. For private messages, `receiverId` is populated and `parcheId` is null."""
)
public class MessageResponse {

    @Schema(
            description = "Unique identifier of the message",
            example = "550e8400-e29b-41d4-a716-446655440000",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private UUID id;

    @Schema(
            description = """
                    UUID of the Parche this message belongs to. Present only for group chat messages. \
                    Null for private messages.""",
            example = "660e8400-e29b-41d4-a716-446655440001"
    )
    private UUID parcheId;

    @Schema(
            description = "UUID of the user who sent the message",
            example = "770e8400-e29b-41d4-a716-446655440002",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private UUID senderId;

    @Schema(
            description = """
                    UUID of the intended recipient. Present only for private messages. \
                    Null for group messages.""",
            example = "880e8400-e29b-41d4-a716-446655440003"
    )
    private UUID receiverId;

    @Schema(
            description = "Display name of the message sender (for UI rendering purposes)",
            example = "Juan Pérez"
    )
    private String senderName;

    @Schema(
            description = "Text content of the message. Limited to 1000 characters.",
            example = "¡Hola! ¿Alguien se apunta al parche de esta tarde?"
    )
    private String content;

    @Schema(
            description = """
                    Type of the message. Determines how the client should render it:
                    - `TEXT` — Plain text message
                    - `IMAGE` — Message containing an image (check `imageUrl`)""",
            example = "TEXT"
    )
    private MessageType type;

    @Schema(
            description = "URL of the attached image. Present only when `type` is `IMAGE`.",
            example = "https://cdn.eci.edu.co/images/message-123.jpg"
    )
    private String imageUrl;

    @Schema(
            description = "ISO-8601 timestamp of when the message was sent",
            example = "2025-06-15T14:30:00.000Z",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private LocalDateTime sentAt;

    public MessageResponse() {}

    // Getters y setters
    public UUID getId()                        { return id; }
    public void setId(UUID id)                 { this.id = id; }

    public UUID getParcheId()                  { return parcheId; }
    public void setParcheId(UUID parcheId)     { this.parcheId = parcheId; }

    public UUID getSenderId()                  { return senderId; }
    public void setSenderId(UUID senderId)     { this.senderId = senderId; }

    public UUID getReceiverId()                { return receiverId; }
    public void setReceiverId(UUID receiverId) { this.receiverId = receiverId; }

    public String getSenderName()              { return senderName; }
    public void setSenderName(String name)     { this.senderName = name; }

    public String getContent()                 { return content; }
    public void setContent(String content)     { this.content = content; }

    public MessageType getType()               { return type; }
    public void setType(MessageType type)      { this.type = type; }

    public String getImageUrl()                { return imageUrl; }
    public void setImageUrl(String url)        { this.imageUrl = url; }

    public LocalDateTime getSentAt()           { return sentAt; }
    public void setSentAt(LocalDateTime t)     { this.sentAt = t; }
}