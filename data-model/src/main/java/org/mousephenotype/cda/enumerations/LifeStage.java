package org.mousephenotype.cda.enumerations;

public enum LifeStage {
    E9_5("E9.5"),
    E12_5("E12.5"),
    E15_5("E15.5"),
    E18_5("E18.5"),
    EARLY_ADULT("Early adult"),
    MIDDLE_AGED_ADULT("Middle aged adult"),
    LATE_ADULT("Late adult");

    private final String name;

    LifeStage(String name) {
        this.name = name;
    }

    public String getName() {
        return this.toString();
    }
}