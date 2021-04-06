package com.hospital.hospitalapi.domain.cert;

import java.security.PrivateKey;

import org.bouncycastle.asn1.x500.X500Name;

import lombok.Data;

@Data
public class IssuerData {

    private X500Name x500name;
    private PrivateKey privateKey;

    public IssuerData(PrivateKey privateKey, X500Name x500name) {
        this.privateKey = privateKey;
        this.x500name = x500name;
    }

    public IssuerData() {
    }
}
