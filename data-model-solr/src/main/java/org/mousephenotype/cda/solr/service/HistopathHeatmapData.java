package org.mousephenotype.cda.solr.service;

import java.util.ArrayList;
import java.util.List;

public class HistopathHeatmapData {
    private List<String> parameterNames;

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

    public List<List<Integer>> getData() {
        return data;
    }

    public void setData(List<List<Integer>> data) {
        this.data = data;
    }

    private List<String> geneSymbols;
    List<List<Integer>> data;//triplets required row, column, value

    public HistopathHeatmapData(List<String> parameterNames, List<String> geneSymbols, List<List<Integer>>data){
        this.parameterNames=parameterNames;
        this.geneSymbols=geneSymbols;
        this.data=data;
    }

}
