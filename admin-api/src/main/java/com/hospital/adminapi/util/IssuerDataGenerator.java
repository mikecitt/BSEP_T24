package com.hospital.adminapi.util;

import java.security.PrivateKey;
import java.security.PublicKey;

import com.hospital.adminapi.domain.IssuerData;

import org.bouncycastle.asn1.x500.X500Name;

public class IssuerDataGenerator {

  public static IssuerData generateIssuerData(PrivateKey issuerKey, X500Name name, PublicKey publicKey) {
    return new IssuerData(issuerKey, name, publicKey);
  }
}
