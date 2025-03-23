package com.pbg.lpg_delivery.model.request;

import com.pbg.lpg_delivery.model.enums.CylinderType;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request payload for modifying an existing LPG cylinder order.")
public record ModifyOrderRequest(
        @Schema(description = "Type of LPG cylinder (e.g., COMMERCIAL or DOMESTIC)", example = "DOMESTIC")
        CylinderType cylinderType,

        @Schema(description = "Capacity of the LPG cylinder in kilograms", example = "14.5")
        Double capacity,

        @Schema(description = "Number of cylinders requested in the modification", example = "2")
        Integer quantity
) {}
