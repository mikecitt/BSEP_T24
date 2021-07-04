package com.hospital.adminapi.service;

import com.hospital.adminapi.domain.CertificateSigningRequest;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.springframework.stereotype.Service;

@Service
public class CertificateSigningRequestService {

  public insert(byte[] certificate) {

    PKCS10CertificationRequest csr = this.extractCertificationRequest(certificate);

    X500Name x500Name = csr.getSubject();

    String commonName = getField(x500Name, BCStyle.CN);
    String lastName = getField(x500Name, BCStyle.SURNAME);
    String firstName = getField(x500Name, BCStyle.GIVENNAME);
    String organization = getField(x500Name, BCStyle.O);
    String organizationUnit = getField(x500Name, BCStyle.OU);
    String country = getField(x500Name, BCStyle.C);
    String email = getField(x500Name, BCStyle.E);
    String serialNumber = getField(x500Name, BCStyle.SERIALNUMBER);
    String locality = getField(x500Name, BCStyle.L);
    
    CertificateSigningRequest csr_pom = new CertificateSignRequest(
            commonName,
            lastName,
            firstName,
            organization,
            organizationUnit,
            country,
            locality,
            email,
            0,
            serialNumber,
            req
    );



    return certificateSignRequestRepository.save(
            csr_pom);
  }
}
