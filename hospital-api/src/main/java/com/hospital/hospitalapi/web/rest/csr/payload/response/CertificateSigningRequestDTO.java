package com.hospital.hospitalapi.web.rest.csr.payload.response;

import lombok.Data;

@Data
public class CertificateSigningRequestDTO {

  private Long id;
  private String csr;
  private String issuerName;
  private String serialNumber;
  private String username;
}
