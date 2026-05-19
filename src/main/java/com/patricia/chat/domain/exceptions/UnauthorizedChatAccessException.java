package com.patricia.chat.domain.exceptions;

/**
 * Se lanza cuando un usuario intenta acceder al chat de un parche
 * del que no es miembro activo.
 */
public class UnauthorizedChatAccessException extends RuntimeException {
    public UnauthorizedChatAccessException(String userId, String parcheId) {
        super("El usuario " + userId + " no es miembro del parche " + parcheId);
    }

    public UnauthorizedChatAccessException(String message) {
        super(message);
    }
}
