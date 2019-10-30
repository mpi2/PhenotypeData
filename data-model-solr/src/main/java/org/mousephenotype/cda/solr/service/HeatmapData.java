package org.mousephenotype.cda.solr.service;


import org.springframework.boot.configurationprocessor.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

public class HeatmapData {
    private List<String> parameterNames;

    public List<String> getMgiAccessions() {
        return mgiAccessions;
    }

    public void setMgiAccessions(List<String> mgiAccessions) {
        this.mgiAccessions = mgiAccessions;
    }

    private List<String> mgiAccessions;

    public List<List<Integer>> getRows() {
        return rows;
    }

    public void setRows(List<List<Integer>> rows) {
        this.rows = rows;
    }

    List<List<Integer>>rows= new ArrayList<>();

    public List<String> getColumnHeaders() {
        return parameterNames;
    }

    public void setParameterNames(List<String> parameterNames) {
        this.parameterNames = parameterNames;
    }

    public List<String> getGeneSymbols() {
        return geneSymbols;
    }

    public void setGeneSymbols(List<String> geneSymbols) {
        this.geneSymbols = geneSymbols;
    }

    public JSONArray getData() {
        return data;
    }

    public void setData(JSONArray data) {
        this.data = data;
    }

    private List<String> geneSymbols;
    JSONArray data;//triplets required row, column, value

    public HeatmapData(List<String> parameterNames, List<String> geneSymbols, JSONArray data){
        this.parameterNames=parameterNames;
        this.geneSymbols=geneSymbols;
        this.data=data;
    }

    public HeatmapData(List<String> parameterNames, List<String> geneSymbols, List<List<Integer>> rows){
        this.parameterNames=parameterNames;
        this.geneSymbols=geneSymbols;
        this.rows=rows;
    }

}
