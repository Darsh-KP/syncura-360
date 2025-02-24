package com.syncura360.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum TraumaLevel {
    Level_I("Level I"),
    Level_II("Level II"),
    Level_III("Level III"),
    Level_IV("Level IV"),
    Level_V("Level V");

    private final String value;

    TraumaLevel(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static TraumaLevel fromValue(String value) {
        for (TraumaLevel level : TraumaLevel.values()) {
            if (level.getValue().equals(value)) {
                return level;
            }
        }
        throw new IllegalArgumentException("Unknown trauma level: " + value);
    }
}