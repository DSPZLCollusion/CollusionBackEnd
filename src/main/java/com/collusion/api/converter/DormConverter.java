package com.collusion.api.converter;

import com.collusion.api.domain.pnm.Dorm;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class DormConverter implements AttributeConverter<Dorm, String> {

    @Override
    public String convertToDatabaseColumn(Dorm attribute) {
        return attribute == null ? null : attribute.getDbValue();
    }

    @Override
    public Dorm convertToEntityAttribute(String dbData) {
        return dbData == null ? null : Dorm.fromDbValue(dbData);
    }
}