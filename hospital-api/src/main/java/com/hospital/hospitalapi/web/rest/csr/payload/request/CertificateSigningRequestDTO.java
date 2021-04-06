package com.hospital.hospitalapi.web.rest.csr.payload.request;

import lombok.Data;

@Data
public class CertificateSigningRequestDTO {
    private Long id;
    private String commonName;
    private String organization;
    private String locality;
    private String country;
    private String email;
}
