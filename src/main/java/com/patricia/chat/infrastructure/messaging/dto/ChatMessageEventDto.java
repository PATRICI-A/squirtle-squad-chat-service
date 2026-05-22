package com.patricia.chat.infrastructure.messaging.dto;

import lombok.*;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageEventDto {
    private UUID recipientUserId;
    private String senderName;
    private UUID conversationId;
}
