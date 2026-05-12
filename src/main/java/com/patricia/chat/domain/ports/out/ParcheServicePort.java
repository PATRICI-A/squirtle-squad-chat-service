package com.patricia.chat.domain.ports.out;

import java.util.UUID;

public interface ParcheServicePort {
    /**
     * Consulta al Parche Service (Equipo 3) si el usuario
     * es miembro activo del parche dado.
     */
    boolean isMember(UUID parcheId, UUID userId);
}
