package com.hospital.hospitalapi.repository;

import org.springframework.stereotype.Repository;
import com.hospital.hospitalapi.domain.cert.CertificateSigningRequest;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface CertificateSigningRequestRepository extends JpaRepository<CertificateSigningRequest, String> {
    CertificateSigningRequest findById(Long id);
}
