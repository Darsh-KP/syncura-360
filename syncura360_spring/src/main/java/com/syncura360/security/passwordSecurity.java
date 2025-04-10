package com.syncura360.security;

import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Utility class for password encoding using Argon2id.
 *
 * @author Darsh-KP
 */
public class passwordSecurity {
    private passwordSecurity() {}

    // Argon2id password encoder
    public static PasswordEncoder getPasswordEncoder() {
        return new Argon2PasswordEncoder(16, 16, 1, 262144, 3);
    }
}