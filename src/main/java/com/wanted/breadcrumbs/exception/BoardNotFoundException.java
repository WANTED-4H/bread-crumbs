package com.wanted.breadcrumbs.exception;

import org.springframework.http.HttpStatus;

public class BoardNotFoundException extends CustomException{
    public BoardNotFoundException() {
        super(new ErrorCodeResponse(HttpStatus.NOT_FOUND, "Board Not Found"));
    }
}
