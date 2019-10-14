package org.mousephenotype.cda.solr.service;


import org.springframework.boot.configurationprocessor.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

public class HistopathHeatmapData {
    private List<String> parameterNames;

    public List<List<String>> getRows() {
        return rows;
    }

    public void setRows(List<List<String>> rows) {
        this.rows = rows;
    }

    List<List<String>>rows= new ArrayList<>();

    public List<String> getParameterNames() {
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

    public HistopathHeatmapData(List<String> parameterNames, List<String> geneSymbols, JSONArray data){
        this.parameterNames=parameterNames;
        this.geneSymbols=geneSymbols;
        this.data=data;
    }

    public HistopathHeatmapData(List<String> parameterNames, List<String> geneSymbols, List<List<String>> rows){
        this.parameterNames=parameterNames;
        this.geneSymbols=geneSymbols;
        this.rows=rows;
    }

}
