package com.patricia.chat.domain.exceptions;

import java.util.UUID;

public class ConnectionNotFoundException extends RuntimeException {
    public ConnectionNotFoundException(UUID connectionId) {
        super("Conexión no encontrada: " + connectionId);
    }
}
