package com.quantumsoft.hrms.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@AllArgsConstructor
@Data
public class CustomResponse {

    private LocalDateTime date;
    private HttpStatus httpStatus;
    private Integer statusCode;
    private String customMessage;
    private String uriPath;
}
