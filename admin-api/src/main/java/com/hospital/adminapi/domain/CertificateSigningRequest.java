package com.hospital.adminapi.domain;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import lombok.Data;

@Data
public class CertificateSigningRequest {
  @Id
  @GeneratedValue
  private Long id;

  @Column(unique = true)
  private String commonName;
  private String organization;
  private String organizationUnit;
  private String country;
  private String locality;
  private String email;

  @Column(columnDefinition = "BLOB")
  private byte[] certificate;
}
