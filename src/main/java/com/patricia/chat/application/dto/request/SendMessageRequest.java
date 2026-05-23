package com.patricia.chat.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(
        name = "SendMessageRequest",
        description = """
                Payload for sending a new message in either a Parche group chat or a private \
                conversation. Supports text messages and optional image attachments. For group \
                chats, the `receiverId` should be null; for private chats, `parcheId` should be null."""
)
public class SendMessageRequest {

    @NotBlank(message = "El contenido del mensaje no puede estar vacío")
    @Size(max = 1000, message = "El mensaje no puede superar 1000 caracteres")
    @Schema(
            description = "Text content of the message. Cannot be blank and limited to 1000 characters.",
            example = "¡Hola! ¿Alguien se apunta al parche de esta tarde?",
            maxLength = 1000,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String content;

    @Schema(
            description = """
                    Optional URL of an image attached to the message. When provided, the message \
                    type is automatically set to `IMAGE`. The image should be uploaded to a CDN \
                    before sending the message.""",
            example = "https://cdn.eci.edu.co/images/message-123.jpg"
    )
    private String imageUrl;

    public SendMessageRequest() {}

    public String getContent()           { return content; }
    public void setContent(String c)     { this.content = c; }

    public String getImageUrl()          { return imageUrl; }
    public void setImageUrl(String url)  { this.imageUrl = url; }
}