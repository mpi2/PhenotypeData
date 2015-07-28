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
package org.mousephenotype.cda.solr.web.dto;

import java.util.List;

/**
 * Type/structure that I needed to pass bigger objects around for the stacked bars / histograms.
 * @author tudose
 *
 */
public class StackedBarsData {
	List<String> mutantGenes;
	List<String> controlGenes;
	List<String> mutantGeneAccesionIds;
	List<String> controlGeneAccesionIds;
	List<Double> upperBounds;
	List<Double> controlMutatns;
	List<Double> phenMutants;
	
	
	public List<String> getMutantGeneAccesionIds() {
		return mutantGeneAccesionIds;
	}
	public void setMutantGeneAccesionIds(List<String> mutantGeneAccesionIds) {
		this.mutantGeneAccesionIds = mutantGeneAccesionIds;
	}
	public List<String> getControlGeneAccesionIds() {
		return controlGeneAccesionIds;
	}
	public void setControlGeneAccesionIds(List<String> controlGeneAccesionIds) {
		this.controlGeneAccesionIds = controlGeneAccesionIds;
	}
	public List<String> getMutantGenes() {
		return mutantGenes;
	}
	public void setMutantGenes(List<String> mutantGenes) {
		this.mutantGenes = mutantGenes;
	}
	public List<String> getControlGenes() {
		return controlGenes;
	}
	public void setControlGenes(List<String> controlGenes) {
		this.controlGenes = controlGenes;
	}
	public List<Double> getUpperBounds() {
		return upperBounds;
	}
	public void setUpperBounds(List<Double> upperBounds) {
		this.upperBounds = upperBounds;
	}
	public List<Double> getControlMutatns() {
		return controlMutatns;
	}
	public void setControlMutatns(List<Double> controlMutatns) {
		this.controlMutatns = controlMutatns;
	}
	public List<Double> getPhenMutants() {
		return phenMutants;
	}
	public void setPhenMutants(List<Double> phenMutants) {
		this.phenMutants = phenMutants;
	}
	
	
}
