package com.collusion.api.model.enums;

public enum HousingType {

    ON_CAMPUS("on_campus"),
    OFF_CAMPUS("off_campus");

    private final String pgLabel;

    HousingType(String pgLabel) {
        this.pgLabel = pgLabel;
    }

    public String getPgLabel() {
        return pgLabel;
    }

    public static HousingType fromPgLabel(String label) {
        for (HousingType v : values()) {
            if (v.pgLabel.equals(label)) return v;
        }
        throw new IllegalArgumentException("Unknown housing_type label: " + label);
    }
}
