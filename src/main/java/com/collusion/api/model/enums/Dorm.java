package com.collusion.api.model.enums;

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

    private final String pgLabel;

    Dorm(String pgLabel) {
        this.pgLabel = pgLabel;
    }

    public String getPgLabel() {
        return pgLabel;
    }

    public static Dorm fromPgLabel(String label) {
        for (Dorm v : values()) {
            if (v.pgLabel.equals(label)) return v;
        }
        throw new IllegalArgumentException("Unknown dorm label: " + label);
    }
}
