package com.syncura360.model.enums;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Converter for {@link BloodType} enum to its String representation for database storage and vice versa.
 *
 * @author Darsh-KP
 */
@Converter(autoApply = true)
public class BloodTypeConvertor implements AttributeConverter<BloodType, String> {
    @Override
    public String convertToDatabaseColumn(BloodType type) {
        if (type == null) return null;
        return type.getValue();
    }

    @Override
    public BloodType convertToEntityAttribute(String dbValue) {
        if (dbValue == null) return null;
        return BloodType.fromValue(dbValue);
    }
}
