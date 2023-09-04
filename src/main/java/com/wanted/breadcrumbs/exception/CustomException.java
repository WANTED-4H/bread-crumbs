package com.wanted.breadcrumbs.exception;


import lombok.Getter;

@Getter
public class CustomException extends RuntimeException{
    private final ErrorCodeResponse errorCodeResponse;
    public CustomException(ErrorCodeResponse errorCodeResponse) {
        this.errorCodeResponse = errorCodeResponse;
    }
}
