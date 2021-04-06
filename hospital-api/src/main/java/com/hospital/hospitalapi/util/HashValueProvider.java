package com.hospital.hospitalapi.util;

import java.util.UUID;

public class HashValueProvider {

    public static String generateHash() {
        return UUID.randomUUID().toString();
    }
}
