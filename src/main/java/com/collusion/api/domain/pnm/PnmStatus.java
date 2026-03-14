package com.collusion.api.domain.pnm;

public enum PnmStatus {
    DELTA("delta"),
    SIGMA("sigma"),
    PHI("phi");

    private final String dbValue;

    PnmStatus(String dbValue) {
        this.dbValue = dbValue;
    }

    public String getDbValue() {
        return dbValue;
    }

    public static PnmStatus fromDbValue(String value) {
        for (PnmStatus s : values()) {
            if (s.dbValue.equals(value)) return s;
        }
        throw new IllegalArgumentException("Unknown status: " + value);
    }
}