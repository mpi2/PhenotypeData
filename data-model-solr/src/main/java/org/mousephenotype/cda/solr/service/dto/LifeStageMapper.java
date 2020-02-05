package org.mousephenotype.cda.solr.service.dto;

public class LifeStageMapper {

    public static String getLifeStage(String parameterStableId) {
        String developmentalStageName=null;
        if (parameterStableId.contains("LA_")) {
            return "Late adult";
        } else if (parameterStableId.contains("IP_")) {
            return "Middle aged adult";
        } else if ( ! developmentalStageName.equals("postnatal")) {
            return developmentalStageName;
        } else {
            return "Early adult";
        }
    }
}
