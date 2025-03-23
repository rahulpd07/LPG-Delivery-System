package com.pbg.lpg_delivery.model.request;

import com.pbg.lpg_delivery.common.Role;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request payload for user registration.")
public record SignupRequest(

        @Schema(description = "Unique username for the user", example = "john_doe")
        String username,

        @Schema(description = "Password for the user account (must be strong)", example = "P@ssw0rd123")
        String password,

        @Schema(description = "Email address of the user", example = "johndoe@example.com")
        String email,

        @Schema(description = "Phone number of the user", example = "+919876543210")
        String phoneNumber,

        @Schema(description = "Residential address of the user", example = "123, Green Street, New Delhi, India")
        String address,

        @Schema(description = "Role of the user (CUSTOMER, ADMIN, DELIVERY_PERSON)", example = "CUSTOMER")
        Role role
) {}
