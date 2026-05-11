package com.collusion.api.model.enums;

import lombok.Getter;

@Getter
public enum ClassYear {

    FRESHMAN("Freshman"),
    SOPHOMORE("Sophomore"),
    JUNIOR("Junior"),
    SENIOR("Senior"),
    SUPER_SENIOR("Super Senior");

    private final String pgLabel;

    ClassYear(String pgLabel) {
        this.pgLabel = pgLabel;
    }

    public static ClassYear fromPgLabel(String label) {
        for (ClassYear v : values()) {
            if (v.pgLabel.equals(label)) return v;
        }
        throw new IllegalArgumentException("Unknown class_year label: " + label);
    }
}