package com.syncura360.security;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class passwordSecurity {
    static boolean debug = true;

    private passwordSecurity() {}

    // Method to generate a random salt
    public static String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    // Method to hash the password using SHA-256 and salt
    public static String hashPassword(String password, String salt) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.reset();
            byte[] decodedSalt = Base64.getDecoder().decode(salt);
            digest.update(decodedSalt);
            byte[] hashedBytes = digest.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(hashedBytes);
        } catch (NoSuchAlgorithmException e) {
            if (debug) System.out.println("Couldn't load algorithm for password hashing.");
            return null;
        }
    }

    // Method to verify if the entered password matches the hashed password
    public static boolean verifyPassword(String enteredPassword, String storedPassword, String salt) {
        String hashedEnteredPassword = hashPassword(enteredPassword, salt);
        return hashedEnteredPassword.equals(storedPassword);
    }

    public static void main(String[] args) throws NoSuchAlgorithmException {
        // To Test
        String password = "password123";
        String salt = generateSalt();
        String hashedPassword = hashPassword(password, salt);

        // Verify password
        String enteredPassword = "password123";
        boolean passwordCorrect = verifyPassword(enteredPassword, hashedPassword, salt);

        if (debug) {
            if (passwordCorrect) {
                System.out.println("Password is correct.");
            } else {
                System.out.println("Password is incorrect.");
            }
        }
    }
}