package com.syncura360.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Enum representing user roles with values for Doctor, Nurse, Admin, and Super Admin.
 *
 * @author Darsh-KP
 */
public enum Role {
    Doctor("Doctor"),
    Nurse("Nurse"),
    Admin("Admin"),
    Super_Admin("Super Admin");

    private final String value;

    Role(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static Role fromValue(String value) {
        for (Role role : Role.values()) {
            if (role.value.equals(value)) {
                return role;
            }
        }
        throw new IllegalArgumentException("Unknown role: " + value);
    }
}