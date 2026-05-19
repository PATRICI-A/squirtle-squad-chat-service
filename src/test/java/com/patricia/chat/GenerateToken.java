package com.patricia.chat;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;
import javax.crypto.SecretKey;

public class GenerateToken {
    public static void main(String[] args) {
        String secret = "patricia-super-secret-key-2026-minimum-32-chars";
        SecretKey secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        
        UUID testUserId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        
        String token = Jwts.builder()
                .subject(testUserId.toString())
                .claim("email", "test@escuelaing.edu.co")
                .claim("role", "ESTUDIANTE")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 3600000))
                .signWith(secretKey)
                .compact();
                
        System.out.println("\n--- TOKEN GENERADO ---");
        System.out.println(token);
        System.out.println("----------------------\n");
    }
}
