package com.syncura360.model.enums;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class DrugCategoryConvertor implements AttributeConverter<DrugCategory, String> {
    @Override
    public String convertToDatabaseColumn(DrugCategory drugCategory) {
        if (drugCategory == null) return null;
        return drugCategory.getValue();
    }

    @Override
    public DrugCategory convertToEntityAttribute(String dbValue) {
        if (dbValue == null) return null;
        return DrugCategory.fromValue(dbValue);
    }
}