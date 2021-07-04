package com.hospital.adminapi.service.csr.dto;

import com.hospital.adminapi.domain.CertificateSigningRequest;

import lombok.Data;

@Data
public class CertificateSigningRequestDTO {
    private Long id;
    private String commonName;
    private String organization;
    private String organizationUnit;
    private String country;
    private String locality;
    private String email;

    public CertificateSigningRequestDTO(CertificateSigningRequest csr) {
        this.id = csr.getId();
        this.commonName = csr.getCommonName();
        this.organization = csr.getOrganization();
        this.organizationUnit = csr.getOrganizationUnit();
        this.country = csr.getCountry();
        this.email = csr.getEmail();
        this.locality = csr.getLocality();
    }
}
