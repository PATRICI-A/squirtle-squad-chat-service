package com.patricia.chat.infrastructure.adapters.persistence.entity;

import com.patricia.chat.domain.model.MessageType;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.index.Indexed;

import java.time.LocalDateTime;
import java.util.UUID;

@Document(collection = "messages")
public class MessageEntity {

    @Id
    private UUID id;

    @Indexed
    @Field("parche_id")
    private UUID parcheId;

    @Field("sender_id")
    private UUID senderId;

    @Indexed
    @Field("receiver_id")
    private UUID receiverId;

    @Field("sender_name")
    private String senderName;

    private String content;

    private MessageType type;

    @Field("image_url")
    private String imageUrl;

    @Field("sent_at")
    private LocalDateTime sentAt;

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