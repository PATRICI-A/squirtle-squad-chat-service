package com.patricia.chat.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class SendMessageRequest {

    @NotBlank(message = "El contenido del mensaje no puede estar vacío")
    @Size(max = 1000, message = "El mensaje no puede superar 1000 caracteres")
    private String content;

    private String imageUrl;

    public SendMessageRequest() {}

    public String getContent()           { return content; }
    public void setContent(String c)     { this.content = c; }

    public String getImageUrl()          { return imageUrl; }
    public void setImageUrl(String url)  { this.imageUrl = url; }
}
