package com.patricia.chat.domain.exceptions;

public class ConnectionAlreadyExistsException extends RuntimeException {
    public ConnectionAlreadyExistsException(String requesterId, String addresseeId) {
        super("Ya existe una solicitud de conexión entre " + requesterId + " y " + addresseeId);
    }
}
