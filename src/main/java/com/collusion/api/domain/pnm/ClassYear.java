package com.collusion.api.domain.pnm;

public enum ClassYear {
    FRESHMAN("Freshman"),
    SOPHOMORE("Sophomore"),
    JUNIOR("Junior"),
    SENIOR("Senior"),
    SUPER_SENIOR("Super Senior");

    private final String dbValue;

    ClassYear(String dbValue) {
        this.dbValue = dbValue;
    }

    public String getDbValue() {
        return dbValue;
    }

    public static ClassYear fromDbValue(String value) {
        for (ClassYear y : values()) {
            if (y.dbValue.equals(value)) return y;
        }
        throw new IllegalArgumentException("Unknown class_year: " + value);
    }
}