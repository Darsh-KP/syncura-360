package com.syncura360.security;

import java.security.SecureRandom;
import java.util.Base64;


public class KeyGenerator {

    public static void main (String[] args) {
    }

    public static String generateSecretKey() {
        SecureRandom random = new SecureRandom();
        byte[] keyBytes = new byte[64]; // for HS512, 64 bytes is recommended
        random.nextBytes(keyBytes);
        return Base64.getEncoder().encodeToString(keyBytes);
    }

}
