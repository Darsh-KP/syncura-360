package com.syncura360.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Enum representing the category of drugs, with values for Drug and Medical Supply.
 *
 * @author Darsh-KP
 */
public enum DrugCategory {
    Drug("Drug"),
    Medical_Supply("Medical Supply");

    private final String value;

    DrugCategory(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static DrugCategory fromValue(String value) {
        for (DrugCategory drugCategory : DrugCategory.values()) {
            if (drugCategory.getValue().equals(value)) {
                return drugCategory;
            }
        }
        throw new IllegalArgumentException("Unknown drug category: " + value);
    }
}
