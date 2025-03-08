package com.syncura360.model.enums;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class RoleConvertor implements AttributeConverter<Role, String> {
    @Override
    public String convertToDatabaseColumn(Role role) {
        if (role == null) return null;
        return role.getValue();
    }

    @Override
    public Role convertToEntityAttribute(String dbValue) {
        if (dbValue == null) return null;
        return Role.fromValue(dbValue);
    }
}
