package com.hospital.adminapi.service.certificate;

import com.hospital.adminapi.domain.Certificate;
import com.hospital.adminapi.repository.CertificateRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CertificateService {

  @Autowired
  private CertificateRepository certificateRepository;

  public void save(Certificate certificate) {
    certificateRepository.save(certificate);
  }
}
