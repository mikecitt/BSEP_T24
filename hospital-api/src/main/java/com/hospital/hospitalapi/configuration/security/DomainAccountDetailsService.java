package com.hospital.hospitalapi.configuration.security;

import java.util.ArrayList;
import java.util.List;

import com.hospital.hospitalapi.domain.user.Account;
import com.hospital.hospitalapi.service.account.AccountService;
import com.hospital.hospitalapi.service.exception.consts.ExceptionErrorCodeType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class DomainAccountDetailsService implements UserDetailsService {

    @Autowired
    private AccountService accountService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Account account = accountService.findOneByEmailOrElseThrowNotFound(email,
                ExceptionErrorCodeType.TOKEN_ACCOUNT_INVALID, "Provided token is invalid");

        return createSpringSecurityUser(account);
    }

    private User createSpringSecurityUser(Account account) {
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        grantedAuthorities.add(new SimpleGrantedAuthority(account.getAuthority().getName()));

        return new User(account.getEmail(), account.getEncryptedPassword(), account.getActive(), true, true, true,
                grantedAuthorities);
    }
}
