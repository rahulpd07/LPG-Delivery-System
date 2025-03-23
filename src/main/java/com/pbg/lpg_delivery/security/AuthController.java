package com.pbg.lpg_delivery.security;

import com.pbg.lpg_delivery.common.ResponseWrapper;
import com.pbg.lpg_delivery.exceptionHandler.LpgException;
import com.pbg.lpg_delivery.exceptionHandler.ParentException;
import com.pbg.lpg_delivery.exceptionHandler.UserUnauthorizedException;
import com.pbg.lpg_delivery.model.request.SignupRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "APIs for user authentication and management")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(summary = "Register a new user customer only", description = "Allows a customer to create an account.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User registered successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "409", description = "User already exists")
    })
    @PostMapping("/signup")
    public ResponseEntity<String> registerUser(
            @Parameter(description = "User signup request containing username, password, and other details")
            @RequestBody SignupRequest request) {
        try {
            authService.registerUser(request);
            logger.info("User registered successfully: {}", request.username());
            return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully");
        } catch (ParentException ex) {
            logger.error("Signup failed: {}", ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            logger.error("Unexpected error during signup", ex);
            throw ex;
        }
    }

    @Operation(summary = "Authenticate a user", description = "Allows an existing user to log in and receive an authentication token.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User authenticated successfully"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/signin")
    public ResponseEntity<ResponseWrapper<String>> authenticateUser(
            @Parameter(description = "User authentication request containing username and password")
            @RequestBody LoginRequest authRequest) {
        try {
            String token = authService.authenticateUser(authRequest);
            logger.info("User authenticated successfully: {}", authRequest.username());
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseWrapper.Builder<String>().data(token).build());
        } catch (LpgException ex) {
            logger.warn("Authentication failed: {}", ex.getMessage());
            throw ex;
        }catch (UserUnauthorizedException ex) {
            logger.error("User unauthorized, wrong credentials {}",ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            logger.error("Unexpected error during authentication", ex);
            throw ex;
        }
    }

    @Operation(summary = "Create a delivery person", description = "Allows an admin to create a delivery person account.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Delivery person created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "403", description = "User is not authorized to perform this action"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/create-delivery-person")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseWrapper<String>> createDeliveryPersonOrAdmin(
            @Parameter(description = "Signup request containing delivery person's details")
            @RequestBody SignupRequest request) {
        try {
            authService.createDeliveryPersonOrAdmin(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(
                    new ResponseWrapper.Builder<String>()
                            .data("User created successfully")
                            .build()
            );
        } catch (LpgException ex) {
            logger.error("Business error while creating delivery person: {}", ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            logger.error("Unexpected error while creating delivery person: {}", ex.getMessage(), ex);
            throw ex;
        }
    }
}
