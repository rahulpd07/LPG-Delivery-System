package com.pbg.lpg_delivery.exceptionHandler;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ConcreteErrorResponse {
    private String errorCode;
    private String errorMessage;

}

