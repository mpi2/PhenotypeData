package org.mousephenotype.cda.loads.annotations;

public enum StatisticalResultDataType {

    unidimensional("unidimensional"),
    categorical("categorical"),
    rr_plus("rr_plus");

    StatisticalResultDataType(String name) {
        this.name = name;
    }

    private final String name;
    public String getName(){
        return this.toString();
    }

}
