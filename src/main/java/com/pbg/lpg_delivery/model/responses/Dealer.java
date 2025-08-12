package com.pbg.lpg_delivery.model.responses;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record Dealer(
        String companyName,
        boolean authorized,
        DealerAddress address
) {}

