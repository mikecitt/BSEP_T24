package com.hospital.hospitalapi.web.rest.exception;

import com.hospital.hospitalapi.service.auth.exception.AccountLoginWrongPasswordException;
import com.hospital.hospitalapi.service.auth.exception.AccountOldPasswordNotMatchException;
import com.hospital.hospitalapi.service.email.exception.EmailNotSentException;
import com.hospital.hospitalapi.service.exception.BaseException;
import com.hospital.hospitalapi.service.exception.types.EntityAlreadyExistsException;
import com.hospital.hospitalapi.service.exception.types.EntityNotFoundException;
import com.hospital.hospitalapi.service.exception.types.EntityNotSavedException;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = { EntityNotFoundException.class, EntityAlreadyExistsException.class,
            EntityNotSavedException.class, AccountOldPasswordNotMatchException.class,
            AccountLoginWrongPasswordException.class, EmailNotSentException.class })
    public ResponseEntity<ExceptionResponseDTO> handleNotFoundException(BaseException exception) {
        return buildResponseEntity(exception);
    }

    private ResponseEntity<ExceptionResponseDTO> buildResponseEntity(BaseException baseException) {
        ExceptionResponseDTO exceptionResponse = new ExceptionResponseDTO(baseException.getErrorCode().code,
                baseException.getMessage(), baseException.getStatus());

        return new ResponseEntity<>(exceptionResponse, HttpStatus.valueOf(exceptionResponse.getStatus()));
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
            HttpHeaders headers, HttpStatus status, WebRequest request) {

        return new ResponseEntity<>(ex.getBindingResult().getAllErrors(), HttpStatus.BAD_REQUEST);
    }
}
