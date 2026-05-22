package com.patricia.chat.infrastructure.external;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

/**
 * Cliente Feign que consulta al Parche Service (Equipo 3)
 * si un usuario es miembro activo de un parche.
 *
 * La URL base se configura en application.properties:
 *   parche-service.url=http://localhost:8083
 */
@FeignClient(name = "parche-service", url = "${parche-service.url}")
public interface ParcheServiceClient {

    @GetMapping("/api/v1/parches/{parcheId}/miembros/{userId}/check")
    boolean isMember(@PathVariable UUID parcheId, @PathVariable UUID userId);
}
