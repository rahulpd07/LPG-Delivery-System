package com.pbg.lpg_delivery.exceptionHandler;

public class ParentException extends LpgException {
    public ParentException(String errorCode, String errorMessage) {
        super(errorCode, errorMessage);
    }
}
