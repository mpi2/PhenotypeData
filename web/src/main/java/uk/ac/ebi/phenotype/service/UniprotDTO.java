package uk.ac.ebi.phenotype.service;

import java.util.Set;
import java.util.HashSet;


public class UniprotDTO {


	private String function;
	private String name;
	private Set<String> synonyms;
	private Set<String> goProcess;
	private Set<String> goCell;
	private Set<String> goMolecularFunction;
	
	public String getFunction() {
		return function;
	}
	
	public String getFunctionSummary(){
		if (function.length() > 100){
			return function.substring(0, 100);
		} else return function;
	}
	
	public void setFunction(String function) {
		this.function = function;
	}
	public Set<String> getGoProcess() {
		return goProcess;
	}
	public void setGoProcess(Set<String> goProcess) {
		this.goProcess = goProcess;
	}
	public void addGoProcess(String goProcess) {
		if (this.goProcess == null){
			this.goProcess = new HashSet<>();
		} 
		this.goProcess.add(goProcess);
	}
	
	public Set<String> getGoCell() {
		return goCell;
	}
	public void setGoCell(Set<String> goCell) {
		this.goCell = goCell;
	}
	public void addGoCell(String goCell) {
		if (this.goCell == null){
			this.goCell = new HashSet<>();
		} 
		this.goCell.add(goCell);
	}
	
	public Set<String> getGoMolecularFunction() {
		return goMolecularFunction;
	}
	public void setGoMolecularFunction(Set<String> goMolecularFunction) {
		this.goMolecularFunction = goMolecularFunction;
	}
	public void addGoMolecularFunction(String goMolecularFunction) {
		if (this.goMolecularFunction == null){
			this.goMolecularFunction = new HashSet<>();
		} 
		this.goMolecularFunction.add(goMolecularFunction);
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Set<String> getSynonyms() {
		return synonyms;
	}
	public void setSynonyms(Set<String> synonyms) {
		this.synonyms = synonyms;
	}
	public void addSynonym(String synonym) {
		if (this.synonyms == null){
			this.synonyms = new HashSet<>();
		} 
		this.synonyms.add(synonym);
	}
}
