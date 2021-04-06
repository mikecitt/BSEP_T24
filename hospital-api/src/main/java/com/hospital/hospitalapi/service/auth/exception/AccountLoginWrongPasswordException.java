package com.hospital.hospitalapi.service.auth.exception;

import com.hospital.hospitalapi.service.exception.BaseException;
import com.hospital.hospitalapi.service.exception.consts.ExceptionErrorCodeType;

import org.springframework.http.HttpStatus;

public class AccountLoginWrongPasswordException extends BaseException {
    private static final long serialVersionUID = 1L;

    public AccountLoginWrongPasswordException(ExceptionErrorCodeType errorCode, String errorMessage) {
        super(errorCode, errorMessage, HttpStatus.UNAUTHORIZED);
    }
}
