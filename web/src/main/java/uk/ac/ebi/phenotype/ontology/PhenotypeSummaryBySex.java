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
package uk.ac.ebi.phenotype.ontology;

import java.util.ArrayList;

public class PhenotypeSummaryBySex {
	
	private ArrayList <PhenotypeSummaryType> notSignificantMalePhens;
	private ArrayList <PhenotypeSummaryType> notSignificantFemalePhens;
	private ArrayList <PhenotypeSummaryType> notSignificantBothPhens;
	private ArrayList <PhenotypeSummaryType> significantMalePhens;
	private ArrayList <PhenotypeSummaryType> significantFemalePhens;
	private ArrayList <PhenotypeSummaryType> significantBothPhens;
	
	public PhenotypeSummaryBySex(){

		significantMalePhens = new ArrayList<PhenotypeSummaryType>();
		significantFemalePhens = new ArrayList<PhenotypeSummaryType>();
		significantBothPhens = new ArrayList<PhenotypeSummaryType>();
		
		notSignificantMalePhens = new ArrayList<PhenotypeSummaryType>();
		notSignificantFemalePhens = new ArrayList<PhenotypeSummaryType>();
		notSignificantBothPhens = new ArrayList<PhenotypeSummaryType>();
	}
	
	
	public void addPhenotye ( PhenotypeSummaryType obj) 
	throws Exception{

		String sex = obj.getSex();
		
		if (obj.getSex() == null){
			if (obj.isSignificant()){
				significantBothPhens.add(obj);
			} else {
				notSignificantBothPhens.add(obj);
			}			
		} else {
			if (sex.equals("male")){
				if (obj.isSignificant()){
					significantMalePhens.add(obj);
				} else {
					notSignificantMalePhens.add(obj);					
				}
			} else if (sex.equals("female")){
				if (obj.isSignificant()){
					significantFemalePhens.add(obj);
				} else {
					notSignificantFemalePhens.add(obj);					
				}
			} else if (sex.equals("both sexes")){
				if (obj.isSignificant()){
					significantBothPhens.add(obj);
				} else {
					notSignificantBothPhens.add(obj);
				}
			} else { 
				throw (new Exception("Object of type PhenotypeSummaryType recieved without valid sex field."));
			}
		}
	}
	
	public ArrayList <PhenotypeSummaryType> getMalePhenotypes(Boolean significant){
		if (significant){
			return significantMalePhens;
		} else {
			return notSignificantMalePhens;
		}
	}
	
	public ArrayList <PhenotypeSummaryType> getFemalePhenotypes(Boolean significant){
		if (significant){
			return significantFemalePhens;
		} else {
			return notSignificantFemalePhens;
		}
	}
	
	public ArrayList <PhenotypeSummaryType> getBothPhenotypes(Boolean significant){
		if (significant){
			return significantBothPhens;
		} else {
			return notSignificantBothPhens;
		}
	}


	public int getTotalPhenotypesNumber(){

		int total = 0;

		for (PhenotypeSummaryType entry: significantMalePhens) {
			total += entry.getNumberOfEntries();
		}
		for (PhenotypeSummaryType entry: significantFemalePhens) {
			total += entry.getNumberOfEntries();
		}
		for (PhenotypeSummaryType entry: significantBothPhens) {
			total += entry.getNumberOfEntries();
		}

		for (PhenotypeSummaryType entry: notSignificantMalePhens) {
			total += entry.getNumberOfEntries();
		}
		for (PhenotypeSummaryType entry: notSignificantFemalePhens) {
			total += entry.getNumberOfEntries();
		}
		for (PhenotypeSummaryType entry: notSignificantBothPhens) {
			total += entry.getNumberOfEntries();
		}

		return total;
	}
	
}
