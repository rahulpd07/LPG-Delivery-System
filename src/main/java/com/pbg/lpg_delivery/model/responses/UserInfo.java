package com.pbg.lpg_delivery.model.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "User information response containing essential user details.")
public record UserInfo(

        @Schema(description = "Username of the user", example = "john_doe")
        String username,

        @Schema(description = "Email address of the user", example = "john.doe@example.com")
        String email,

        @Schema(description = "Phone number of the user", example = "+1234567890")
        String phoneNumber,

        @Schema(description = "Residential address of the user", example = "123 Main Street, Springfield")
        String address
) {}
