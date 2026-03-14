package com.collusion.api.domain.pnm;

public enum HousingType {
    ON_CAMPUS("on_campus"),
    OFF_CAMPUS("off_campus");

    private final String dbValue;

    HousingType(String dbValue) {
        this.dbValue = dbValue;
    }

    public String getDbValue() {
        return dbValue;
    }

    public static HousingType fromDbValue(String value) {
        for (HousingType h : values()) {
            if (h.dbValue.equals(value)) return h;
        }
        throw new IllegalArgumentException("Unknown housing_type: " + value);
    }
}