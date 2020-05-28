package org.mousephenotype.cda.solr.service.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.mousephenotype.cda.dto.LifeStage;
import org.mousephenotype.cda.enumerations.ZygosityType;

import javax.validation.constraints.NotNull;

@Data
public class CombinedObservationKey {
    @NotNull final String alleleSymbol;
    @NotNull final String alleleAccessionId;
    @NotNull final String geneSymbol;
    @NotNull final String geneAccession;
    @NotNull final String parameterStableId;
    @NotNull final String parameterName;
    @NotNull final String procedureStableId;
    @NotNull final String procedureName;
    @NotNull final String pipelineStableId;
    @NotNull final String pipelineName;
    @NotNull final ZygosityType zygosity;
    @NotNull final String phenotypingCenter;
    @NotNull final LifeStage lifeStage;

    @EqualsAndHashCode.Exclude String statisticalMethod;
    @EqualsAndHashCode.Exclude String status;
    @EqualsAndHashCode.Exclude String metadataGroup;

}
