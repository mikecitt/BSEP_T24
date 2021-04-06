package com.hospital.hospitalapi.web.rest.certificate;

import com.hospital.hospitalapi.configuration.constants.Constants;
import com.hospital.hospitalapi.service.certificate.CertificateService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/certificate")
public class CertificateResource {

  @Autowired
  private CertificateService certificateService;

  @PostMapping("/issue-certificate/{csrId}/{templateName}")
  public ResponseEntity<?> issueCertificate(@PathVariable Long csrId, @PathVariable String templateName) {

    if (!templateName.equals(Constants.CERT_TYPE.ROOT_CERT.toString())
        && !templateName.equals(Constants.CERT_TYPE.LEAF_CERT.toString())) {
      return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    }

    this.certificateService.createCertificate(csrId, Constants.CERT_TYPE.valueOf(templateName));
    return new ResponseEntity<>(null, HttpStatus.OK);
  }
}
