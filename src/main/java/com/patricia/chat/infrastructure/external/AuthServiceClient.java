package com.patricia.chat.infrastructure.external;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "auth-service", url = "${auth-service.url}")
public interface AuthServiceClient {

    @GetMapping("/api/v1/auth/validate")
    AuthTokenInfo validateToken(@RequestHeader("Authorization") String bearer);
}
