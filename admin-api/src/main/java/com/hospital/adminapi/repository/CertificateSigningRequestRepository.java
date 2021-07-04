package com.hospital.adminapi.repository;

import com.hospital.adminapi.domain.CertificateSigningRequest;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CertificateSigningRequestRepository extends JpaRepository<CertificateSigningRequest, Long> {

}
