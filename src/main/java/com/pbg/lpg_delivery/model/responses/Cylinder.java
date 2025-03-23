package com.pbg.lpg_delivery.model.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.pbg.lpg_delivery.model.enums.CylinderType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
@Schema(description = "Response object representing an LPG cylinder.")
public record Cylinder(

        @Schema(description = "Type of LPG cylinder (COMMERCIAL or DOMESTIC)", example = "DOMESTIC")
        CylinderType cylinderType,

        @Schema(description = "Weight of the cylinder in kilograms", example = "14.5")
        Double weight,

        @Schema(description = "Price of the cylinder in local currency", example = "850.00")
        Double price,

        @Schema(description = "Available stock quantity of the cylinder", example = "50")
        Integer stockQuantity
) {
}
