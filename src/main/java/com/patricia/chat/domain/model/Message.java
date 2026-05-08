package com.patricia.chat.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entidad de dominio pura — no depende de JPA ni de ningún framework.
 * Representa un mensaje enviado dentro de un parche.
 */
public class Message {

    private UUID id;
    private UUID parcheId;
    private UUID senderId;
    private String senderName;
    private String content;
    private MessageType type;
    private String imageUrl;
    private LocalDateTime sentAt;

    public Message() {}

    public Message(UUID parcheId, UUID senderId, String senderName,
                   String content, MessageType type) {
        this.id       = UUID.randomUUID();
        this.parcheId = parcheId;
        this.senderId = senderId;
        this.senderName = senderName;
        this.content  = content;
        this.type     = type;
        this.sentAt   = LocalDateTime.now();
    }

    // Getters y setters
    public UUID getId()                      { return id; }
    public void setId(UUID id)               { this.id = id; }

    public UUID getParcheId()                { return parcheId; }
    public void setParcheId(UUID parcheId)   { this.parcheId = parcheId; }

    public UUID getSenderId()                { return senderId; }
    public void setSenderId(UUID senderId)   { this.senderId = senderId; }

    public String getSenderName()            { return senderName; }
    public void setSenderName(String name)   { this.senderName = name; }

    public String getContent()               { return content; }
    public void setContent(String content)   { this.content = content; }

    public MessageType getType()             { return type; }
    public void setType(MessageType type)    { this.type = type; }

    public String getImageUrl()              { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public LocalDateTime getSentAt()         { return sentAt; }
    public void setSentAt(LocalDateTime t)   { this.sentAt = t; }
}
