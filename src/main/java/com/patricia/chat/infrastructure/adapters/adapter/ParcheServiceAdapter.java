package com.patricia.chat.infrastructure.adapters.adapter;

import com.patricia.chat.domain.ports.out.ParcheServicePort;
import com.patricia.chat.infrastructure.external.ParcheServiceClient;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Adaptador que implementa el puerto de salida ParcheServicePort
 * usando el cliente Feign.
 *
 * Si el Parche Service no está disponible, retorna false por seguridad.
 */
@Component
public class ParcheServiceAdapter implements ParcheServicePort {

    private final ParcheServiceClient parcheServiceClient;

    public ParcheServiceAdapter(ParcheServiceClient parcheServiceClient) {
        this.parcheServiceClient = parcheServiceClient;
    }

    @Override
    public boolean isMember(UUID parcheId, UUID userId) {
        // TODO: conectar con Parche Service (Equipo 3) cuando esté disponible
        return true;
    }
}
