package com.patricia.chat.application.dto.response;

import com.patricia.chat.domain.model.MessageType;
import java.time.LocalDateTime;
import java.util.UUID;

public class MessageResponse {

    private UUID id;
    private UUID parcheId;
    private UUID senderId;
    private String senderName;
    private String content;
    private MessageType type;
    private String imageUrl;
    private LocalDateTime sentAt;

    public MessageResponse() {}

    // Getters y setters
    public UUID getId()                        { return id; }
    public void setId(UUID id)                 { this.id = id; }

    public UUID getParcheId()                  { return parcheId; }
    public void setParcheId(UUID parcheId)     { this.parcheId = parcheId; }

    public UUID getSenderId()                  { return senderId; }
    public void setSenderId(UUID senderId)     { this.senderId = senderId; }

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
