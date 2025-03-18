package com.syncura360.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum BloodType {
    A_Positive("A+"),
    A_Negative("A-"),
    B_Positive("B+"),
    B_Negative("B-"),
    AB_Positive("AB+"),
    AB_Negative("AB-"),
    O_Positive("O+"),
    O_Negative("O-");

    private final String value;

    BloodType(String value) {this.value = value;}

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static BloodType fromValue(String value) {
        for (BloodType type : BloodType.values()) {
            if (type.value.equals(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown blood type: " + value);
    }
}
