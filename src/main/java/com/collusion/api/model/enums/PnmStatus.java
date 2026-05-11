package com.collusion.api.model.enums;

public enum PnmStatus {

    DELTA("delta"),
    SIGMA("sigma"),
    PHI("phi");

    private final String pgLabel;

    PnmStatus(String pgLabel) {
        this.pgLabel = pgLabel;
    }

    public String getPgLabel() {
        return pgLabel;
    }

    public static PnmStatus fromPgLabel(String label) {
        for (PnmStatus v : values()) {
            if (v.pgLabel.equals(label)) return v;
        }
        throw new IllegalArgumentException("Unknown status label: " + label);
    }
}
