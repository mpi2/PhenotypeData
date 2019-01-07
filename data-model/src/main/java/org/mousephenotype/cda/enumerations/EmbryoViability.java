package org.mousephenotype.cda.enumerations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public enum EmbryoViability {

    E9_5("IMPC_EVL_001_001",

            "IMPC_EVL_002_001",
            "IMPC_EVL_007_001",
            "IMPC_EVL_008_001",
            "IMPC_EVL_009_001",

            "IMPC_EVL_010_001",
            "IMPC_EVL_011_001",
            "IMPC_EVL_012_001",
            "IMPC_EVL_013_001",

            "IMPC_EVL_014_001",
            "IMPC_EVL_015_001",
            "IMPC_EVL_016_001",
            "IMPC_EVL_017_001",

            "IMPC_EVL_024_001",
            "IMPC_EVL_026_001",
            "IMPC_EVL_025_001",
            "IMPC_EVL_027_001",

            "IMPC_EVL_018_001",
            "IMPC_EVL_021_001"),

    E12_5("IMPC_EVM_001_001",

            "IMPC_EVM_023_001",
            "IMPC_EVM_004_001",
            "IMPC_EVM_005_001",
            "IMPC_EVM_006_001",

            "IMPC_EVM_007_001",
            "IMPC_EVM_008_001",
            "IMPC_EVM_009_001",
            "IMPC_EVM_010_001",

            "IMPC_EVM_011_001",
            "IMPC_EVM_012_001",
            "IMPC_EVM_013_001",
            "IMPC_EVM_014_001",

            "IMPC_EVM_024_001",
            "IMPC_EVM_026_001",
            "IMPC_EVM_025_001",
            "IMPC_EVM_027_001",

            "IMPC_EVM_015_001",
            "IMPC_EVM_019_001"),

    E14_5("IMPC_EVO_001_001",

            "IMPC_EVO_004_001",
            "IMPC_EVO_005_001",
            "IMPC_EVO_006_001",
            "IMPC_EVO_007_001",

            "IMPC_EVO_008_001",
            "IMPC_EVO_009_001",
            "IMPC_EVO_010_001",
            "IMPC_EVO_011_001",

            "IMPC_EVO_012_001",
            "IMPC_EVO_013_001",
            "IMPC_EVO_014_001",
            "IMPC_EVO_015_001",

            "IMPC_EVO_024_001",
            "IMPC_EVO_026_001",
            "IMPC_EVO_025_001",
            "IMPC_EVO_027_001",

            "IMPC_EVO_016_001",
            "IMPC_EVO_017_001"),

    E18_5("IMPC_EVP_001_001",

            "IMPC_EVP_004_001",
            "IMPC_EVP_023_001",
            "IMPC_EVP_005_001",
            "IMPC_EVP_006_001",

            "IMPC_EVP_007_001",
            "IMPC_EVP_008_001",
            "IMPC_EVP_009_001",
            "IMPC_EVP_010_001",

            "IMPC_EVP_011_001",
            "IMPC_EVP_012_001",
            "IMPC_EVP_013_001",
            "IMPC_EVP_014_001",

            "IMPC_EVP_024_001",
            "IMPC_EVP_026_001",
            "IMPC_EVP_025_001",
            "IMPC_EVP_027_001",

            "IMPC_EVP_015_001",
            "IMPC_EVP_016_001");




    public final String outcome;

    public final String totalEmbryos;
    public final String totalEmbryosWt;
    public final String totalEmbryosHet;
    public final String totalEmbryosHom;

    public final String totalDeadEmbryos;
    public final String totalDeadEmbryosWt;
    public final String totalDeadEmbryosHet;
    public final String totalDeadEmbryosHom;

    public final String totalGrossDefect;
    public final String totalGrossDefectWt;
    public final String totalGrossDefectHet;
    public final String totalGrossDefectHom;

    public final String totalLiveEmbryos;
    public final String totalLiveEmbryosWt;
    public final String totalLiveEmbryosHet;
    public final String totalLiveEmbryosHom;

    public final String reabsorptionNumber;
    public final String averageLitterSize;

    public final List<String> parameterList;




    EmbryoViability(String outcome,

                    String totalEmbryos,
                    String totalEmbryosWt,
                    String totalEmbryosHet,
                    String totalEmbryosHom,

                    String totalDeadEmbryos,
                    String totalDeadEmbryosWt,
                    String totalDeadEmbryosHet,
                    String totalDeadEmbryosHom,

                    String totalGrossDefect,
                    String totalGrossDefectWt,
                    String totalGrossDefectHet,
                    String totalGrossDefectHom,

                    String totalLiveEmbryos,
                    String totalLiveEmbryosWt,
                    String totalLiveEmbryosHet,
                    String totalLiveEmbryosHom,

                    String reabsorptionNumber,
                    String averageLitterSize) {

        this.outcome = outcome;

        this.totalEmbryos = totalEmbryos;
        this.totalEmbryosWt = totalEmbryosWt;
        this.totalEmbryosHet = totalEmbryosHet;
        this.totalEmbryosHom = totalEmbryosHom;

        this.totalDeadEmbryos = totalDeadEmbryos;
        this.totalDeadEmbryosWt = totalDeadEmbryosWt;
        this.totalDeadEmbryosHet = totalDeadEmbryosHet;
        this.totalDeadEmbryosHom = totalDeadEmbryosHom;

        this.totalGrossDefect = totalGrossDefect;
        this.totalGrossDefectWt = totalGrossDefectWt;
        this.totalGrossDefectHet = totalGrossDefectHet;
        this.totalGrossDefectHom = totalGrossDefectHom;

        this.totalLiveEmbryos = totalLiveEmbryos;
        this.totalLiveEmbryosWt = totalLiveEmbryosWt;
        this.totalLiveEmbryosHet = totalLiveEmbryosHet;
        this.totalLiveEmbryosHom = totalLiveEmbryosHom;

        this.reabsorptionNumber = reabsorptionNumber;
        this.averageLitterSize = averageLitterSize;


        List<String> parameterList = new ArrayList<String>();

        parameterList.add(totalEmbryos);
        parameterList.add(totalEmbryosWt);
        parameterList.add(totalEmbryosHet);
        parameterList.add(totalEmbryosHom);

        parameterList.add(totalDeadEmbryos);
        parameterList.add(totalDeadEmbryosWt);
        parameterList.add(totalDeadEmbryosHet);
        parameterList.add(totalDeadEmbryosHom);

        parameterList.add(totalGrossDefect);
        parameterList.add(totalGrossDefectWt);
        parameterList.add(totalGrossDefectHet);
        parameterList.add(totalGrossDefectHom);

        parameterList.add(totalLiveEmbryos);
        parameterList.add(totalLiveEmbryosWt);
        parameterList.add(totalLiveEmbryosHet);
        parameterList.add(totalLiveEmbryosHom);

        parameterList.add(reabsorptionNumber);
        parameterList.add(averageLitterSize);

        this.parameterList = Collections.unmodifiableList(parameterList);

    }






    public String getTotalEmbryos() {

        return totalEmbryos;
    }

    public String getTotalEmbryosWt() {

        return totalEmbryosWt;
    }

    public String getTotalEmbryosHet() {

        return totalEmbryosHet;
    }

    public String getTotalEmbryosHom() {

        return totalEmbryosHom;
    }

    public String getTotalDeadEmbryos() {

        return totalDeadEmbryos;
    }

    public String getTotalDeadEmbryosWt() {

        return totalDeadEmbryosWt;
    }

    public String getTotalDeadEmbryosHet() {

        return totalDeadEmbryosHet;
    }

    public String getTotalDeadEmbryosHom() {

        return totalDeadEmbryosHom;
    }

    public String getTotalGrossDefect() {

        return totalGrossDefect;
    }

    public String getTotalGrossDefectWt() {

        return totalGrossDefectWt;
    }

    public String getTotalGrossDefectHet() {

        return totalGrossDefectHet;
    }

    public String getTotalGrossDefectHom() {

        return totalGrossDefectHom;
    }

    public String getTotalLiveEmbryos() {

        return totalLiveEmbryos;
    }

    public String getTotalLiveEmbryosWt() {

        return totalLiveEmbryosWt;
    }

    public String getTotalLiveEmbryosHet() {

        return totalLiveEmbryosHet;
    }

    public String getTotalLiveEmbryosHom() {

        return totalLiveEmbryosHom;
    }

    public String getReabsorptionNumber() {

        return reabsorptionNumber;
    }

    public String getAverageLitterSize() {

        return averageLitterSize;
    }


}
