package com.hospital.hospitalapi.domain.cert;

import javax.persistence.*;

import lombok.Data;

@Entity
@Table(name = "certificate_signing_requests")
@Data
public class CertificateSigningRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "csr", columnDefinition = "BLOB")
    private byte[] csr;

    @Column(name = "issuer_name")
    private String issuerName;

    @Column(name = "serial_number")
    private String serialNumber;

    @Column(name = "username")
    private String username;

    public CertificateSigningRequest() {

    }

    public CertificateSigningRequest(byte[] csr, String issuerName, String username) {
        this.csr = csr;
        this.issuerName = issuerName;
        this.username = username;
    }
}
