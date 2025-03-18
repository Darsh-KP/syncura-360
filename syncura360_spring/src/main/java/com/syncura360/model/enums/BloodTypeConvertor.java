package com.syncura360.model.enums;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

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
        return BloodType.valueOf(dbValue);
    }
}
