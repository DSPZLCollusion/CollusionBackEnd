package com.collusion.api.converter;

import com.collusion.api.domain.pnm.HousingType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class HousingTypeConverter implements AttributeConverter<HousingType, String> {

    @Override
    public String convertToDatabaseColumn(HousingType attribute) {
        return attribute == null ? null : attribute.getDbValue();
    }

    @Override
    public HousingType convertToEntityAttribute(String dbData) {
        return dbData == null ? null : HousingType.fromDbValue(dbData);
    }
}