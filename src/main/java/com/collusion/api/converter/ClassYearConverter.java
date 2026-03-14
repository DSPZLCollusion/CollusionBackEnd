package com.collusion.api.converter;

import com.collusion.api.domain.pnm.ClassYear;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class ClassYearConverter implements AttributeConverter<ClassYear, String> {

    @Override
    public String convertToDatabaseColumn(ClassYear attribute) {
        return attribute == null ? null : attribute.getDbValue();
    }

    @Override
    public ClassYear convertToEntityAttribute(String dbData) {
        return dbData == null ? null : ClassYear.fromDbValue(dbData);
    }
}