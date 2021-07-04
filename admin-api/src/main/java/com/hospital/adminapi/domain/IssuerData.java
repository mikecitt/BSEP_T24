package com.hospital.adminapi.domain;

import java.security.PrivateKey;

import org.bouncycastle.asn1.x500.X500Name;

import lombok.Data;

@Data
public class IssuerData {

	private X500Name x500name;
	private PrivateKey privateKey;
}
