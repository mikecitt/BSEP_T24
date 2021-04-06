package com.hospital.hospitalapi.service.auth;

import java.time.LocalDateTime;

import javax.validation.Valid;

import com.hospital.hospitalapi.configuration.security.AuthoritiesConstants;
import com.hospital.hospitalapi.domain.user.Account;
import com.hospital.hospitalapi.service.account.AccountService;
import com.hospital.hospitalapi.service.auth.exception.AccountLoginWrongPasswordException;
import com.hospital.hospitalapi.service.auth.exception.AccountOldPasswordNotMatchException;
import com.hospital.hospitalapi.service.authority.AuthorityService;
import com.hospital.hospitalapi.service.email.ComposeEmailService;
import com.hospital.hospitalapi.service.exception.consts.ExceptionErrorCodeType;
import com.hospital.hospitalapi.service.exception.types.EntityAlreadyExistsException;
import com.hospital.hospitalapi.util.HashValueProvider;
import com.hospital.hospitalapi.util.TokenProvider;
import com.hospital.hospitalapi.web.rest.auth.payload.request.AccountLoginRequestDTO;
import com.hospital.hospitalapi.web.rest.auth.payload.request.AccountRegisterRequest;
import com.hospital.hospitalapi.web.rest.auth.payload.request.ChangePasswordRequestDTO;
import com.hospital.hospitalapi.web.rest.auth.payload.request.ResetPasswordRequestDTO;
import com.hospital.hospitalapi.web.rest.auth.payload.response.AccountLoginResponseDTO;
import com.hospital.hospitalapi.web.rest.auth.payload.response.AccountResponseDTO;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

@Service
public class AuthenticationService {

        @Autowired
        private AccountService accountService;

        @Autowired
        private ModelMapper modelMapper;

        @Autowired
        private BCryptPasswordEncoder bCryptPasswordEncoder;

        @Autowired
        private TokenProvider tokenProvider;

        @Autowired
        private AuthorityService authorityService;

        @Autowired
        ComposeEmailService composeEmailService;

        public AccountLoginResponseDTO login(@RequestBody @Valid AccountLoginRequestDTO request) {
                Account account = accountService.findOneByEmailOrElseThrowNotFound(request.getEmail(),
                                ExceptionErrorCodeType.LOGIN_ACCOUNT_NOT_FOUND,
                                String.format("Account with %s email not found", request.getEmail()));

                if (!bCryptPasswordEncoder.matches(request.getPassword(), account.getEncryptedPassword())) {
                        throw new AccountLoginWrongPasswordException(
                                        ExceptionErrorCodeType.LOGIN_ACCOUNT_WRONG_PASSWORD,
                                        String.format("Password is invalid for %s", account.getEmail()));
                }

                String token = tokenProvider.generateToken(account);
                return new AccountLoginResponseDTO(token, modelMapper.map(account, AccountResponseDTO.class));
        }

        public String register(@RequestBody @Valid AccountRegisterRequest request) {
                accountService.findOneByEmailOrElseThrowAlreadyExists(request.getEmail(),
                                ExceptionErrorCodeType.REGISTER_EMAIL_ALREADY_EXISTS,
                                String.format("Email %s is already taken", request.getEmail()));

                String hash = HashValueProvider.generateHash();

                Account account = modelMapper.map(request, Account.class);
                account.setCreatedAt(LocalDateTime.now());
                account.setUuid(HashValueProvider.generateHash());
                account.setEncryptedPassword(bCryptPasswordEncoder.encode(request.getPassword()));
                account.setActive(false);
                account.setHash(hash);

                // TODO check authority
                account.setAuthority(authorityService.findOneByNameOrElseThrowNotFound(AuthoritiesConstants.DOCTOR,
                                ExceptionErrorCodeType.REGISTER_AUTHORITY_NOT_FOUND,
                                String.format("Role %s not found", AuthoritiesConstants.DOCTOR)));
                accountService.save(account, ExceptionErrorCodeType.REGISTER_ACCOUNT_NOT_SAVED,
                                String.format("Account %s was not saved in the database", request.getEmail()));

                composeEmailService.createActivateAccountMail(new String[] { account.getEmail() },
                                String.format("http://localhost:8080/api/auth/activate?hash=%s", hash));

                return hash;
        }

        public Void activate(String hash) {
                Account account = accountService.findOneByHashOrElseThrowNotFound(hash,
                                ExceptionErrorCodeType.ACTIVATE_ACCOUNT_NOT_FOUND,
                                String.format("Account with %s hash not found", hash));

                if (account.getActive()) {
                        throw new EntityAlreadyExistsException(ExceptionErrorCodeType.ACCOUNT_ALREADY_ACTIVATED,
                                        "Account is already activated");
                }

                account.setActive(true);
                account.setHash(HashValueProvider.generateHash());

                accountService.save(account, ExceptionErrorCodeType.ACTIVATE_ACCOUNT_NOT_SAVED,
                                "Account was not saved in the database");

                return null;
        }

        public String forgotPassword(String email) {
                Account account = accountService.findOneByEmailOrElseThrowNotFound(email,
                                ExceptionErrorCodeType.FORGOT_PASSWORD_ACCOUNT_NOT_FOUND,
                                String.format("Account with %s email not found", email));

                composeEmailService.createForgotPasswordMail(new String[] { email }, String
                                .format("http://localhost:8080/api/auth/reset-password?hash=%s", account.getHash()));

                return account.getHash();
        }

        public Void resetPassword(String hash, ResetPasswordRequestDTO request) {

                Account account = accountService.findOneByHashOrElseThrowNotFound(hash,
                                ExceptionErrorCodeType.RESET_PASSWORD_ACCOUNT_NOT_FOUND,
                                String.format("Account with %s hash not found", hash));

                account.setEncryptedPassword(bCryptPasswordEncoder.encode(request.getPassword()));
                account.setHash(HashValueProvider.generateHash());
                accountService.save(account, ExceptionErrorCodeType.RESET_PASSWORD_ACCOUT_NOT_SAVED,
                                "Account was not saved in the database");

                return null;
        }

        public Void changePassword(User user, ChangePasswordRequestDTO request) {
                Account account = accountService.findOneByEmailOrElseThrowNotFound(user.getUsername(),
                                ExceptionErrorCodeType.CHANGE_PASSWORD_ACCOUNT_NOT_FOUND,
                                "Provided token is not valid");

                if (!bCryptPasswordEncoder.matches(request.getOldPassword(), account.getEncryptedPassword())) {
                        throw new AccountOldPasswordNotMatchException(ExceptionErrorCodeType.CHANGE_PASSWORD_NOT_MATCH,
                                        "Provided old password is incorrect");
                }

                account.setEncryptedPassword(bCryptPasswordEncoder.encode(request.getNewPassword()));
                accountService.save(account, ExceptionErrorCodeType.CHANGE_PASSWORD_ACCOUNT_NOT_SAVED,
                                "Account was not saved in the database");

                return null;
        }
}
