package com.wanted.breadcrumbs.exception;

import lombok.*;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ErrorCodeResponse {
    private HttpStatus httpStatus;
    private String message;
}
