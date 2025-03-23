package com.pbg.lpg_delivery.exceptionHandler;


import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class UserUnauthorizedException extends RuntimeException {
    private  final String errorCode;
    private  final String errorMessage;
}
