package org.mousephenotype.cda.solr.service.dto;

public class LifeStageMapper {

    public static String getLifeStage(String parameterStableId, ObservationDTO obs) {
        String developmentalStageName=obs.getDevelopmentalStageName();


        if (parameterStableId.contains("LA_")) {
            return "Late adult";
        } else if (parameterStableId.contains("IP_")) {
            return "Middle aged adult";
        } else if (parameterStableId.contains("GEL_")|| parameterStableId.contains("EVL_")|| parameterStableId.contains("HPL_")|| parameterStableId.contains("EOL_")|| parameterStableId.contains("EML_")|| parameterStableId.contains("HEL_")|| parameterStableId.contains("GPL_")) {
            return "E9.5";
        } else if (parameterStableId.contains("GEM_")|| parameterStableId.contains("ELZ_")|| parameterStableId.contains("GPM_") || parameterStableId.contains("EVM_")) {
            return "E12.5";
        } else if (parameterStableId.contains("EVO")|| parameterStableId.contains("GPO")|| parameterStableId.contains("MAA_") || parameterStableId.contains("EMO_") || parameterStableId.contains("GEO_")) {
            return "E15.5";
        } else if (parameterStableId.contains("EVP")|| parameterStableId.contains("EMA")|| parameterStableId.contains("GEP_") || parameterStableId.contains("GPP_")) {
            return "E18.5";
        } else if ( ! developmentalStageName.equals("postnatal")) {
            return developmentalStageName;
        }

        else {
            return "Early adult";
        }
    }
}
