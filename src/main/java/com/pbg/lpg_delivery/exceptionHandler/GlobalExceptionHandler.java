package com.pbg.lpg_delivery.exceptionHandler;



import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({LpgException.class})
    ResponseEntity<ConcreteErrorResponse> handleAuthException(LpgException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ConcreteErrorResponse(ex.getErrorCode(), ex.getErrorMessage()));    }

    @ExceptionHandler({ParentException.class})
    ResponseEntity<ConcreteErrorResponse> handleParentException(ParentException ex){
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ConcreteErrorResponse(ex.getErrorCode(), ex.getErrorMessage()));
    }
    @ExceptionHandler({AccessDeniedException.class})
    ResponseEntity<ConcreteErrorResponse> handleAccessDeniedException(AccessDeniedException ex){
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ConcreteErrorResponse("AUTH-0001", "Access denied, You are not allowed to access this"));
    }

    @ExceptionHandler({UserUnauthorizedException.class})
    ResponseEntity<ConcreteErrorResponse> handleAuthException(UserUnauthorizedException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ConcreteErrorResponse(ex.getErrorCode(), ex.getErrorMessage()));  }
}
