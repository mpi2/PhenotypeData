package uk.ac.ebi.phenotype.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

/**
 * Created by ckc on 23/11/2015.
 */

@Service
public class SearchConfig {

    private Map<String, String> labelMap = new HashMap<>();
    private Map<String, List<String>> facetMap = new HashMap<>();
    private Map<String, String> facetSortMap = new HashMap<>();
    private Map<String, List<String>> fieldMap = new HashMap<>();
    private Map<String, List<String>> gridHeaderMap = new HashMap<>();
    private Map<String, String> bqMap = new HashMap<>();


    public String defType = "edismax"; // default
    public String qf = "auto_suggest"; // default

    public SearchConfig() {
        setBreadcrumLabel();
        setFecetFields();
        setFacetSort();
        setFieldList();
        setGridColumns();
    }

    public String getSortingStr(String coreName) {

        Map<String, String> sorting = new HashMap<>();

        sorting.put("gene", "&sort=marker_symbol asc");
        sorting.put("mp", "&sort=mp_term asc");
        sorting.put("disease", "&sort=disease_term asc");
        sorting.put("anatomy", "&sort=term asc");
        sorting.put("impc_images", "");  // these have multivalue fields, not sortable
        //sorting.put("images", "");  // these have multivalue fields, not sortable

        return sorting.get(coreName);
    }

    public String getFqStr(String coreName, String fqStr) {

        Map<String, String> coreDefault = new HashMap<>();
        if ( coreName.equals("gene") && fqStr ==  null) {
            coreDefault.put("gene", "marker_type:\"protein coding gene\"");
        }
        else {
            coreDefault.put("gene", "*:*");
        }
        coreDefault.put("mp", "top_level_mp_term:*");
        coreDefault.put("disease", "*:*");
        coreDefault.put("anatomy", "selected_top_level_anatomy_term:*");
        coreDefault.put("impc_images", "*:*");
        //coreDefault.put("images", "*:*");

        return coreDefault.get(coreName);
    }


    public List<String> getFacetFields(String coreName) {
        return facetMap.get(coreName);
    }
    public String getFacetFieldsSolrStr(String coreName) {

        String solrStr = "";
        for ( String facetField : getFacetFields(coreName) ){
            solrStr += "&facet.field=" + facetField;
        }
        return "&facet=on&facet.limit=-1&facet.sort=" + facetSortMap.get(coreName) + solrStr;
    }


    public List<String> getFieldList(String coreName) {
        return fieldMap.get(coreName);
    }
    public String getFieldListSolrStr(String coreName) {

        String solrStr = "";
        List<String> fls = new ArrayList<>();
        for ( String fl : getFieldList(coreName) ){
            fls.add(fl);
        }
        return "&fl=" + StringUtils.join(fls, ",");
    }

    public String getQf(){ return qf; }
    public String getQfSolrStr(){
        return "&qf=" + qf;
    }
    public void setQf(String qf){
        qf = qf;
    }

    public String getDefType(){
        return defType;
    }
    public String getDefTypeSolrStr() {
        return "&defType=" + defType;
    }
    public void setDefType(String defType){
        defType = defType;
    }

    public List<String> getGridHeaders(String coreName) {
        return gridHeaderMap.get(coreName);
    }

    private void setBreadcrumLabel() {
        // coreName to breadcrum label mapping

        labelMap.put("gene", "Genes");
        labelMap.put("mp", "Phenotypes");
        labelMap.put("disease", "Diseases");
        labelMap.put("anatomy", "Anatomy");
        labelMap.put("impc_images", "IMPC Images");
        //labelMap.put("images", "Images");
    }
    public String getBreadcrumLabel(String coreName) {
      return labelMap.get(coreName);
    }

    private void setFieldList(){
        List<String> geneFields = Arrays.asList(new String[]{"marker_symbol",
                "mgi_accession_id",
                "marker_synonym",
                "marker_name",
                "marker_type",
                "human_gene_symbol",
                "latest_es_cell_status",
                "latest_production_status",
                "latest_phenotype_status",
                "status",
                "es_cell_status",
                "mouse_status",
                "legacy_phenotype_status",
                "allele_name"});
        List<String> mpFields = Arrays.asList(new String[]{"mp_id",
                "mp_term",
                "mp_term_synonym",
                "mp_definition",
                "top_level_mp_term",
                "top_mp_term_id",
                "hp_id",
                "hp_term",
                "pheno_calls"});
        List<String> diseaseFields = Arrays.asList(new String[]{"disease_id",
                "disease_term",
                "disease_source",
                "disease_classes",
                "human_curated",
                "mouse_curated",
                "impc_predicted_known_gene",
                "mgi_predicted_known_gene",
                "impc_predicted",
                "impc_novel_predicted_in_locus",
                "mgi_predicted",
                "mgi_novel_predicted_in_locus",
                "marker_symbol",
                "mgi_accession_id"});
        List<String> anatomyFields = Arrays.asList(new String[]{"anatomy_id",
                "anatomy_term",
                "anatomy_term_synonym",
                "stage",
                "selected_top_level_anatomy_term",
                "selected_top_level_anatomy_id"});
        List<String> impc_imagesFields = Arrays.asList(new String[]{"omero_id",
                "procedure_name",
                "gene_symbol",
                "gene_accession_id",
                "ma_term",
                "ma_id",
                "jpeg_url",
                "download_url",
                "parameter_association_name",
                "parameter_association_value"});
//        List<String> imagesFields = Arrays.asList(new String[]{"annotationTermId",
//                "annotationTermName",
//                "mpTermName",
//                "maTermName",
//                "expName",
//                "expName_exp",
//                "symbol",
//                "symbol_gene",
//                "smallThumbnailFilePath",
//                "largeThumbnailFilePath"});

        fieldMap.put("gene", geneFields);
        fieldMap.put("mp", mpFields);
        fieldMap.put("disease", diseaseFields);
        fieldMap.put("anatomy", anatomyFields);
        fieldMap.put("impc_images", impc_imagesFields);
        //fieldMap.put("images", imagesFields);

    }

    private void setFecetFields(){

        List<String> geneFacets = Arrays.asList(new String[]{"latest_phenotype_status",
                "legacy_phenotype_status",
                "status",
                "latest_production_centre",
                "latest_phenotyping_centre",
                "marker_type",
                "embryo_data_available",
                "embryo_modalities"
                });
        List<String> mpFacets =  Arrays.asList(new String[]{"top_level_mp_term"});
        List<String> diseaseFacets =  Arrays.asList(new String[]{"disease_source",
                "disease_classes",
                "human_curated",
                "mouse_curated",
                "impc_predicted_known_gene",
                "mgi_predicted_known_gene",
                "impc_predicted",
                "impc_novel_predicted_in_locus",
                "mgi_predicted",
                "mgi_novel_predicted_in_locus"});
        List<String> anatomyFacets =  Arrays.asList(new String[]{"selected_top_level_anatomy_term", "stage"});
        //List<String> imagesFacets =  Arrays.asList(new String[]{"procedure_name", "top_level_mp_term", "selected_top_level_ma_term", "marker_type"});
        List<String> impc_imagesFacets =  Arrays.asList(new String[]{"procedure_name", "selected_top_level_ma_term"});

        facetMap.put("gene", geneFacets);
        facetMap.put("mp", mpFacets);
        facetMap.put("disease", diseaseFacets);
        facetMap.put("anatomy", anatomyFacets);
        //facetMap.put("images", imagesFacets);
        facetMap.put("impc_images", impc_imagesFacets);

    }

    private void setFacetSort(){
        facetSortMap.put("gene", "count");
        facetSortMap.put("mp", "index");
        facetSortMap.put("disease", "count");
        facetSortMap.put("anatomy", "index");
        facetSortMap.put("impc_images", "index");
        //facetSortMap.put("images", "index");
    }

    public String getBqStr(String coreName, String q) {

        String wildCardStr = "^\\*\\w*$|^\\w*\\*$|^\\*\\w*\\*$|^\\*\\W+";
        //Pattern pattern = Pattern.compile(wildCardStr);

        if (coreName.equals("gene")) {

//            String qBoost = q;
//            if (q.matches("^\".+\"")) {
//                qBoost = q.replaceAll("\"", "");
//            }

           // if ( q.matches(wildCardStr) || q.matches("^.+\\S+.+$") ){

            bqMap.put("gene", "marker_symbol_lowercase:(" + q + ")^1000" + " marker_symbol_bf:(" + q + ")^100 latest_phenotype_status:\"Phenotyping Complete\"^200" );
        }
        else if (coreName.equals("mp")) {
            if ( q.equals("*:*") || q.equals("*") ) {
                bqMap.put("mp", "mp_term:\"male infertility\" ^100 mp_term:\"female infertility\" ^100 mp_term:infertility ^90");
            }
            else {
                bqMap.put("mp", "mp_term:(" + q + ")^1000"
                        + " mp_term_synonym:(" + q + ")^500"
                        + " mp_definition:(" + q + ")^100");
            }
        }
        else if (coreName.equals("disease")) {
            bqMap.put("disease", "disease_term:(" + q + ")^1000"
                    + " disease_alts:(" + q + ")^700"
                    + " disease_source:(" + q + ")^200");

        }
        else if (coreName.equals("anatomy")) {
            bqMap.put("anatomy", "anatomy_term:(" + q + ")^1000"
                    + " anatomy_term_synonym:(" + q + ")^500");
        }
        else if (coreName.equals("impc_images")) {
            bqMap.put("impc_images", "procedure_name:(" + q + ")^500"
                    + " gene_symbol:(" + q + ")^500");
        }
//        else if (coreName.equals("images")) {
//            bqMap.put("images", "annotationTermName:(" + q + ")^500"
//                    + " expName:(" + q + ")^500"
//                    + " symbol:(" + q + ")^500");
//        }

        return "&bq=" + bqMap.get(coreName);

    }


    private void setGridColumns(){
        List<String> geneCols = Arrays.asList(new String[]{"Gene", "Production", "Phenotype", "Register"});
        List<String> mpCols = Arrays.asList(new String[]{"Phenotype", "Definition", "Phenotyping<br>Call(s)", "Ontology<br/>Tree", "Register"});

       // List<String> diseaseCols = Arrays.asList(new String[]{"Disease", "Source", "Curated Genes", "Candidate Genes<br>by phenotype"});
        List<String> diseaseCols = Arrays.asList(new String[]{"Disease", "Source"});
        List<String> maCols = Arrays.asList(new String[]{"Anatomy", "Stage", "LacZ Expression Data", "Ontology<br/>Tree"});

        List<String> impc_imagesCols = Arrays.asList(new String[]{"Name", "Images"});
        List<String> imagesCols = Arrays.asList(new String[]{"Name", "Image(s)"});

        gridHeaderMap.put("gene", geneCols);
        gridHeaderMap.put("mp", mpCols);
        gridHeaderMap.put("disease", diseaseCols);
        gridHeaderMap.put("anatomy", maCols);
        gridHeaderMap.put("impc_images", impc_imagesCols);
        //gridHeaderMap.put("images", imagesCols);
    }


}