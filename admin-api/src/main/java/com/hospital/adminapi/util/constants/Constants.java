package com.hospital.adminapi.util.constants;

public class Constants {

  public static enum CERT_TYPE {
    ROOT_CERT, INTERMEDIATE_CERT, LEAF_CERT, SERVER_CERT
  };

  public static final Integer ROOT_CERT_DURATION = 10; // znaci 30 godina
  public static final Integer LEAF_CERT_DURATION = 10; // znaci 30 godina

  public static String ROOT_ALIAS = "1";
  public static String PKI_ALIAS = "2";

  public static String GENERATED_CERT_DIRECTORY = "src/main/resources/generatedCerts";

}
