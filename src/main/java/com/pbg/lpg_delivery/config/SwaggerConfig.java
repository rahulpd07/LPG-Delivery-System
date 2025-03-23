package com.pbg.lpg_delivery.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class SwaggerConfig {
    @Bean
    public OpenAPI openAPI() {
        final String securitySchemeName = "bearerAuth";
        return new OpenAPI()
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components().addSecuritySchemes(
                        securitySchemeName,
                        new SecurityScheme()
                                .name(securitySchemeName)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                ))
                .info(new Info()
                        .title("LPG Delivery Service API")
                        .description("""
                                The LPG Delivery Service API provides a seamless and efficient way for users to place, track, and manage their LPG cylinder orders. 
                                This API allows customers to request LPG refills, view order history, and check delivery statuses. 
                                Admins can manage inventory, view customer orders, and track overall service performance.
                                Delivery personnel can view assigned deliveries and update order statuses in real time.

                                ### Key Features:
                                - **Customer Features**
                                    - Place new LPG cylinder orders.
                                    - Track order status and estimated delivery time.
                                    - View order history and past transactions.
                                    - Provide feedback after order delivery.
                                
                                - **Admin Features**
                                    - Manage and monitor LPG stock levels.
                                    - View and filter customer orders by date, phone number, and username.
                                    - Assign orders to delivery personnel and track deliveries.
                                
                                - **Delivery Personnel Features**
                                    - View assigned deliveries.
                                    - Update order status (In-Transit, Delivered).
                                    - Add notes related to delivery (e.g., unsuccessful attempt, customer unavailable).
                                
                                - **Security**
                                    - Secure authentication with JWT-based Bearer Token.
                                    - Role-based access control (Customers, Admins, Delivery Personnel).
                                    
                                This API is designed to enhance efficiency and provide a seamless LPG delivery experience for customers, administrators, and delivery personnel.
                                """)
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("LPG Delivery Support")
                                .email("support@lpgdelivery.com")
                                .url("https://lpgdelivery.com")
                        )
                );
    }
}
