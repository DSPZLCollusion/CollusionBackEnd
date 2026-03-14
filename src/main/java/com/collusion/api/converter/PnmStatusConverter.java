package com.collusion.api.converter;

import com.collusion.api.domain.pnm.PnmStatus;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class PnmStatusConverter implements AttributeConverter<PnmStatus, String> {

    @Override
    public String convertToDatabaseColumn(PnmStatus attribute) {
        return attribute == null ? null : attribute.getDbValue();
    }

    @Override
    public PnmStatus convertToEntityAttribute(String dbData) {
        return dbData == null ? null : PnmStatus.fromDbValue(dbData);
    }
}