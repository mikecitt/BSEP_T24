package com.hospital.hospitalapi.configuration.constants;

public class Constants {
    public static final String ROOT_ALIAS = "1";
    public static final Integer ROOT_CERT_DURATION = 365;
    public static final Integer LEAF_CERT_DURATION = 30;

    public static enum CERT_TYPE {
        ROOT_CERT, LEAF_CERT
    };
}
