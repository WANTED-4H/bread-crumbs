package com.wanted.breadcrumbs.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler{

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorCodeResponse> exceptionHandler(Exception e){
        ErrorCodeResponse errorCodeResponse = new ErrorCodeResponse(HttpStatus.BAD_REQUEST, e.getMessage());

        return new ResponseEntity<>(errorCodeResponse, errorCodeResponse.getHttpStatus());
    }

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorCodeResponse> customExceptionHandler(CustomException e){
        ErrorCodeResponse errorCodeResponse = e.getErrorCodeResponse();
        return new ResponseEntity<>(errorCodeResponse, errorCodeResponse.getHttpStatus());
    }
}
