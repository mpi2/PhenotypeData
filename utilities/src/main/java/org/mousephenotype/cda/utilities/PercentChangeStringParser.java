package org.mousephenotype.cda.utilities;

public class PercentChangeStringParser {

    public static Double getFemalePercentageChange(String token) {
        return getPercentageChange(token, "Female");
    }

    public static Double getMalePercentageChange(String token) {
        return getPercentageChange(token, "Male");
    }

    public static Double getPercentageChange(String token, String target) {
        Double retVal = null;

        String[] sexes = token.split(",");
        for (String sex : sexes) {
            if (sex.contains(target)) {
                try {
                    String[] pieces = sex.split(":");
                    retVal = Double.parseDouble(pieces[1].replaceAll("%", ""));
                } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                    // null statement;
                }
                break;
            }
        }

        return retVal;
    }

}
