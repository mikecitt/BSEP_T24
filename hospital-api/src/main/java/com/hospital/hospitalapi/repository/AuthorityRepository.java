package com.hospital.hospitalapi.repository;

import java.util.Optional;

import com.hospital.hospitalapi.domain.user.Authority;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorityRepository extends JpaRepository<Authority, String> {

    Optional<Authority> findOneByName(String name);
}
