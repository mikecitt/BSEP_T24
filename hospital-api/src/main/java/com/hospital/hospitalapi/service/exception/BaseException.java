package com.hospital.hospitalapi.service.exception;

import com.hospital.hospitalapi.service.exception.consts.ExceptionErrorCodeType;

import org.springframework.http.HttpStatus;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class BaseException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private HttpStatus status;
    private ExceptionErrorCodeType errorCode;

    public BaseException(ExceptionErrorCodeType errorCode, String errorMessage, HttpStatus status) {
        super(errorMessage);
        this.status = status;
        this.errorCode = errorCode;
    }
}
