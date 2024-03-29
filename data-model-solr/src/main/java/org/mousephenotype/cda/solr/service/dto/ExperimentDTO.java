/*******************************************************************************
 * Copyright 2015 EMBL - European Bioinformatics Institute
 *
 * Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 *******************************************************************************/
package org.mousephenotype.cda.solr.service.dto;

import org.mousephenotype.cda.enumerations.ControlStrategy;
import org.mousephenotype.cda.enumerations.ObservationType;
import org.mousephenotype.cda.enumerations.SexType;
import org.mousephenotype.cda.enumerations.ZygosityType;

import java.sql.SQLException;
import java.util.*;

public class ExperimentDTO {

    private String                            experimentId;
    private String                            metadataGroup;
    private List<String>                      metadata;
    private String                            parameterStableId;
    private String                            procedureStableId;
    private String                            procedureName;
    private String                            pipelineStableId;
    private ObservationType                   observationType;
    private String                            organisation;
    private String                            strain;
    private String                            geneMarker;
    private Set<ZygosityType>                 zygosities;
    private Set<SexType>                      sexes;
    private ControlStrategy                   controlSelectionStrategy;
    private List<StatisticalResultDTO>        results;
    private String                            alleleSymbol;
    private String                            geneticBackgtround;

    private Set<ObservationDTO> homozygoteMutants;
    private Set<ObservationDTO> heterozygoteMutants;
    private Set<ObservationDTO> hemizygoteMutants;

    private Set<ObservationDTO> controls;
    private String                controlBiologicalModelId;
    private String                experimentalBiologicalModelId;
    private Set<ObservationDTO> maleControls;
    private Set<ObservationDTO> femaleControls;
    private String              alleleAccession;
    private String              markerAccession;

    private String statisticalResultUrl;
    private String genotypePhenotypeUrl;
    private String DataPhenStatFormatUrl;

    public List<String> getTabbedToString() throws SQLException {

        List<String> rows = new ArrayList<>();
        boolean headerAdded = false;

        // Iterate through the sets in this order:
        //   homozygoteMutants, heterozygoteMutants, hemizygoteMutants, controls
        //
        for (Set<ObservationDTO> data : Arrays.asList(homozygoteMutants, heterozygoteMutants, hemizygoteMutants, controls)) {
            for (ObservationDTO obs : data) {

                if ( ! headerAdded) {
                    rows.add(obs.getTabbedFields());
                    headerAdded = true;
                }

                rows.add(obs.tabbedToString());
            }
        }

        return rows;
    }

    @Override
	public String toString() {
		return "ExperimentDTO [experimentId=" + experimentId + ", metadataGroup=" + metadataGroup + ", metadata="
				+ metadata + ", parameterStableId=" + parameterStableId + ", procedureStableId=" + procedureStableId
				+ ", procedureName=" + procedureName + ", pipelineStableId=" + pipelineStableId + ", observationType="
				+ observationType + ", organisation=" + organisation + ", strain=" + strain + ", geneMarker="
				+ geneMarker + ", zygosities=" + zygosities + ", sexes=" + sexes + ", controlSelectionStrategy="
				+ controlSelectionStrategy + ", results=" + results + ", alleleSymobl=" + alleleSymbol
				+ ", geneticBackgtround=" + geneticBackgtround + ", homozygoteMutants=" + homozygoteMutants
				+ ", heterozygoteMutants=" + heterozygoteMutants + ", hemizygoteMutants=" + hemizygoteMutants
				+ ", controls=" + controls + ", controlBiologicalModelId=" + controlBiologicalModelId
				+ ", experimentalBiologicalModelId=" + experimentalBiologicalModelId + ", maleControls=" + maleControls
				+ ", femaleControls=" + femaleControls + ", alleleAccession=" + alleleAccession + ", markerAccession=" + markerAccession
				+ ", statisticalResultUrl=" + statisticalResultUrl + ", genotypePhenotypeUrl=" + genotypePhenotypeUrl
				+ ", DataPhenStatFormatUrl=" + DataPhenStatFormatUrl + "]";
	}

	public Set<String> getCategories() {
        Set<String> categorieSet = new TreeSet<>();
        for (ObservationDTO ob : controls) {
            categorieSet.add(ob.getCategory());
        }
        for (ObservationDTO ob : homozygoteMutants) {
            categorieSet.add(ob.getCategory());
        }
        for (ObservationDTO ob : heterozygoteMutants) {
            categorieSet.add(ob.getCategory());
        }
        return categorieSet;
    }

    public ControlStrategy getControlSelectionStrategy() {
		return controlSelectionStrategy;
	}
    public void setControlSelectionStrategy(ControlStrategy controlSelectionStrategy) {
		this.controlSelectionStrategy = controlSelectionStrategy;
	}


	public String getAlleleSymbol() {
		return alleleSymbol;
	}


	public void setAlleleSymbol(String alleleSymbol) {
		this.alleleSymbol = alleleSymbol;
	}

    public String getMarkerAccession() {
        return markerAccession;
    }

    public void setMarkerAccession(String markerAccession) {
        this.markerAccession = markerAccession;
    }

    public String getGeneticBackgtround() {
		return geneticBackgtround;
	}


	public void setGeneticBackgtround(String geneticBackgtround) {
		this.geneticBackgtround = geneticBackgtround;
	}


	public void setHemizygoteMutants(Set<ObservationDTO> hemizygoteMutants) {
		this.hemizygoteMutants = hemizygoteMutants;
	}


	public int getControlSampleSizeFemale() {
        return this.getFemaleControls().size();
    }

    public int getControlSampleSizeMale() {
        return this.getMaleControls().size();
    }

    public Set<ObservationDTO> getFemaleControls() {
        return this.getControls(SexType.female);
    }

    public Set<ObservationDTO> getMaleControls() {
        return this.getControls(SexType.male);
    }

    public Set<ObservationDTO> getControls(SexType sex) {

        Set<ObservationDTO> controls = new HashSet<>();

        if (femaleControls == null || maleControls == null) {
            femaleControls = new HashSet<>();
            maleControls = new HashSet<>();
            for (ObservationDTO control : this.getControls()) {

                if (SexType.valueOf(control.getSex()).equals(SexType.female)) {
                    femaleControls.add(control);
                } else {
                    maleControls.add(control);
                }
            }
        }
        if (sex.equals(SexType.female)) {
            controls = femaleControls;
        } else {
            controls = maleControls;
        }

        return controls;
    }

    public Set<ObservationDTO> getMutants() {
    	Set<ObservationDTO> allMutants = new HashSet<>(homozygoteMutants);
    	allMutants.addAll(hemizygoteMutants);
    	allMutants.addAll(heterozygoteMutants);
    	return allMutants;
    }

    public Set<ObservationDTO> getMutants(SexType sex, ZygosityType zyg) {
        Set<ObservationDTO> mutantsDtos = new HashSet<>();
        Set<ObservationDTO> mutantDtosForSex = new HashSet<>();
        if (zyg == null || zyg.equals(ZygosityType.homozygote)) {
            mutantsDtos = this.getHomozygoteMutants();
        }
        if (zyg == null || zyg.equals(ZygosityType.heterozygote)) {
            mutantsDtos = this.getHeterozygoteMutants();
        }
        if (zyg == null || zyg.equals(ZygosityType.hemizygote)) {
            mutantsDtos = this.getHemizygoteMutants();
        }

        for (ObservationDTO mutant : mutantsDtos) {
            if (sex == null || sex.equals(SexType.not_considered) || sex.equals(SexType.getByDisplayName(mutant.getSex()))) {
                mutantDtosForSex.add(mutant);
            }
        }
        return mutantDtosForSex;
    }

    // *************************** Generated *************************** //

    public ObservationType getObservationType() {
        return observationType;
    }

    public void setObservationType(ObservationType observationType) {
        this.observationType = observationType;
    }

    public String getControlBiologicalModelId() {
        return controlBiologicalModelId;
    }

    public void setControlBiologicalModelId(String controlBiologicalModelId) {
        this.controlBiologicalModelId = controlBiologicalModelId;
    }

    public String getExperimentalBiologicalModelId() {
        return experimentalBiologicalModelId;
    }

    public void setExperimentalBiologicalModelId(
            String experimentalBiologicalModelId) {
        this.experimentalBiologicalModelId = experimentalBiologicalModelId;
    }

    public String getPipelineStableId() {
        return pipelineStableId;
    }

    public void setPipelineStableId(String pipelineStableId) {
        this.pipelineStableId = pipelineStableId;
    }

    public String getExperimentId() {
        return experimentId;
    }

    /**
     * @param experimentId the experimentId to set
     */
    public void setExperimentId(String experimentId) {
        this.experimentId = experimentId;
    }

    /**
     * @return the parameterStableId
     */
    public String getParameterStableId() {
        return parameterStableId;
    }

	/**
	 * @return the procedureName
	 */
	public String getProcedureName() {

		return procedureName;
	}

	/**
	 * @param procedureName the procedureName to set
	 */
	public void setProcedureName(String procedureName) {

		this.procedureName = procedureName;
	}

    public String getStatisticalResultUrl() {
        return statisticalResultUrl;
    }

    public void setStatisticalResultUrl(String statisticalResultUrl) {
        this.statisticalResultUrl = statisticalResultUrl;
    }

    public String getGenotypePhenotypeUrl() {
        return genotypePhenotypeUrl;
    }

    public void setGenotypePhenotypeUrl(String genotypePhenotypeUrl) {
        this.genotypePhenotypeUrl = genotypePhenotypeUrl;
    }

    public String getDataPhenStatFormatUrl() {
        return DataPhenStatFormatUrl;
    }

    public void setDataPhenStatFormatUrl(String dataPhenStatFormatUrl) {
        DataPhenStatFormatUrl = dataPhenStatFormatUrl;
    }

    /**
     * @param parameterStableId the parameterStableId to set
     */
    public void setParameterStableId(String parameterStableId) {
        this.parameterStableId = parameterStableId;
    }

    /**
     * @return the organisation
     */
    public String getOrganisation() {
        return organisation;
    }

    /**
     * @param organisation the organisation to set
     */
    public void setOrganisation(String organisation) {
        this.organisation = organisation;
    }

    /**
     * @return the strain
     */
    public String getStrain() {
        return strain;
    }

    /**
     * @param strain the strain to set
     */
    public void setStrain(String strain) {
        this.strain = strain;
    }

    /**
     * @return the geneMarker
     */
    public String getGeneMarker() {
        return geneMarker;
    }

    /**
     * @param geneMarker the geneMarker to set
     */
    public void setGeneMarker(String geneMarker) {
        this.geneMarker = geneMarker;
    }

    /**
     * @return the zygosity
     */
    public Set<ZygosityType> getZygosities() {
        if(zygosities != null) {
            return zygosities;
        } else {
            return new HashSet<>();
        }
    }


    public void setZygosities(Set<ZygosityType> zygosities) {
        this.zygosities = zygosities;
    }

    /**
     * @return the sexes
     */
    public Set<SexType> getSexes() {
        return sexes;
    }

    /**
     * @param sexes the sexes to set
     */
    public void setSexes(Set<SexType> sexes) {
        this.sexes = sexes;
    }

    /**
     * @return the result
     */
    public List<StatisticalResultDTO> getResults() {
        return results;
    }

    public void setResults(List<StatisticalResultDTO> results) {
        this.results = results;
    }

    /**
     * @return the homozygoteMutants
     */
    public Set<ObservationDTO> getHomozygoteMutants() {
        return homozygoteMutants;
    }

    public Set<ObservationDTO> getHemizygoteMutants() {
        return hemizygoteMutants;
    }

    /**
     * @param homozygoteMutants the homozygoteMutants to set
     */
    public void setHomozygoteMutants(Set<ObservationDTO> homozygoteMutants) {
        this.homozygoteMutants = homozygoteMutants;
    }

    /**
     * @return the heterozygoteMutants
     */
    public Set<ObservationDTO> getHeterozygoteMutants() {
        return heterozygoteMutants;
    }

    /**
     * @param heterozygoteMutants the heterozygoteMutants to set
     */
    public void setHeterozygoteMutants(Set<ObservationDTO> heterozygoteMutants) {
        this.heterozygoteMutants = heterozygoteMutants;
    }

    /**
     * @param hemizygote Mutants the hemizygoteMutants to set
     */
    public void setHemizygoteMutants(HashSet<ObservationDTO> hemizygote) {
        this.hemizygoteMutants = hemizygote;
    }

    /**
     * @return the controls
     */
    public Set<ObservationDTO> getControls() {

        return controls != null ? controls : new HashSet<>();
    }

    /**
     * @param controls the controls to set
     */
    public void setControls(Set<ObservationDTO> controls) {
        this.controls = controls;
    }

    /**
     * @return the metadataGroup
     */
    public String getMetadataGroup() {
        return metadataGroup;
    }

    /**
     * @param metadataGroup the metadataGroup to set
     */
    public void setMetadataGroup(String metadataGroup) {
        this.metadataGroup = metadataGroup;
    }

    /**
     * @return the metadata
     */
    public List<String> getMetadata() {
        return metadata;
    }
    public String getMetadataHtml() {

    	String html = "";
    	if (metadataGroup != null){
    		html += "<span title='";
            if (metadata != null) {
                for (String m: metadata){
                    html += m + "\n";
                }
            } else {
                html += "No metadata\n";
            }
	    	html += "'>" + metadataGroup + "</span>";
    	}
    	return html;
    }

    /**
     * @param metadata the metadata to set
     */
    public void setMetadata(List<String> metadata) {
        this.metadata = metadata;
    }

    /**
     * @param maleControls the maleControls to set
     */
    public void setMaleControls(Set<ObservationDTO> maleControls) {
        this.maleControls = maleControls;
    }

    /**
     * @param femaleControls the femaleControls to set
     */
    public void setFemaleControls(Set<ObservationDTO> femaleControls) {
        this.femaleControls = femaleControls;
    }

    public String getAlleleAccession() {
        return this.alleleAccession;
    }

    public void setAlleleAccession(String alleleAccession) {
        this.alleleAccession=alleleAccession;
    }

	/**
	 * @return the procedureStableId
	 */
	public String getProcedureStableId() {

		return procedureStableId;
	}

	/**
	 * @param procedureStableId the procedureStableId to set
	 */
	public void setProcedureStableId(String procedureStableId) {

		this.procedureStableId = procedureStableId;
	}


	@Override
    public int hashCode() {
        int hash = 7;
        hash = 23 * hash + Objects.hashCode(this.experimentId);
        hash = 23 * hash + Objects.hashCode(this.metadataGroup);
        hash = 23 * hash + Objects.hashCode(this.parameterStableId);
        hash = 23 * hash + Objects.hashCode(this.pipelineStableId);
        hash = 23 * hash + Objects.hashCode(this.observationType);
        hash = 23 * hash + Objects.hashCode(this.organisation);
        hash = 23 * hash + Objects.hashCode(this.strain);
        hash = 23 * hash + Objects.hashCode(this.geneMarker);
        hash = 23 * hash + Objects.hashCode(this.zygosities);
        hash = 23 * hash + Objects.hashCode(this.sexes);
        hash = 23 * hash + Objects.hashCode(this.results);
        hash = 23 * hash + Objects.hashCode(this.homozygoteMutants);
        hash = 23 * hash + Objects.hashCode(this.heterozygoteMutants);
        hash = 23 * hash + Objects.hashCode(this.hemizygoteMutants);
        hash = 23 * hash + Objects.hashCode(this.controls);
        hash = 23 * hash + Objects.hashCode(this.controlBiologicalModelId);
        hash = 23 * hash + Objects.hashCode(this.experimentalBiologicalModelId);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ExperimentDTO other = (ExperimentDTO) obj;
        if (!Objects.equals(this.experimentId, other.experimentId)) {
            return false;
        }
        if (!Objects.equals(this.metadataGroup, other.metadataGroup)) {
            return false;
        }
        if (!Objects.equals(this.parameterStableId, other.parameterStableId)) {
            return false;
        }
        if (!Objects.equals(this.pipelineStableId, other.pipelineStableId)) {
            return false;
        }
        if (this.observationType != other.observationType) {
            return false;
        }
        if (!Objects.equals(this.organisation, other.organisation)) {
            return false;
        }
        if (!Objects.equals(this.strain, other.strain)) {
            return false;
        }
        if (!Objects.equals(this.geneMarker, other.geneMarker)) {
            return false;
        }
        if (!Objects.equals(this.zygosities, other.zygosities)) {
            return false;
        }
        if (!Objects.equals(this.sexes, other.sexes)) {
            return false;
        }
        if (!Objects.equals(this.results, other.results)) {
            return false;
        }
        if (!Objects.equals(this.homozygoteMutants, other.homozygoteMutants)) {
            return false;
        }
        if (!Objects.equals(this.heterozygoteMutants, other.heterozygoteMutants)) {
            return false;
        }
        if (!Objects.equals(this.hemizygoteMutants, other.hemizygoteMutants)) {
            return false;
        }
        if (!Objects.equals(this.controls, other.controls)) {
            return false;
        }
        if (!Objects.equals(this.controlBiologicalModelId, other.controlBiologicalModelId)) {
            return false;
        }
        if (!Objects.equals(this.experimentalBiologicalModelId, other.experimentalBiologicalModelId)) {
            return false;
        }
        return true;
    }
}