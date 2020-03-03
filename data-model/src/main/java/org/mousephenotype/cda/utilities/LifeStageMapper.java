package org.mousephenotype.cda.utilities;

import org.mousephenotype.cda.enumerations.LifeStage;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Maps from procedure corresponding live stage
 */
public class LifeStageMapper {

    public static final Set<String> E9_5_PROCEDURES = new HashSet<>(Arrays.asList("GEL_", "EVL_", "HPL_", "EOL_", "EML_", "HEL_", "GPL_"));
    public static final Set<String> E12_5_PROCEDURES = new HashSet<>(Arrays.asList("GEM_", "ELZ_", "GPM_", "EVM_"));
    public static final Set<String> E15_5_PROCEDURES = new HashSet<>(Arrays.asList("EVO", "GPO", "MAA_", "EMO_", "GEO_"));
    public static final Set<String> E18_5_PROCEDURES = new HashSet<>(Arrays.asList("EVP", "EMA", "GEP_", "GPP_"));
    public static final Set<String> LA_PROCEDURES = new HashSet<>(Arrays.asList("LA_"));
    public static final Set<String> IP_PROCEDURES = new HashSet<>(Arrays.asList("IP_"));

    public static LifeStage getLifeStage(String parameterStableId) {
        return getLifeStage(parameterStableId, "postnatal");
    }

    public static LifeStage getLifeStage(String parameterStableId, String developmentalStageName) {
        if (LA_PROCEDURES.stream().anyMatch(parameterStableId::contains)) {
            return LifeStage.LATE_ADULT;
        } else if (IP_PROCEDURES.stream().anyMatch(parameterStableId::contains)) {
            return LifeStage.MIDDLE_AGED_ADULT;
        } else if (E9_5_PROCEDURES.stream().anyMatch(parameterStableId::contains)) {
            return LifeStage.E9_5;
        } else if (E12_5_PROCEDURES.stream().anyMatch(parameterStableId::contains)) {
            return LifeStage.E12_5;
        } else if (E15_5_PROCEDURES.stream().anyMatch(parameterStableId::contains)) {
            return LifeStage.E15_5;
        } else if (E18_5_PROCEDURES.stream().anyMatch(parameterStableId::contains)) {
            return LifeStage.E18_5;
        } else if ( ! developmentalStageName.equals("postnatal")) {
            return LifeStage.getByDisplayName(developmentalStageName);
        } else {
            return LifeStage.EARLY_ADULT;
        }
    }
}
