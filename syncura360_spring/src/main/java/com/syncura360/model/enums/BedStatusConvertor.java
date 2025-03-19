package com.syncura360.model.enums;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class BedStatusConvertor implements AttributeConverter<BedStatus, String> {
    @Override
    public String convertToDatabaseColumn(BedStatus bedStatus) {
        if (bedStatus == null) return null;
        return bedStatus.getValue();
    }

    @Override
    public BedStatus convertToEntityAttribute(String dbValue) {
        if (dbValue == null) return null;
        return BedStatus.fromValue(dbValue);
    }
}
