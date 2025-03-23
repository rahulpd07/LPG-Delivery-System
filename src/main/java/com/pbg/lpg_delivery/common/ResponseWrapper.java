package com.pbg.lpg_delivery.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.web.ErrorResponse;

public record ResponseWrapper<T> (
    @JsonInclude(JsonInclude.Include.NON_NULL)
    T data,
    @JsonInclude(JsonInclude.Include.NON_NULL)
    ErrorResponse error
){
    public static final class Builder<T> {
        private T data;
        private ErrorResponse error;

        public Builder<T> data(T data) {
            this.data = data;
            return this;
        }

        public Builder<T> error(ErrorResponse error) {
            this.error = error;
            return this;
        }

        public ResponseWrapper<T> build() {
            return new ResponseWrapper<>(data, error);
        }
    }
}
