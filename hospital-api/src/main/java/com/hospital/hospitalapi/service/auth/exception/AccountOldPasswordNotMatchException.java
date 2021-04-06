package com.hospital.hospitalapi.service.auth.exception;

import com.hospital.hospitalapi.service.exception.BaseException;
import com.hospital.hospitalapi.service.exception.consts.ExceptionErrorCodeType;

import org.springframework.http.HttpStatus;

public class AccountOldPasswordNotMatchException extends BaseException {
    private static final long serialVersionUID = 1L;

    public AccountOldPasswordNotMatchException(ExceptionErrorCodeType errorCode, String errorMessage) {
        super(errorCode, errorMessage, HttpStatus.FORBIDDEN);
    }
}
