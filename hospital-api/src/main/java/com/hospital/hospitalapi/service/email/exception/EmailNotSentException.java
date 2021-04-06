package com.hospital.hospitalapi.service.email.exception;

import com.hospital.hospitalapi.service.exception.BaseException;
import com.hospital.hospitalapi.service.exception.consts.ExceptionErrorCodeType;

import org.springframework.http.HttpStatus;

public class EmailNotSentException extends BaseException {
    private static final long serialVersionUID = 1L;

    public EmailNotSentException(ExceptionErrorCodeType errorCode, String errorMessage) {
        super(errorCode, errorMessage, HttpStatus.SERVICE_UNAVAILABLE);
    }
}
