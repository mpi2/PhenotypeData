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
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mousephenotype.cda.solr.web.dto;

import org.apache.commons.lang3.StringUtils;
import org.mousephenotype.cda.solr.service.dto.BasicBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author jwarren
 */
public class GeneRowForHeatMap implements Comparable<GeneRowForHeatMap>{
	
    private String accession="";
    private String symbol="";
    private String groupLabel;
    private String miceProduced="No";//not boolean as 3 states No, Yes, In progress - could have an enum I guess?
	Map<String, HeatMapCell> xAxisToCellMap=new HashMap<>();
	private Float lowestPValue=new Float(1000000);//just large number so we don't get null pointers
	private List<String> humanSymbol;

	public List<String> getHumanSymbol() {
		return humanSymbol;
	}


	public String getHumanSymbolToString() {
		if(humanSymbol==null) {
			System.err.println("human geneSymbol is null for gene: " + accession);
			return "No Human Symbol";
		
		}else {
			//System.out.println("returning humanSymobl"+humanSymbol.toString().replaceAll("\\]|\\[", ""));
			return humanSymbol.toString().replaceAll("\\]|\\[", "");
		}
		
	}

	public void setHumanSymbol(List<String> humanSymbol) {
		this.humanSymbol = humanSymbol;
	}

	public String getGroupLabel() {
		return groupLabel;
	}

	public void setGroupLabel(String groupLabel) {
		this.groupLabel = groupLabel;
	}

	public Map<String, HeatMapCell> getXAxisToCellMap() {
        return xAxisToCellMap;
    }

    public void setXAxisToCellMap(Map<String, HeatMapCell> paramToCellMap) {
        this.xAxisToCellMap = paramToCellMap;
    }

    public GeneRowForHeatMap(String accession, String symbol, List<BasicBean> xAxis){
        this.accession=accession;
        this.symbol = symbol;
        for (BasicBean bean : xAxis){
        	xAxisToCellMap.put(bean.getName(), new HeatMapCell(bean.getName(), HeatMapCell.THREE_I_NO_DATA));
		}
    }

	public GeneRowForHeatMap(String accession){
		this.accession=accession;
	}

    public String getAccession() {
        return this.accession;
    }

    public void add(HeatMapCell cell) {
       this.xAxisToCellMap.put(cell.getxAxisKey(), cell);
    }
    
    public String getMiceProduced() {
		return miceProduced;
	}

    public String getMiceProducedPlain() {
		return this.statusStringForExport(miceProduced).replace("<br>", " ");
	}

	public void setMiceProduced(String miceProduced) {
		this.miceProduced = miceProduced;
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
    
    public int compareTo(GeneRowForHeatMap compareRow) {
    	String thisGroupLabel="";
    	String thatGroupLabel="";
    	if(this.getGroupLabel()!=null){
    		thisGroupLabel=this.getGroupLabel();
    	}
    	if(compareRow.getGroupLabel()!=null){
    		thatGroupLabel=compareRow.getGroupLabel();
    		
    	}
    	if(thisGroupLabel.compareTo(thatGroupLabel)==0){
    		return this.getSymbol().compareTo(compareRow.getSymbol());
    	}
		return thisGroupLabel.compareTo(thatGroupLabel);
	}

	public Float getLowestPValue() {
		return this.lowestPValue;
		
	}

	public void setLowestPValue(Float getpValue) {
		this.lowestPValue=getpValue;
	}


	public String toTabbedString() {
		return  accession + "\t" + symbol + "\t" + getHumanSymbolToString() + "\t" + groupLabel + "\t" + this.statusStringForExport(miceProduced).replace("<br>", " ") + "\t";
	}
	

	private String statusStringForExport(String status){
		List<String> values = getTagValues(status);
		return StringUtils.join(values, "|");
	}
	private static final Pattern TAG_REGEX = Pattern.compile("<span>(.+?)</span>");

	private static List<String> getTagValues(final String str) {
	    final List<String> tagValues = new ArrayList<>();
	    final Matcher matcher = TAG_REGEX.matcher(str);
	    while (matcher.find()) {
	        tagValues.add(matcher.group(1).replace("<i class='icon icon-species icon-mouse'></i>", "Mice"));
	    }

		return tagValues;
	}
	
    
    
}
