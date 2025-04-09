package com.syncura360.security;

import java.security.SecureRandom;
import java.util.Base64;

/**
 * Utility class to generate a secret key for JWT signing.
 *
 * @author Benjamin Leiby
 */
public class KeyGenerator {
    public static void main (String[] args) {
        System.out.println(generateSecretKey());
    }

    public static String generateSecretKey() {
        SecureRandom random = new SecureRandom();
        byte[] keyBytes = new byte[64]; // for HS512, 64 bytes is recommended
        random.nextBytes(keyBytes);
        return Base64.getEncoder().encodeToString(keyBytes);
    }
}
