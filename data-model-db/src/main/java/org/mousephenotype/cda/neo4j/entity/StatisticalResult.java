package org.mousephenotype.cda.neo4j.entity;

import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.Set;

/**
 * Created by ckchen on 14/03/2017.
 */
@NodeEntity
public class StatisticalResult {

    @GraphId
    Long id;

    private String ResourceName;
    private String ResourceFullname;
    private int resourceId;
    private String projectName;
    private String phenotypingCenter;
    private String colonyId;
    private String strainName;
    private String strainAccessionId;
    private String sex;
    private String zygosity;
    private String controlSelectionMethod;
    private String dependentVariable;
    private String metadataGroup;
    private String dataFrame;
    private int externalDbId;
    private int projectId;

    // Information; about the raw data
    private int controlBiologicalModelId;
    private int mutantBiologicalModelId;
    private int maleControlCount;
    private int maleMutantCount;
    private int femaleControlCount;
    private int femaleMutantCount;
    private double maleControlMean;
    private double maleMutantMean;
    private double femaleControlMean;
    private double femaleMutantMean;

    //Information; about the calculation
    private String workflow;
    private String statisticalMethod;
    private String status;
    private String additionalInformation;
    private String rawOutput;
    private double pValue;
    private double effectSize;

    //Reference; Range Plus statistics details
    private double genotypePvalueLowVsNormalHigh;
    private double genotypePvalueLowNormalVsHigh;
    private double genotypeEffectSizeLowVsNormalHigh;
    private double genotypeEffectSizeLowNormalVsHigh;
    private double femalePvalueLowVsNormalHigh;
    private double femalePvalueLowNormalVsHigh;
    private double femaleEffectSizeLowVsNormalHigh;
    private double femaleEffectSizeLowNormalVsHigh;
    private double malePvalueLowVsNormalHigh;
    private double malePvalueLowNormalVsHigh;
    private double maleEffectSizeLowVsNormalHigh;
    private double maleEffectSizeLowNormalVsHigh;

    //Fisher; exact statistics details
    private String categories;
    private double categoricalPValue;
    private double categoricalEffectSize;

    //MM;/LM statistic details
    private boolean batchSignificant;
    private boolean varianceSignificant;
    private double nullTestPValue;
    private double genotypeEffectPValue;
    private double genotypeEffectStderrEstimate;
    private double genotypeEffectParameterEstimate;
    private String malePercentageChange;
    private String femalePercentageChange;
    private double sexEffectPValue;
    private double sexEffectStderrEstimate;
    private double sexEffectParameterEstimate;
    private double weightEffectPValue;
    private double weightEffectStderrEstimate;
    private double weightEffectParameterEstimate;
    private String group_1Genotype;
    private double group_1ResidualsNormalityTest;
    private String group_2Genotype;
    private double group_2ResidualsNormalityTest;
    private double blupsTest;
    private double rotatedResidualsTest;
    private double interceptEstimate;
    private double interceptEstimateStderrEstimate;
    private boolean interactionSignificant;
    private double interactionEffectPValue;
    private double femaleKoEffectPValue;
    private double femaleKoEffectStderrEstimate;
    private double femaleKoParameterEstimate;
    private double maleKoEffectPValue;
    private double maleKoEffectStderrEstimate;
    private double maleKoParameterEstimate;
    private String classificationTag;
    private String phenotypeSex;
    private String lifeStageAcc;
    private String lifeStageName;
    private boolean significant;


    @Relationship(type="GENE", direction=Relationship.OUTGOING)
    private Gene gene;

    @Relationship(type="ALLELE", direction=Relationship.OUTGOING)
    private Allele allele;

    @Relationship(type="MP", direction=Relationship.OUTGOING)
    private Mp mp;

    @Relationship(type="MALE_MP", direction=Relationship.OUTGOING)
    private Mp maleMp;

    @Relationship(type="FEMALE_MP", direction=Relationship.OUTGOING)
    private Mp femaleMp;

    @Relationship(type="MP_OPTIONS", direction=Relationship.OUTGOING)
    private Set<Mp> mpOptions;

    @Relationship(type="PIPELINE", direction=Relationship.OUTGOING)
    private Pipeline pipeline;

    @Relationship(type="PROCEDURE", direction=Relationship.OUTGOING)
    private Procedure procedure;

    @Relationship(type="PARAMETER", direction=Relationship.OUTGOING)
    private Parameter parameter;

}
