package com.pbg.lpg_delivery.security;




import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request payload for user authentication.")
public record LoginRequest(
        @Schema(description = "Username of the user", example = "jackson")
        String username,

        @Schema(description = "Password for authentication", example = "Password123")
        String password
) {}


