package uk.ac.ebi.phenotype.service;

import java.util.ArrayList;
import java.util.List;

public class UniprotDTO {


	private String function;
	private String name;
	private List<String> synonyms;
	private List<String> goProcess;
	private List<String> goCell;
	private List<String> goMolecularFunction;
	
	public String getFunction() {
		return function;
	}
	public void setFunction(String function) {
		this.function = function;
	}
	public List<String> getGoProcess() {
		return goProcess;
	}
	public void setGoProcess(List<String> goProcess) {
		this.goProcess = goProcess;
	}
	public void addGoProcess(String goProcess) {
		if (this.goProcess == null){
			this.goProcess = new ArrayList<>();
		} 
		this.goProcess.add(goProcess);
	}
	
	public List<String> getGoCell() {
		return goCell;
	}
	public void setGoCell(List<String> goCell) {
		this.goCell = goCell;
	}
	public void addGoCell(String goCell) {
		if (this.goCell == null){
			this.goCell = new ArrayList<>();
		} 
		this.goCell.add(goCell);
	}
	
	public List<String> getGoMolecularFunction() {
		return goMolecularFunction;
	}
	public void setGoMolecularFunction(List<String> goMolecularFunction) {
		this.goMolecularFunction = goMolecularFunction;
	}
	public void addGoMolecularFunction(String goMolecularFunction) {
		if (this.goMolecularFunction == null){
			this.goMolecularFunction = new ArrayList<>();
		} 
		this.goMolecularFunction.add(goMolecularFunction);
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<String> getSynonyms() {
		return synonyms;
	}
	public void setSynonyms(List<String> synonyms) {
		this.synonyms = synonyms;
	}
	public void addSynonym(String synonym) {
		if (this.synonyms == null){
			this.synonyms = new ArrayList<>();
		} 
		this.synonyms.add(synonym);
	}
}
