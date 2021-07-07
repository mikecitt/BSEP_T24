package com.hospital.adminapi.domain;

import java.math.BigInteger;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import lombok.Data;

@Entity
@Data
public class RevokedCertificate {

    @Id
    @GeneratedValue
    private Long id;

    @Column(unique = true, nullable = false)
    private BigInteger serialNumber;

    @Column
    private Timestamp revokingDate;

    public RevokedCertificate(BigInteger serialNumber, Timestamp revokingDate) {
        this.serialNumber = serialNumber;
        this.revokingDate = revokingDate;
    }
}
