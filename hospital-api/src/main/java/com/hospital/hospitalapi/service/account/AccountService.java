package com.hospital.hospitalapi.service.account;

import java.util.Optional;

import com.hospital.hospitalapi.domain.user.Account;
import com.hospital.hospitalapi.repository.AccountRepository;
import com.hospital.hospitalapi.service.exception.consts.ExceptionErrorCodeType;
import com.hospital.hospitalapi.service.exception.types.EntityAlreadyExistsException;
import com.hospital.hospitalapi.service.exception.types.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    public Account findOneByEmailOrElseThrowNotFound(String email, ExceptionErrorCodeType errorCode,
            String errorMessage) {
        Optional<Account> entity = accountRepository.findOneByEmail(email);

        return entity.orElseThrow(() -> new EntityNotFoundException(errorCode, errorMessage));
    }

    public void findOneByEmailOrElseThrowAlreadyExists(String email, ExceptionErrorCodeType errorCode,
            String errorMessage) {
        Optional<Account> entity = accountRepository.findOneByEmail(email);

        if (entity.isPresent()) {
            throw new EntityAlreadyExistsException(errorCode, errorMessage);
        }
    }

    public void save(Account account, ExceptionErrorCodeType errorCode, String errorMessage) {
        try {
            accountRepository.save(account);
        } catch (Exception exception) {
            throw new EntityAlreadyExistsException(errorCode, errorMessage);
        }
    }

    public Account findOneByHashOrElseThrowNotFound(String hash, ExceptionErrorCodeType errorCode,
            String errorMessage) {
        Optional<Account> entity = accountRepository.findOneByHash(hash);

        return entity.orElseThrow(() -> new EntityNotFoundException(errorCode, errorMessage));
    }
}
