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

import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.Group;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.hibernate.HibernateException;
import org.mousephenotype.cda.db.dao.OntologyTermDAO;
import org.mousephenotype.cda.db.dao.PhenotypePipelineDAO;
import org.mousephenotype.cda.db.pojo.OntologyTerm;
import org.mousephenotype.cda.db.pojo.Synonym;
import org.mousephenotype.cda.enumerations.ObservationType;
import org.mousephenotype.cda.enumerations.SexType;
import org.mousephenotype.cda.solr.generic.util.PhenotypeCallSummarySolr;
import org.mousephenotype.cda.solr.generic.util.PhenotypeFacetResult;
import org.mousephenotype.cda.solr.repositories.image.ImagesSolrDao;
import org.mousephenotype.cda.solr.service.*;
import org.mousephenotype.cda.solr.service.dto.ImpressBaseDTO;
import org.mousephenotype.cda.solr.service.dto.ImpressDTO;
import org.mousephenotype.cda.solr.service.dto.MpDTO;
import org.mousephenotype.cda.solr.service.dto.ParameterDTO;
import org.mousephenotype.cda.solr.web.dto.DataTableRow;
import org.mousephenotype.cda.solr.web.dto.ExperimentsDataTableRow;
import org.mousephenotype.cda.solr.web.dto.PhenotypeCallSummaryDTO;
import org.mousephenotype.cda.solr.web.dto.PhenotypePageTableRow;
import org.mousephenotype.cda.solr.web.dto.SimpleOntoTerm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import uk.ac.ebi.phenotype.error.GenomicFeatureNotFoundException;
import uk.ac.ebi.phenotype.error.OntologyTermNotFoundException;
import uk.ac.ebi.phenotype.generic.util.RegisterInterestDrupalSolr;
import uk.ac.ebi.phenotype.util.PhenotypeGeneSummaryDTO;
import uk.ac.ebi.phenotype.web.util.FileExportUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.*;

@Controller
public class PhenotypesController {

    private final Logger log = LoggerFactory.getLogger(PhenotypesController.class);
    private static final int numberOfImagesToDisplay = 5;

    @Autowired
    private OntologyTermDAO ontoTermDao;
    @Autowired
    private PhenotypeCallSummarySolr phenoDAO;
    @Autowired
    @Qualifier("phenotypePipelineDAOImpl")
    private PhenotypePipelineDAO pipelineDao;
    @Autowired
    private ImagesSolrDao imagesSolrDao;

    @Autowired
    SolrIndex solrIndex;
    
    @Autowired
    StatisticalResultService srService;
    @Autowired
	@Qualifier("postqcService")
    PostQcService gpService;
    @Autowired
    MpService mpService;
    @Autowired
    ObservationService os;
    @Autowired
    PreQcService preqcService;
    @Autowired
    ImpressService impressService;

    @Resource(name = "globalConfiguration")
    private Map<String, String> config;

    /**
     * Phenotype controller loads information required for displaying the
     * phenotype page or, in the case of an error, redirects to the error page
     *
     * @param phenotype_id the Mammalian phenotype id of the phenotype to
     * display
     * @param model the data model
     * @param request the <code>HttpServletRequest</code> request instance
     * @param attributes redirect attributes
     * @return the name of the view to render, or redirect to search page on
     * error
     * @throws OntologyTermNotFoundException
     * @throws URISyntaxException
     * @throws IOException
     * @throws SolrServerException
     * @throws SQLException
     *
     */
    @RequestMapping(value = "/phenotypes/{phenotype_id}", method = RequestMethod.GET)
    public String loadMpPage(   @PathVariable String phenotype_id,  Model model, HttpServletRequest request, RedirectAttributes attributes)
    throws OntologyTermNotFoundException, IOException, URISyntaxException, SolrServerException, SQLException {

    	long time = System.currentTimeMillis();
    	
    	// Check whether the MP term exists
    	MpDTO mpTerm = mpService.getPhenotype(phenotype_id);
    	OntologyTerm mpDbTerm = ontoTermDao.getOntologyTermByAccessionAndDatabaseId(phenotype_id, 5);
        if (mpTerm == null && mpDbTerm == null) {
            throw new OntologyTermNotFoundException("", phenotype_id);
        }

        Set<OntologyTerm> mpSiblings = new HashSet<OntologyTerm>();
        Set<OntologyTerm> goTerms = new HashSet<OntologyTerm>();
        Set<Synonym> synonymTerms = new HashSet<Synonym>();
        Set<SimpleOntoTerm> computationalHPTerms = new HashSet<SimpleOntoTerm>();

        try {

        	JSONArray docs = solrIndex
                    .getMpData(phenotype_id)
                    .getJSONObject("response")
                    .getJSONArray("docs");

        	int nbDocs = docs.size();

        	if ( nbDocs == 0 && (preqcService.getGenesBy(phenotype_id, null, true).isEmpty() || preqcService.getGenesBy(phenotype_id, null, true) == null)) {
        		model.addAttribute("hasData", false);
        	} else {
	        	model.addAttribute("hasData", true);

	            JSONObject mpData = docs.getJSONObject(0);
	            JSONArray terms;

	            if (mpData.containsKey("mp_term_synonym")) {
	                JSONArray syonymsArray = mpData.getJSONArray("mp_term_synonym");
	                for (Object syn : syonymsArray) {
	                    String synm = (String) syn;
	                    Synonym synonym = new Synonym();
	                    synonym.setSymbol(synm);
	                    synonymTerms.add(synonym);
	                }
	            }

	            if (mpData.containsKey("hp_term")) {
	            	computationalHPTerms = mpService.getComputationalHPTerms(mpData);
	            }

	            if (mpData.containsKey("sibling_mp_id")) {
	                terms = mpData.getJSONArray("sibling_mp_id");
	                for (Object obj : terms) {
	                    String id = (String) obj;
	                    if ( ! id.equals(phenotype_id)) {
	                        mpSiblings.add(ontoTermDao.getOntologyTermByAccessionAndDatabaseId(id, 5));
	                    }
	                }
	            }

	            if (mpData.containsKey("go_id")) {
	                terms = mpData.getJSONArray("go_id");
	                for (Object obj : terms) {
	                    String id = (String) obj;
	                    goTerms.add(ontoTermDao.getOntologyTermByAccessionAndDatabaseId(id, 11));
	                }
	            }

	        }

        } catch (Exception e) {
            e.printStackTrace();
            mpSiblings = new HashSet<OntologyTerm>();
        }

        // register interest state
 		RegisterInterestDrupalSolr registerInterest = new RegisterInterestDrupalSolr(config.get("drupalBaseUrl"), request);
 		Map<String, String> regInt = registerInterest.registerInterestState(phenotype_id, request, registerInterest);
 		
 		model.addAttribute("registerInterestButtonString", regInt.get("registerInterestButtonString"));
 		model.addAttribute("registerButtonAnchor", regInt.get("registerButtonAnchor"));
 		model.addAttribute("registerButtonId", regInt.get("registerButtonId"));

 		// other stuff
        model.addAttribute("go", goTerms);
        model.addAttribute("siblings", mpSiblings);
        model.addAttribute("synonyms", synonymTerms);
        model.addAttribute("hpTerms", computationalHPTerms);
        // Query the images for this phenotype
        QueryResponse response = imagesSolrDao.getDocsForMpTerm(phenotype_id, 0, numberOfImagesToDisplay);
        model.addAttribute("numberFound", response.getResults().getNumFound());
        model.addAttribute("images", response.getResults());

        processPhenotypes(phenotype_id, null, null, null, model, request);

        
        model.addAttribute("isLive", new Boolean((String) request.getAttribute("liveSite")));
        model.addAttribute("phenotype", mpTerm);
        
	    List<ImpressDTO> procedures = new ArrayList<ImpressDTO>(impressService.getProceduresByMpTerm(phenotype_id));
	    Collections.sort(procedures, ImpressDTO.getComparatorByProcedureNameImpcFirst());
	    model.addAttribute("procedures", procedures);

        model.addAttribute("genePercentage", getPercentages(phenotype_id));

        model.addAttribute("parametersAssociated", getParameters(phenotype_id));

        // Stuff for parent-child display
        model.addAttribute("hasChildren", mpService.getChildren(phenotype_id).size() > 0 ? true : false);
        model.addAttribute("hasParents", mpService.getParents(phenotype_id).size() > 0 ? true : false);
        
        return "phenotypes";
    }
    

    private Map<String, Map<String, Integer>> sortPhenFacets(Map<String, Map<String, Integer>> phenFacets) {
    	
        Map<String, Map<String, Integer>> sortPhenFacets = phenFacets;
        for (String key : phenFacets.keySet()) {
            sortPhenFacets.put(key, new TreeMap<String, Integer>(phenFacets.get(key)));
        }
        return sortPhenFacets;
    }
    

    /**
     *
     * @param phenotype_id
     * @param filter
     * @param model
     * @param request
     * @throws IOException
     * @throws URISyntaxException
     * @throws SolrServerException 
     */
    private void processPhenotypes(String phenotype_id, List<String> procedureName,  List<String> markerSymbol,  List<String> mpTermName, Model model, HttpServletRequest request) 
    throws IOException, URISyntaxException, SolrServerException {
    	
        
    	List<PhenotypeCallSummaryDTO> phenotypeList;
        Set<String> errorCodes = new HashSet();
        
        try {
        	
            PhenotypeFacetResult phenoResult = phenoDAO.getPhenotypeCallByMPAccessionAndFilter(phenotype_id,  procedureName, markerSymbol, mpTermName);
            PhenotypeFacetResult preQcResult = phenoDAO.getPreQcPhenotypeCallByMPAccessionAndFilter(phenotype_id,  procedureName, markerSymbol, mpTermName);

            phenotypeList = phenoResult.getPhenotypeCallSummaries();
            phenotypeList.addAll(preQcResult.getPhenotypeCallSummaries());

            Map<String, Map<String, Integer>> phenoFacets = phenoResult.getFacetResults();
            Map<String, Map<String, Integer>> preQcFacets = preQcResult.getFacetResults();

			for (String key : preQcFacets.keySet()){
				if (preQcFacets.get(key).keySet().size() > 0){
					for (String key2: preQcFacets.get(key).keySet()){
						phenoFacets.get(key).put(key2, preQcFacets.get(key).get(key2));
					}
				}
			}
			
			errorCodes.addAll(phenoResult.getErrorCodes());
			errorCodes.addAll(preQcResult.getErrorCodes());
			String errorMessage = null;
			if (errorCodes != null && errorCodes.size() > 0){
				errorMessage = "There was a problem retrieving some of the phenotype calls. Some rows migth be missing from the table below. Error code(s) " +
				StringUtils.join(errorCodes, ", ") + ".";
			}
			
            phenoFacets = sortPhenFacets(phenoFacets);
            model.addAttribute("phenoFacets", phenoFacets);
            model.addAttribute("errorMessage", errorMessage);

        } catch (HibernateException | JSONException e) {
            log.error("ERROR GETTING PHENOTYPE LIST");
            e.printStackTrace();
            phenotypeList = new ArrayList<PhenotypeCallSummaryDTO>();
        }

        // This is a map because we need to support lookups
        Map<Integer, DataTableRow> phenotypes = new HashMap<Integer, DataTableRow>();

        for (PhenotypeCallSummaryDTO pcs : phenotypeList) {

            // On the phenotype pages we only display stats graphs as evidence, the MPATH links can't be linked from phen pages
            DataTableRow pr = new PhenotypePageTableRow(pcs, request.getAttribute("baseUrl").toString(), config, false);

	        // Collapse rows on sex
            if (phenotypes.containsKey(pr.hashCode())) {
            	
                pr = phenotypes.get(pr.hashCode());
                // Use a tree set to maintain an alphabetical order (Female, Male)
                TreeSet<String> sexes = new TreeSet<String>();
                for (String s : pr.getSexes()) {
                    sexes.add(s);
                }
                sexes.add(pcs.getSex().toString());
                
                pr.setSexes(new ArrayList<String>(sexes));
            }

            if (pr.getParameter() != null && pr.getProcedure() != null) {
                phenotypes.put(pr.hashCode(), pr);
            }
        }

        List<DataTableRow> list = new ArrayList<DataTableRow>(phenotypes.values());
        Collections.sort(list);

        model.addAttribute("phenotypes", list);

        // Check the number of document first
        JSONArray docs = solrIndex
                .getMpData(phenotype_id)
                .getJSONObject("response")
                .getJSONArray("docs");

        int nb = docs.size();

        if (nb == 0) {
        	model.addAttribute("isImpcTerm", false);
        } else {

        JSONObject mpData = docs.getJSONObject(0);

        if (mpData.containsKey("ontology_subset")) {
            model.addAttribute("isImpcTerm", mpData.getJSONArray("ontology_subset").contains("IMPC_Terms"));
        } else {
            model.addAttribute("isImpcTerm", false);
        }
        }

    }

    @ExceptionHandler(OntologyTermNotFoundException.class)
    public ModelAndView handleOntologyTermNotFoundException(OntologyTermNotFoundException exception) {
        ModelAndView mv = new ModelAndView("identifierError");
        mv.addObject("errorMessage", exception.getMessage());
        mv.addObject("acc", exception.getAcc());
        mv.addObject("type", "mammalian phenotype");
        mv.addObject("exampleURI", "/phenotypes/MP:0000585");
        return mv;
    }

    @ExceptionHandler(Exception.class)
    public ModelAndView handleGenericException(Exception exception) {
        exception.printStackTrace();
        ModelAndView mv = new ModelAndView("identifierError");
        mv.addObject("errorMessage", exception.getMessage());
        mv.addObject("acc", "");
        mv.addObject("type", "mammalian phenotype");
        mv.addObject("exampleURI", "/phenotypes/MP:0000585");
        return mv;
    }

    @RequestMapping("/geneVariantsWithPhenotypeTable/{acc}") // Keep params in synch with export()
    public String geneVariantsWithPhenotypeTable(
            @PathVariable String acc,
			@RequestParam(required = false, value = "procedure_name")  List<String> procedureName,
			@RequestParam(required = false, value = "marker_symbol")  List<String> markerSymbol,
			@RequestParam(required = false, value = "mp_term_name")  List<String> mpTermName,			
            Model model,
            HttpServletRequest request,
            RedirectAttributes attributes) throws KeyManagementException, NoSuchAlgorithmException, URISyntaxException, GenomicFeatureNotFoundException, IOException, SolrServerException {
        
        processPhenotypes(acc, procedureName, markerSymbol, mpTermName, model, request);
        return "geneVariantsWithPhenotypeTable";
    }

    
    /**
     * @author ilinca
     * @since 2016/05/05
     * @throws IOException
     * @throws URISyntaxException
     * @throws SolrServerException
     */
    @RequestMapping("/phenotypes/export/{acc}") // Keep params in synch with geneVariantsWithPhenotypeTable()
    public void export(
            @PathVariable String acc,
			@RequestParam(required = true, value = "fileType") String fileType,
			@RequestParam(required = true, value = "fileName") String fileName,
			@RequestParam(required = false, value = "procedure_name")  List<String> procedureName,
			@RequestParam(required = false, value = "marker_symbol")  List<String> markerSymbol,
			@RequestParam(required = false, value = "mp_term_name")  List<String> mpTermName,			
            Model model,
            HttpServletRequest request,
			HttpServletResponse response,
            RedirectAttributes attributes) 
    throws IOException, URISyntaxException, SolrServerException {
            
        PhenotypeFacetResult phenoResult = phenoDAO.getPhenotypeCallByMPAccessionAndFilter(acc, procedureName, markerSymbol, mpTermName);
        List<PhenotypeCallSummaryDTO> phenotypeList = phenoResult.getPhenotypeCallSummaries();
        List<PhenotypePageTableRow> phenotypes = new ArrayList<PhenotypePageTableRow>();
        String url =  request.getAttribute("mappedHostname").toString() + request.getAttribute("baseUrl").toString();
        
        for (PhenotypeCallSummaryDTO pcs : phenotypeList) {
            List<String> sex = new ArrayList<String>();
            sex.add(pcs.getSex().toString());
            // On the phenotype pages we only display stats graphs as evidence, the MPATH links can't be linked from phen pages
            PhenotypePageTableRow pr = new PhenotypePageTableRow(pcs, url, config, false);
            phenotypes.add(pr);
        } 
        
        List<String> dataRows = new ArrayList<>();
		dataRows.add(PhenotypePageTableRow.getTabbedHeader());
		for (PhenotypePageTableRow row : phenotypes) {
			dataRows.add(row.toTabbedString());
		}
		
		FileExportUtils.writeOutputFile(response, dataRows, fileType, fileName);
    }
    
    @RequestMapping("/mpTree/{mpId}")
    public String getChildParentTree(
            @PathVariable String mpId,
            Model model,
            HttpServletRequest request,
            RedirectAttributes attributes) 
    throws KeyManagementException, NoSuchAlgorithmException, URISyntaxException, GenomicFeatureNotFoundException, IOException, SolrServerException {
        
    	model.addAttribute("ontId", mpId);
        return "pageTree";
    }    
    
    
    /**
     * @author ilinca
     * @since 2016/03/22
     * @param mpId
     * @param type
     * @param model
     * @return
     * @throws SolrServerException
     * @throws IOException
     * @throws URISyntaxException
     */
    @RequestMapping(value="/mpTree/json/{mpId}", method=RequestMethod.GET)	
    public @ResponseBody String getParentChildren( @PathVariable String mpId, @RequestParam(value = "type", required = true) String type, Model model) 
    throws SolrServerException, IOException, URISyntaxException {
    	
    	if (type.equals("parents")){
    	
	    	JSONObject data = new JSONObject();
	    	data.element("id", mpId);
	    	JSONArray nodes = new JSONArray();
	    
	    	for (OntologyBean term : mpService.getParents(mpId)){
	    		nodes.add(term.toJson());
	    	}

	    	data.element("children", nodes);
			return data.toString();
			
    	} else if (type.equals("children")){
    		
    		JSONObject data = new JSONObject();
        	data.element("id", mpId);
        	JSONArray nodes = new JSONArray();

        	for (OntologyBean term : mpService.getChildren(mpId)){
	    		nodes.add(term.toJson());
	    	}
        	
        	data.element("children", nodes);
    		return data.toString();
    	}
    	return "";
    }
        
    
    public JSONObject getJsonObj(String name, String type){
    	return new JSONObject().element("name", name).element(type, true);
    }
    
    
    public PhenotypeGeneSummaryDTO getPercentages(String phenotype_id) throws SolrServerException { // <sex, percentage>
        PhenotypeGeneSummaryDTO pgs = new PhenotypeGeneSummaryDTO();

        int total = 0;
        int nominator = 0;

        nominator = gpService.getGenesBy(phenotype_id, null, false).size();
        total = srService.getGenesBy(phenotype_id, null).size();
        pgs.setTotalPercentage(100 * (float) nominator / (float) total);
        pgs.setTotalGenesAssociated(nominator);
        pgs.setTotalGenesTested(total);
        boolean display = (total > 0);
        pgs.setDisplay(display);

        List<String> genesFemalePhenotype = new ArrayList<String>();
        List<String> genesMalePhenotype = new ArrayList<String>();
        List<String> genesBothPhenotype;

        if (display) {
            for (Group g : gpService.getGenesBy(phenotype_id, "female", false)) {
                genesFemalePhenotype.add((String) g.getGroupValue());
            }
            nominator = genesFemalePhenotype.size();
            total = srService.getGenesBy(phenotype_id, SexType.female).size();
            pgs.setFemalePercentage(100 * (float) nominator / (float) total);
            pgs.setFemaleGenesAssociated(nominator);
            pgs.setFemaleGenesTested(total);

            for (Group g : gpService.getGenesBy(phenotype_id, "male", false)) {
                genesMalePhenotype.add(g.getGroupValue());
            }
            nominator = genesMalePhenotype.size();
            total = srService.getGenesBy(phenotype_id, SexType.male).size();
            pgs.setMalePercentage(100 * (float) nominator / (float) total);
            pgs.setMaleGenesAssociated(nominator);
            pgs.setMaleGenesTested(total);
        }

        genesBothPhenotype = new ArrayList<String>(genesFemalePhenotype);
        genesBothPhenotype.retainAll(genesMalePhenotype);
        genesFemalePhenotype.removeAll(genesBothPhenotype);
        genesMalePhenotype.removeAll(genesBothPhenotype);
        pgs.setBothNumber(genesBothPhenotype.size());
        pgs.setFemaleOnlyNumber(genesFemalePhenotype.size());
        pgs.setMaleOnlyNumber(genesMalePhenotype.size());
        pgs.fillPieChartCode();

        return pgs;
    }

    /**
     *
     * @param mpId
     * @return List of parameters that led to an association to the given
     * phenotype term or any of it's children
     * @throws SolrServerException
     */
    public List<ParameterDTO> getParameters(String mpId) 
    throws SolrServerException {
    
    	List<String> parameters = srService.getParametersForPhenotype(mpId);
    	List<ParameterDTO> res =  new ArrayList<>();
    	for (String parameterStableId : parameters){
    		ParameterDTO param = impressService.getParameterByStableId(parameterStableId);
    		if (param.getObservationType().equals(ObservationType.categorical) && (param.getStableId().contains("_VIA_") || param.getStableId().contains("_FER_"))){
    			res.add(param);
    		} else if (param.getObservationType().equals(ObservationType.unidimensional)){
    			res.add(param);
    		} 
    	}
        Collections.sort(res, ImpressBaseDTO.getComparatorByNameImpcFirst());

        return res;
    }

}
