package com.hospital.adminapi.service.certificate.dto;

import java.math.BigInteger;
import java.sql.Timestamp;

import com.hospital.adminapi.domain.Certificate;

import lombok.Data;

@Data
public class CertificateResponseDTO {
    private BigInteger serialNumber;
    private Timestamp startDate;
    private Timestamp endDate;
    private String commonName;
    private boolean revoked;

    public CertificateResponseDTO(Certificate certificate) {
        if(certificate != null) {
            this.serialNumber = certificate.getSerialNumber();
            this.startDate = certificate.getStartDate();
            this.endDate = certificate.getEndDate();
            this.commonName = certificate.getCommonName();
        }
    }
}
