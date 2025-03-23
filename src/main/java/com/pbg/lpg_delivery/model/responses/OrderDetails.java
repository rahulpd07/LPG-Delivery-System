package com.pbg.lpg_delivery.model.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Response object containing user information and their respective orders.")
public record OrderDetails(

        @Schema(description = "Information about the user who placed the orders")
        UserInfo userInfo,

        @Schema(description = "List of orders placed by the user")
        List<Order> order
) {
}
