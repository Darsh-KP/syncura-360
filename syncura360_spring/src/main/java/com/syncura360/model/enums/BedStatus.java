package com.syncura360.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Enum representing the status of a bed, with values for Vacant, Occupied, and Under Maintenance.
 *
 * @author Darsh-KP
 */
public enum BedStatus {
    Vacant("Vacant"),
    Occupied("Occupied"),
    Under_Maintenance("Under Maintenance");

    private final String value;

    BedStatus(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static BedStatus fromValue(String value) {
        for (BedStatus bedStatus : BedStatus.values()) {
            if (bedStatus.getValue().equals(value)) {
                return bedStatus;
            }
        }
        throw new IllegalArgumentException("Unknown bed status: " + value);
    }
}
