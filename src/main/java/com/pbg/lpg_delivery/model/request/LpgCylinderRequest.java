package com.pbg.lpg_delivery.model.request;

import com.pbg.lpg_delivery.model.enums.CylinderType;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request payload for adding or updating LPG cylinder stock.")
public record LpgCylinderRequest(
        @Schema(description = "Type of the LPG cylinder (e.g., COMMERCIAL or DOMESTIC)", example = "DOMESTIC")
        CylinderType type,

        @Schema(description = "Weight of the LPG cylinder in kilograms", example = "14.5")
        Double weight,

        @Schema(description = "Price of the LPG cylinder", example = "1200.50")
        Double price,

        @Schema(description = "Number of cylinders available in stock", example = "50")
        Integer stockQuantity
) {
}

