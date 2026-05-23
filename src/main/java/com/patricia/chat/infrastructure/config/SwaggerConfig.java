package com.patricia.chat.infrastructure.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * SpringDoc OpenAPI configuration for the Chat Service.
 * Registers the {@link OpenAPI} bean that populates the Swagger UI with service metadata,
 * security schemes, and organised API tags.
 */
@Configuration
public class SwaggerConfig {

    private static final String BEARER_SCHEME = "Bearer Authentication";

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("PATRICI.A Chat Service API")
                        .description("""
                                This microservice is responsible for managing real-time and historical chat \
                                functionality for the PATRICI.A platform at Universidad ECI.

                                Key responsibilities include:
                                - **Group Chat (Parches)**: Real-time messaging within Parche groups. Users can \
                                  send text messages and images to all members of a Parche they have joined. \
                                  Access is restricted to active Parche members only.

                                - **Private Messaging**: One-to-one conversations between users who have an \
                                  active connection (friend status). Both participants must have accepted the \
                                  connection request before messaging is allowed.

                                - **Connection Management**: Complete friend request lifecycle including sending \
                                  requests, accepting/rejecting, listing active connections, and viewing pending \
                                  requests. Supports states: PENDING, ACCEPTED, REJECTED, BLOCKED.

                                - **Message Persistence**: All messages are persisted and retrievable via paginated \
                                  history endpoints. Messages are retained indefinitely for audit and moderation purposes.

                                - **Real-time Notifications**: The service publishes events to the message exchange \
                                  for real-time delivery via WebSocket (not covered by this REST API).

                                All write endpoints require a valid Bearer JWT token issued by the authentication \
                                service. The user ID is extracted from the JWT `sub` claim.
                                """)
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("PATRICI.A Platform Team")
                                .email("support@eci.edu.co")
                                .url("https://www.escuelaing.edu.co")))
                .addSecurityItem(new SecurityRequirement().addList(BEARER_SCHEME))
                .components(new Components()
                        .addSecuritySchemes(BEARER_SCHEME, createSecurityScheme()))
                .addTagsItem(new Tag()
                        .name("Messages")
                        .description("""
                                Endpoints for retrieving chat message histories from both Parche group chats \
                                and private conversations. All endpoints are read-only (GET) and require the \
                                authenticated user to have access to the target conversation: active membership \
                                for Parche groups or an ACCEPTED connection for private chats. Messages are \
                                returned in chronological order (oldest first) with pagination support to \
                                efficiently load large conversation histories.
                                """))
                .addTagsItem(new Tag()
                        .name("Connections")
                        .description("""
                                Endpoints for managing user-to-user connections (friend requests). Supports the \
                                complete friend request lifecycle: sending requests, accepting/rejecting pending \
                                requests, listing active connections (ACCEPTED), and viewing pending incoming \
                                requests (PENDING). Connection states: PENDING → ACCEPTED/REJECTED/BLOCKED. \
                                All endpoints require a valid Bearer JWT token and resolve the authenticated \
                                user's identity from the token's `sub` claim.
                                """));
    }

    /**
     * Creates the security scheme configuration for JWT Bearer authentication.
     *
     * @return a {@link SecurityScheme} configured for HTTP Bearer authentication with JWT format
     */
    private SecurityScheme createSecurityScheme() {
        return new SecurityScheme()
                .name(BEARER_SCHEME)
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .description("""
                        JWT Bearer token authentication for all chat endpoints.
                        
                        Required for:
                        - Retrieving message histories (Messages tag)
                        - Managing connections (Connections tag)
                        
                        The token must be obtained from the authentication service.
                        Include the token in the Authorization header as:
                        `Authorization: Bearer <your-jwt-token>`
                        
                        The user ID is extracted from the JWT `sub` claim.
                        """);
    }
}