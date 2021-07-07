package com.hospital.adminapi.domain;

import java.security.PrivateKey;
import java.security.PublicKey;

import org.bouncycastle.asn1.x500.X500Name;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class IssuerData {

	private PrivateKey privateKey;
	private X500Name x500name;
	private PublicKey publicKey;

	public IssuerData(PrivateKey privateKey, X500Name x500name) {
        this.privateKey = privateKey;
        this.x500name = x500name;
    }
}
