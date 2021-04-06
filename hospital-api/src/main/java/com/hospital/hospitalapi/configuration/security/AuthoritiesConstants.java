package com.hospital.hospitalapi.configuration.security;

public class AuthoritiesConstants {

    public static final String DOCTOR = "DOCTOR";

    public static final String ADMIN = "ADMIN";

    public static final String SUPER_ADMIN = "SUPER_ADMIN";

    public static final String AUTH_DOCTOR = "hasAuthority('DOCTOR')";

    public static final String AUTH_ADMIN = "hasAuthority('ADMIN')";

    public static final String AUTH_SUPER_ADMIN = "hasAuthority('SUPER_ADMIN')";

    public AuthoritiesConstants() {

    }
}
