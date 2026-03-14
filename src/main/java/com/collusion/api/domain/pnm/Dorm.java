package com.collusion.api.domain.pnm;

public enum Dorm {
    SPEED("Speed"),
    BSB("BSB"),
    BLUMBERG("Blumberg"),
    MEES("Mees"),
    DEMING("Deming"),
    SCHARPENBERG("Scharpenberg"),
    LAKESIDE("Lakeside"),
    PERCOPO("Percopo"),
    APARTMENTS_WEST("Apartments West"),
    APARTMENTS_EAST("Apartments East"),
    TBA("TBA");

    private final String dbValue;

    Dorm(String dbValue) {
        this.dbValue = dbValue;
    }

    public String getDbValue() {
        return dbValue;
    }

    public static Dorm fromDbValue(String value) {
        for (Dorm d : values()) {
            if (d.dbValue.equals(value)) return d;
        }
        throw new IllegalArgumentException("Unknown dorm: " + value);
    }
}