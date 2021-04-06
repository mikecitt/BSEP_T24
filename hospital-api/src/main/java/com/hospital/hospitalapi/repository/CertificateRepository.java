package com.hospital.hospitalapi.repository;

import com.hospital.hospitalapi.domain.cert.Certificate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CertificateRepository extends JpaRepository<Certificate, String> {
    Certificate findBySerialNumber(String serialNumber);
}
