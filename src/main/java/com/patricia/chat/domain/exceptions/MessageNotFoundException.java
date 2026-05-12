package com.patricia.chat.domain.exceptions;

import java.util.UUID;

public class MessageNotFoundException extends RuntimeException {
    public MessageNotFoundException(UUID messageId) {
        super("Mensaje no encontrado: " + messageId);
    }
}
