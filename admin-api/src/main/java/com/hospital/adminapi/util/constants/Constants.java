package com.hospital.adminapi.util.constants;

public class Constants {

  public static enum CERT_TYPE {
    ROOT_CERT, INTERMEDIATE_CERT, LEAF_CERT, SERVER_CERT
  };

  public static final Integer ROOT_CERT_DURATION = 120; // 10 godina
  public static final Integer LEAF_CERT_DURATION = 12; // 1 godinu

  public static String ROOT_ALIAS = "0";
}
