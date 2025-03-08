package com.syncura360.model.enums;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class TraumaLevelConvertor implements AttributeConverter<TraumaLevel, String> {
    @Override
    public String convertToDatabaseColumn(TraumaLevel traumaLevel) {
        if (traumaLevel == null) return null;
        return traumaLevel.getValue();
    }

    @Override
    public TraumaLevel convertToEntityAttribute(String dbValue) {
        if (dbValue == null) return null;
        return TraumaLevel.fromValue(dbValue);
    }
}
