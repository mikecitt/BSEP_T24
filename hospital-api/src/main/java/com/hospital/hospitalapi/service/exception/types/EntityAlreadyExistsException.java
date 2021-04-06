package com.hospital.hospitalapi.service.exception.types;

import com.hospital.hospitalapi.service.exception.BaseException;
import com.hospital.hospitalapi.service.exception.consts.ExceptionErrorCodeType;

import org.springframework.http.HttpStatus;

public class EntityAlreadyExistsException extends BaseException {
    private static final long serialVersionUID = 1L;

    public EntityAlreadyExistsException(ExceptionErrorCodeType errorCode, String errorMessage) {
        super(errorCode, errorMessage, HttpStatus.CONFLICT);
    }
}
