package com.pbg.lpg_delivery.model.request;
import io.swagger.v3.oas.annotations.media.Schema;


@Schema(description = "Request payload for submitting customer feedback after order delivery.")
public record FeedbackRequest(
        @Schema(description = "Customer rating for the order", example = "4", minimum = "1", maximum = "5")
        Integer rating,

        @Schema(description = "Additional comments or feedback from the customer", example = "The delivery was on time, and the cylinder was in good condition.")
        String comments
) {
}

