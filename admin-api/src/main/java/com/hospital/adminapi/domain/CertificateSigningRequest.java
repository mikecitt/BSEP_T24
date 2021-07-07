package com.hospital.adminapi.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import lombok.Data;

@Data
@Entity
public class CertificateSigningRequest {
  @Id
  @GeneratedValue
  private Long id;

  private String commonName;
  private String organization;
  private String organizationUnit;
  private String country;
  private String locality;
  private String email;
  private String username;

  @Column(columnDefinition = "BLOB")
  private byte[] certificate;
}
