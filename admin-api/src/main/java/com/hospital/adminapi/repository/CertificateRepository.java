package com.hospital.adminapi.repository;

import java.util.Optional;

import com.hospital.adminapi.domain.Certificate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CertificateRepository extends JpaRepository<Certificate, Long> {
    Optional<Certificate> findBySerialNumber(Long serialNumber);
}
