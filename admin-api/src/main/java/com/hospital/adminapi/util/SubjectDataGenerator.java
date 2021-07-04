package com.hospital.adminapi.util;

import java.security.PublicKey;
import java.util.Calendar;
import java.util.Date;

import com.hospital.adminapi.domain.SubjectData;

import org.bouncycastle.asn1.x500.X500Name;

public class SubjectDataGenerator {

  public static SubjectData generateSubjectData(PublicKey publicKey, X500Name name) {
    String sn = "1";

    Calendar endDate = Calendar.getInstance();
    endDate.add(Calendar.YEAR, 10);

    return new SubjectData(publicKey, name, sn, new Date(), endDate.getTime());
  }
}
