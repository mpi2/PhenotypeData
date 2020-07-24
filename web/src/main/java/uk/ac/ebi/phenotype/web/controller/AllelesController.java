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
package uk.ac.ebi.phenotype.web.controller;

import org.apache.commons.collections.map.HashedMap;
import org.mousephenotype.cda.solr.service.Design;
import org.mousephenotype.cda.solr.service.HtgtService;
import org.mousephenotype.cda.utilities.HttpProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import uk.ac.ebi.phenotype.generic.util.SolrIndex2;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

@Controller
public class AllelesController {

    private final Logger log = LoggerFactory.getLogger(AllelesController.class);

    SolrIndex2 solrIndex2;

    private final HtgtService htgtService;

    private static final Map<Integer, Boolean> ikmcDesignMap=new HashedMap();

    @Inject
    public AllelesController(SolrIndex2 solrIndex2,HtgtService htgtService) {
        this.htgtService = htgtService;
        this.solrIndex2=solrIndex2;
    }


    @RequestMapping("/alleles/{acc}")
    public String alleles1(
            @PathVariable String acc,
            @RequestParam (value = "ikmc_project_id", required = false) String ikmc_project_id,
            @RequestParam (value = "allele_type", required = false) String allele_type,
            @RequestParam (value = "pipeline", required = false) String pipeline,
            @RequestParam(value = "bare", required = false, defaultValue = "false") Boolean bare,
            Model model,
            HttpServletRequest request,
            RedirectAttributes attributes) throws Exception {
        log.info("#### alleles1...");
        String redirectUrl="redirect:/search/allele2?kw=*";
        //redirect this url to the new search page for products to support old links that will still use the bare parameter to specify creline products rather than default ones of IMPC
        if ( request.getParameter("bare") != null && request.getParameter("bare").equals("true")) {
                log.info("redirect to search with pipeline = Cre");
                redirectUrl=redirectUrl+"&creLine=true";
        }
        return redirectUrl;
    }


    @RequestMapping("/alleles/{acc}/{allele_name:.*}")
    public String alleles2(
            @PathVariable String acc,
            @PathVariable(value="allele_name") String allele_name,  // redefine, so that string after dot will not be truncated
            @RequestParam(value = "creLine", required = false, defaultValue = "false") Boolean creLine,
            Model model,
            HttpServletRequest request,
            RedirectAttributes attributes) throws KeyManagementException, NoSuchAlgorithmException, URISyntaxException, IOException, Exception {
        log.info("#### AllelesController::alleles2");
        log.info("#### acc: " + acc);
        log.info("#### allele_name: " + allele_name);
        //model.addAttribute("allele_name",URLDecoder.decode(allele_name, "UTF-8"));
        System.out.println("creline is "+creLine);
        String debug = request.getParameter("debug");
        log.info("#### alleles1: debug: " + debug);
        boolean d = debug != null && debug.equals("true");
        if (creLine){
            model.addAttribute("creLine", creLine);
        }

        return allelesCommon(acc, allele_name, null, null, model, request, attributes, false);
    }

    @RequestMapping("/allelesFrag/{acc}/{allele_name:.*}")
    public String alleles2Frag(
            @PathVariable String acc,
            @PathVariable(value="allele_name") String allele_name,  // redefine, so that string after dot will not be truncated
            @RequestParam(value = "creLine", required = false, defaultValue = "false") Boolean creLine,
            Model model,
            HttpServletRequest request,
            RedirectAttributes attributes) throws KeyManagementException, NoSuchAlgorithmException, URISyntaxException, IOException, Exception {
        return allelesCommon(acc, allele_name, null, null, model, request, attributes, true);
    }

    @RequestMapping("/alleles/{acc}/{cassette}/{design_id}")
    public String alleles3(
            @PathVariable String acc,
            @PathVariable String cassette,
            @PathVariable String design_id,
            @RequestParam(value = "bare", required = false, defaultValue = "false") Boolean bare,
            Model model,
            HttpServletRequest request,
            RedirectAttributes attributes) throws KeyManagementException, NoSuchAlgorithmException, URISyntaxException, IOException, Exception {

        log.info("#### AllelesController::alleles3");
        log.info("#### acc: " + acc);
        log.info("#### cassette: " + cassette);
        log.info("#### design_id: " + design_id);

        if (bare) model.addAttribute("bare", bare);

        return allelesCommon(acc, null , cassette, design_id, model, request, attributes, false);
    }


    @RequestMapping("/alleles/qc_data/{type}/{name}")
    public String qcData(
            @PathVariable String type,
            @PathVariable String name,
            @RequestParam(value = "bare", required = false, defaultValue = "false") Boolean bare,
            Model model,
            HttpServletRequest request,
            RedirectAttributes attributes) throws Exception {

        HashMap<String, HashMap<String, List<String>>> constructs;
        if ( request.getParameter("bare") != null && request.getParameter("bare").equals("true")) {
        	constructs = solrIndex2.getAlleleQcInfo( type, name, true);
        }
        else {
        	constructs = solrIndex2.getAlleleQcInfo( type, name, false);
        }

        if (bare) model.addAttribute("bare", bare);
        model.addAttribute("qcData", constructs);
        model.addAttribute("type", type);
        model.addAttribute("name", name);

        String simple = request.getParameter("simple");
        boolean s = simple != null && simple.equals("true");
        if(s) {
            return "qcDataSimple";
        }

        return "qcData";
    }


    @RequestMapping("/genotyping_primers/{acc}/{allele_name}")
    public String genotyping_primers_acc_allele(
            @PathVariable String acc,
            @PathVariable String allele_name,
            @RequestParam(value = "bare", required = false, defaultValue = "false") Boolean bare,
            Model model,
            HttpServletRequest request,
            RedirectAttributes attributes) throws KeyManagementException, NoSuchAlgorithmException, URISyntaxException, IOException, Exception {


        Map<String, String> allele_identifier = new HashMap();
        allele_identifier.put("allele_name", allele_name);
        allele_identifier.put("accession", acc);

        Map<String, Object> products;
        if ( request.getParameter("bare") != null && request.getParameter("bare").equals("true")) {
        	products = solrIndex2.getAlleleProductInfo("cre", allele_identifier, false);
        }
        else {
        	products = solrIndex2.getAlleleProductInfo("impc", allele_identifier, false);
        }


        if (products == null) {
            return "genotyping_primers";
        }

        if (bare) model.addAttribute("bare", bare);
        model.addAttribute("mice", products.get("mice"));
        model.addAttribute("es_cells", products.get("es_cells"));
        model.addAttribute("targeting_vectors", products.get("targeting_vectors"));
        model.addAttribute("summary", products.get("summary"));

        HashMap<String, Object> summary = (HashMap<String, Object>) products.get("summary");

        boolean isMirko = summary.get("pipeline").toString().equals("Mirko");

        String id;
       if(isMirko) {
            id = summary.get("design_id").toString();
        }
        else {
            id = summary.get("ikmc_project").toString();
        }

        JSONObject object = getPcrDetails(isMirko, id);
        model.addAttribute("lrpcr", object);

        
     

        if (id != null) {
            model.addAttribute("lrpcr_title", summary.get("symbol") + " LRPCR Genotyping Primers");
        } else {
            model.addAttribute("lrpcr_title", "LRPCR Genotyping Primers not available");
        }

        if(summary.get("loa_link_id")!=null && !summary.get("loa_link_id").equals("null")){
        	String loa_link_id = summary.get("loa_link_id").toString();
        	log.info("#### GENOTYPE PRIMERS " + loa_link_id);
            JSONObject object2 = getLoaDetails(loa_link_id);
            model.addAttribute("loapcr", object2);
            model.addAttribute("loa_title", summary.get("symbol") + " LOA Genotyping Primers");
        } else {
            model.addAttribute("loa_title", "LOA Genotyping Primers not available");
        }

        return "genotyping_primers";
    }

    @RequestMapping("/mutagenesis/{acc}/{allele_name}")
    public String mutagenesis_project_type(
            @PathVariable String acc,
            @PathVariable String allele_name,
            @RequestParam(value = "bare", required = false, defaultValue = "false") Boolean bare,
            Model model,
            HttpServletRequest request,
            RedirectAttributes attributes) throws KeyManagementException, NoSuchAlgorithmException, URISyntaxException, IOException, Exception {

        log.info("#### mutagenesis_project_type");

        Map<String, String> allele_identifier = new HashMap();
        allele_identifier.put("allele_name", allele_name);
        allele_identifier.put("accession", acc);
        Map<String, Object> products = solrIndex2.getAlleleProductInfo("impc", allele_identifier, false);

        if (products == null) {
            return "mutagenesis";
        }

        if (bare) model.addAttribute("bare", bare);
        model.addAttribute("mice", products.get("mice"));
        model.addAttribute("es_cells", products.get("es_cells"));
        model.addAttribute("targeting_vectors", products.get("targeting_vectors"));
        model.addAttribute("summary", products.get("summary"));

        HashMap<String, Object> summary = (HashMap<String, Object>) products.get("summary");

        String projectId = summary.get("ikmc_project").toString();

        JSONArray mutagenesis = getMutagenesisDetails(null, null, projectId);
        Map<String, Integer> stats = getMutagenesisStats(mutagenesis);
        String blurb = getMutagenesisBlurb(stats, (HashMap<String, Object>) products.get("summary"));

        model.addAttribute("mutagenesis", mutagenesis);
        model.addAttribute("mutagenesis_stats", stats);
        model.addAttribute("mutagenesis_blurb", blurb); // TODO: rename me

        log.info("#### mutagenesis allele type" + summary.get("symbol"));
        if (projectId != null) {
            model.addAttribute("mutagenesis_title", summary.get("symbol") + " Mutagenesis Prediction");
        } else {
            model.addAttribute("mutagenesis_title", "Mutagenesis Prediction not available");
        }

        log.info("#### mutagenesis: " + mutagenesis);

        return "mutagenesis";
    }

    private String allelesCommon(
            String acc,
            String allele_name,
            String cassette,
            String design_id,
            Model model,
            HttpServletRequest request,
            RedirectAttributes attributes,
            boolean fragment) throws Exception {

        String view = "alleles";

        if (fragment) {
            view = "allelesFrag";
        }

        log.info("#### AllelesController::allelesCommon");
        log.info("#### acc: " + acc);
        log.info("#### allele_name: " + allele_name);

        String debug = request.getParameter("debug");
        log.info("#### allelesCommon: debug: " + debug);
        log.info("#### allelesCommon: bare: " + request.getParameter("bare"));
        boolean d = debug != null && debug.equals("true");
        if(d) {
            model.addAttribute("debug", "true");
        }

        Map<String, String> allele_identifier = new HashMap();
        allele_identifier.put("accession", acc);
        allele_identifier.put("allele_name", URLDecoder.decode(allele_name, "UTF-8"));
        allele_identifier.put("cassette", cassette);
        allele_identifier.put("design_id", design_id);

        Map<String, Object> constructs;
        String pipeline="impc";
        if ( request.getParameter("creLine") != null && request.getParameter("creLine").equals("true")) {
        	// here we reuse the IMPC code to create eucommtools allele project page that is used in creline.org
        	// the main difference is the solr core used
                log.info("Call SolrIndex2 with pipeline = Cre");
        	pipeline="cre";
        }
        constructs = solrIndex2.getAlleleProductInfo(pipeline, allele_identifier, d);
        if (constructs == null) {
            log.info("return empty data");
            return view;
        }

        System.out.println("constructs="+constructs);
        ArrayList<Map<String,String>> vectorArray = (ArrayList) constructs.get("targeting_vectors");
        System.out.println("vectors="+vectorArray.toString());

        Map<String, Boolean> ikmcDesignMapForRow=new HashMap();
        for(Map<String,String> vector: vectorArray){
            String idString=vector.get("ikmc_project_id");
            System.out.println("vector="+idString);
            Integer ikmcProjectId=Integer.parseInt(idString);
            if(ikmcDesignMap.containsKey(ikmcProjectId)){
                if(ikmcDesignMap.get(ikmcProjectId)){
                    ikmcDesignMapForRow.put(String.valueOf(ikmcProjectId), ikmcDesignMap.get(ikmcProjectId));
                };
            }else{
                List<Design> designs = htgtService.getDesigns(Integer.parseInt(idString));
                Boolean isLink=false;
                if(designs.size()>0){
                   isLink=true;
                   ikmcDesignMapForRow.put(String.valueOf(ikmcProjectId), true);
                }
                ikmcDesignMap.put(ikmcProjectId, isLink);//store true and false so we don't need to check again for another request if we know it's false

            }

        }

        model.addAttribute("mice", constructs.get("mice"));
        model.addAttribute("es_cells", constructs.get("es_cells"));
        model.addAttribute("targeting_vectors", constructs.get("targeting_vectors"));
        model.addAttribute("tissue_enquiry_types", constructs.get("tissue_enquiry_types"));
        model.addAttribute("tissue_enquiry_links", constructs.get("tissue_enquiry_links"));
        model.addAttribute("tissue_distribution_centres", constructs.get("tissue_distribution_centres"));
        model.addAttribute("summary", constructs.get("summary"));
        model.addAttribute("other_available_alleles_with_mice", constructs.get("other_alleles_with_mice"));
        model.addAttribute("other_available_alleles_with_es_cells", constructs.get("other_alleles_with_es_cells"));
        model.addAttribute("title", constructs.get("title"));
        model.addAttribute("accession", acc);
        model.addAttribute("ikmcDesignMapForRow", ikmcDesignMapForRow);


        if (model.containsAttribute("show_header")) {
            return "alleles_noheader";
        }
        return view;
    }


    private JSONObject getPcrDetails(
            boolean isMirko,
            String id) throws MalformedURLException, IOException, URISyntaxException {

        if (id == null) {
            return null;
        }

        String url = "http://www.sanger.ac.uk/htgt/htgt2/tools/genotypingprimers/" + id;

        if(isMirko) {
            url = "http://www.sanger.ac.uk/htgt/htgt2/tools/genotypingprimers/mirko_primers/" + id;
        }

        HttpProxy proxy = new HttpProxy();
        String content;

        try {
            content = proxy.getContent(new URL(url));
        } catch (IOException | URISyntaxException ex) {
            log.error("#### getPcrDetails: " + ex);
            return null;
        }

        log.info("#### getPcrDetails: " + content);

        JSONObject jsonObject;

        try {
            jsonObject = new JSONObject(content);
        } catch (Exception ex) {
            log.error("#### getPcrDetails: " + ex);
            return null;
        }

        return jsonObject;
    }


    private JSONObject getLoaDetails(
            String id) throws MalformedURLException, IOException, URISyntaxException {

        if (id == null) {
            return null;
        }

          String url = "http://deskpro101887.internal.sanger.ac.uk:3000/targ_rep/alleles/" + id + "/loa_primers.json";

        log.info("#### getLoaPcrDetails URL: " + url);

        HttpProxy proxy = new HttpProxy();
        String content;

        try {
            content = proxy.getContent(new URL(url));
        } catch (IOException | URISyntaxException ex) {
            log.error("#### getLoaPcrDetails: " + ex);
            return null;
        }

        log.info("#### getLoaPcrDetails: " + content);

        JSONObject jsonObject;

        try {
            jsonObject = new JSONObject(content);
        } catch (Exception ex) {
            log.error("#### getLoaPcrDetails: " + ex);
            return null;
        }

        return jsonObject;
    }

    @RequestMapping("/lrpcr/{acc}/{allele_name}")
    public String lrpcr_acc_allele(
            @PathVariable String acc,
            @PathVariable String allele_name,
            Model model,
            HttpServletRequest request,
            RedirectAttributes attributes) throws KeyManagementException, NoSuchAlgorithmException, URISyntaxException, IOException, Exception {

        log.info("#### lrpcr_acc_allele");

        Map<String, String> allele_identifier = new HashMap();
        allele_identifier.put("allele_name", allele_name);
        allele_identifier.put("accession", acc);

        Map<String, Object> products = solrIndex2.getAlleleProductInfo("impc", allele_identifier, false);

        if (products == null) {
            return "lrpcr_genotyping_primers";
        }

        model.addAttribute("mice", products.get("mice"));
        model.addAttribute("es_cells", products.get("es_cells"));
        model.addAttribute("targeting_vectors", products.get("targeting_vectors"));
        model.addAttribute("summary", products.get("summary"));

        HashMap<String, Object> summary = (HashMap<String, Object>) products.get("summary");

        boolean isMirko = summary.get("pipeline").toString().equals("Mirko");

        String id;

        if(isMirko) {
            id = summary.get("design_id").toString();
        }
        else {
            id = summary.get("ikmc_project").toString();
        }

        JSONObject object = getPcrDetails(isMirko, id);
        model.addAttribute("lrpcr", object);

        if (id != null) {
            model.addAttribute("lrpcr_title", summary.get("symbol") + " LRPCR Genotyping Primers");
        } else {
            model.addAttribute("lrpcr_title", "LRPCR Genotyping Primers not available");
        }

        return "lrpcr_genotyping_primers";
    }


    private Map<String, Integer> getMutagenesisStats(JSONArray transcripts) throws JSONException {

        if (transcripts == null) {
            return null;
        }

        Map<String, Integer> map = new HashMap<>();
        map.put("wt_transcripts", 0);
        map.put("wt_non_coding_transcripts", 0);
        map.put("wt_protein_coding_transcripts", 0);
        map.put("mut_nmd_transcripts", 0);
        map.put("mut_coding_transcripts", 0);
        map.put("mut_nmd_rescue_transcripts", 0);

        for (int i = 0; i < transcripts.length(); i++) {
            Object o = transcripts.get(i);
            JSONObject o2 = (JSONObject) o;
            map.put("wt_transcripts", map.get("wt_transcripts") + 1);
            if (o2.has("biotype") && o2.getString("biotype").equals("protein_coding")) {
                map.put("wt_protein_coding_transcripts", map.get("wt_protein_coding_transcripts") + 1);

                if (o2.getString("floxed_transcript_description").matches("^No protein product \\(NMD\\)")) {
                    map.put("mut_nmd_transcripts", map.get("mut_nmd_transcripts") + 1);
                }

                if (o2.getString("floxed_transcript_description").matches("^No protein product .NMD.")
                        || !o2.getString("floxed_transcript_description").matches("^No protein product")) {
                    map.put("mut_coding_transcripts", map.get("mut_coding_transcripts") + 1);
                }

                if (o2.getString("floxed_transcript_description").matches("^Possible NMD rescue")) {
                    map.put("mut_nmd_rescue_transcripts", map.get("mut_nmd_rescue_transcripts") + 1);
                }
            }
            map.put("wt_non_coding_transcripts", map.get("wt_transcripts") - map.get("wt_protein_coding_transcripts"));
        }
        return map;
    }

    // TODO: fix dodgy routine returning html!

    private String getMutagenesisBlurb(Map<String, Integer> stats, HashMap<String, Object> summary) {

        if (stats == null) {
            return null;
        }

        HashMap<String, Object> allele_mapping = new HashMap<>();
        HashMap<String, Object> allele_excision_mapping = new HashMap<>();

        allele_mapping.put("a", "Deletion allele (post-Flp and Cre with no reporter)");
        allele_mapping.put("c", "Deletion allele (post-Flp and Cre with no reporter)");
        allele_mapping.put("e", "Targeted, non-conditional allele (post-Cre)");
        allele_mapping.put("", "Reporter-tagged deletion allele (post Flp, with no reporter and selection cassette)");

        allele_excision_mapping.put("a", "Flp and Cre");
        allele_excision_mapping.put("c", "Cre");
        allele_excision_mapping.put("e", "Cre");
        allele_excision_mapping.put("", "Flp");

        log.info("#### mutagenesis: " + summary.get("allele_type").toString());

        String new_allele = allele_mapping.get(summary.get("allele_type").toString()).toString();
        String excision_used = allele_excision_mapping.get(summary.get("allele_type").toString()).toString();

        log.info("#### mutagenesis: " + new_allele);
        log.info("#### mutagenesis: " + excision_used);

        String s
                = "This gene has " + stats.get("wt_transcripts") + " wild type transcripts, "
                + "of which " + stats.get("wt_protein_coding_transcripts") + " are protein-coding. "
                + "Following removal of the floxed region, " + stats.get("mut_coding_transcripts") + " ";

        if (stats.get("mut_coding_transcripts") == 1) {
            s += "transcript is ";
        } else {
            s += "transcripts are ";
        }

        s += "<strong>predicted</strong> to produce a truncated protein product of which "
                + stats.get("mut_nmd_transcripts") + " may be subject to non-sense mediated decay (NMD).";

        if (stats.get("mut_nmd_rescue_transcripts") > 0) {
            s += "<strong>NOTE:</strong> Of the " + stats.get("wt_non_coding_transcripts") + " non-coding wild type transcripts, "
                    + stats.get("mut_nmd_rescue_transcripts") + " are possibly subject to NMD rescue in the mutant.";
        }

        if (summary.get("allele_type").toString().equals("")) {
            s += "This mutation is of type 'Deletion' (more information on IKMC alleles can be found "
                    + "<a href=\"http://www.mousephenotype.org/about-ikmc/targeting-strategies\">here</a>). The table below "
                    + "shows the <strong>predicted</strong> structure of the gene transcripts. "
                    + "Click the 'view' button for each transcript to see the full prediction for that transcript. ";
        } else {
            s += " The original allele for this mutation is of type '" + summary.get("allele_description").toString() + "'. The table below "
                    + "shows the <strong>predicted</strong> structure of the gene transcripts after application of "
                    + excision_used
                    + " (forming a '"
                    + new_allele
                    + "' - more information on IKMC alleles can be found "
                    + "<a href=\"http://www.mousephenotype.org/about-ikmc/targeting-strategies\">here</a>). "
                    + "Click the 'view' button for each transcript to see the full prediction for that transcript. ";
        }

        return s;
    }

    // TODO: move to separate class?

    private JSONArray getMutagenesisDetails(
            String acc,
            String allele_name,
            String projectId) throws JSONException {

        if (projectId == null) {
            log.info("#### getMutagenesisDetails: no project id!");
            return null;
        }

        String url = "http://www.sanger.ac.uk/htgt/htgt2/tools/mutagenesis_prediction/project/" + projectId + "/detail";

        log.info("#### getMutagenesisDetails: url: ");
        log.info(url);

        HttpProxy proxy = new HttpProxy();
        String content;

        try {
            content = proxy.getContent(new URL(url));
        } catch (IOException | URISyntaxException ex) {
            log.error("#### getMutagenesisDetails: " + ex);
            return null;
        }

        log.debug("#### content: " + content);

        JSONArray json = new JSONArray(content);

        log.info("#### before: " + json);

        Map<String, String> mapper = new HashMap<>();
        mapper.put("UC", "UTR + start codon + CDS");
        mapper.put("CU", "CDS + stop codon + UTR");
        mapper.put("C", "CDS");
        mapper.put("U", "UTR");

        for (int i = 0; i < json.length(); i++) {
            Object json1 =json.get(i);
            JSONObject o = (JSONObject) json1;
            log.info("#### o: " + o);
            if (o.has("floxed_transcript_translation")) {
                String floxed_transcript_translation = o.getString("floxed_transcript_translation");
                o.put("floxed_transcript_translation", floxed_transcript_translation.trim());
            }
            if (o.has("exons")) {
                JSONArray exons = o.getJSONArray("exons");

                for (int j = 0; j < exons.length(); j++) {
                    Object exon = exons.get(j);
                    JSONObject o2 = (JSONObject) exon;
                    if (o2.has("structure")) {
                        String structure = o2.getString("structure");
                        o2.put("structure_ex", mapper.get(structure));
                    }
                    if (o2.has("floxed_structure")) {
                        String floxed_structure = o2.getString("floxed_structure");
                        o2.put("floxed_structure_ex", mapper.get(floxed_structure));
                    } else {
                        o2.put("floxed_structure_ex", "Deleted");
                    }
                    if (o2.has("domains")) {
                        JSONArray domains = o2.getJSONArray("domains");
                        for (int k = 0; k < domains.length(); k++) {
                            Object domain = domains.get(k);
                            JSONObject o3 = (JSONObject) domain;
                            JSONArray amino_acids = o3.getJSONArray("amino_acids");
                            String amino_acid_1 = amino_acids.length() > 1 ? amino_acids.getString(0) : "";
                            String amino_acid_2 = amino_acids.length() > 1 ? amino_acids.getString(1) : "";
                            String s = o3.getString("description") + " (" + amino_acid_1 + "/" + amino_acid_2 + " aa)";
                            o3.put("domains_ex", s);
                        }
                    }
                }
            }
        }

        log.info("#### after: " + json);

        return json;
    }
}
