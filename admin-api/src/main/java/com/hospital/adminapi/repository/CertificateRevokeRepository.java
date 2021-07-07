package com.hospital.adminapi.repository;

import java.math.BigInteger;
import java.util.Optional;

import com.hospital.adminapi.domain.RevokedCertificate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CertificateRevokeRepository extends JpaRepository<RevokedCertificate, Integer> {
    Optional<RevokedCertificate> findBySerialNumber(BigInteger serialNumber);
}
