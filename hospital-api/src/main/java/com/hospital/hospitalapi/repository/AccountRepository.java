package com.hospital.hospitalapi.repository;

import java.util.Optional;

import com.hospital.hospitalapi.domain.user.Account;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    Optional<Account> findOneByEmail(String email);

    Optional<Account> findOneByHash(String hash);
}
