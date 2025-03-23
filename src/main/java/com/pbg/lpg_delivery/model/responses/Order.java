package com.pbg.lpg_delivery.model.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.pbg.lpg_delivery.model.enums.CylinderType;
import com.pbg.lpg_delivery.model.enums.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Response object representing an order for LPG cylinders.")
public record Order(

        @Schema(description = "Type of LPG cylinder (COMMERCIAL or DOMESTIC)", example = "DOMESTIC")
        CylinderType cylinderType,

        @Schema(description = "Capacity of the cylinder in kilograms", example = "14.5")
        Double capacity,

        @Schema(description = "Number of cylinders ordered", example = "2")
        Integer quantity,

        @Schema(description = "Total price of the order in local currency", example = "1700.00")
        Double totalPrice,

        @Schema(description = "Current status of the order", example = "PENDING")
        OrderStatus status,

        @Schema(description = "Date and time when the order was placed", example = "2025-02-20T10:30:00")
        LocalDateTime orderDate,

        @Schema(description = "Expected delivery date and time", example = "2025-02-22T15:00:00")
        LocalDateTime deliveryDate
) {
}
