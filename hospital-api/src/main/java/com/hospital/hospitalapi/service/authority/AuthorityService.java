package com.hospital.hospitalapi.service.authority;

import java.util.Optional;

import com.hospital.hospitalapi.domain.user.Authority;
import com.hospital.hospitalapi.repository.AuthorityRepository;
import com.hospital.hospitalapi.service.exception.consts.ExceptionErrorCodeType;
import com.hospital.hospitalapi.service.exception.types.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthorityService {

    @Autowired
    private AuthorityRepository authorityRepository;

    public Authority findOneByNameOrElseThrowNotFound(String name, ExceptionErrorCodeType errorCode,
            String errorMessage) {
        Optional<Authority> entity = authorityRepository.findOneByName(name);

        return entity.orElseThrow(() -> new EntityNotFoundException(errorCode, errorMessage));
    }
}
