package uk.ac.ebi.phenotype.web.controller;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.Group;
import org.hibernate.exception.DataException;
import org.mousephenotype.cda.solr.generic.util.PhenotypeFacetResult;
import org.mousephenotype.cda.solr.service.*;
import org.mousephenotype.cda.solr.service.dto.CountTableRow;
import org.mousephenotype.cda.solr.service.dto.GeneDTO;
import org.mousephenotype.cda.solr.service.dto.ImpressDTO;
import org.mousephenotype.cda.solr.service.dto.MpDTO;
import org.mousephenotype.cda.solr.web.dto.PhenotypeCallSummaryDTO;
import org.mousephenotype.cda.solr.web.dto.PhenotypePageTableRow;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

import net.minidev.json.parser.JSONParser;

// import com.google.common.io.Files;

import uk.ac.ebi.phenotype.bean.LandingPageDTO;
import uk.ac.ebi.phenotype.chart.AnalyticsChartProvider;
import uk.ac.ebi.phenotype.chart.CmgColumnChart;
import uk.ac.ebi.phenotype.chart.ScatterChartAndTableProvider;
import uk.ac.ebi.phenotype.error.OntologyTermNotFoundException;
import uk.ac.ebi.phenotype.web.util.FileExportUtils;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;


/**
 * Created by ilinca on 24/10/2016.
 */

@Controller
public class LandingPageController {

    @NotNull
    @Value("${impc_media_base_url}")
    private String impcMediaBaseUrl;

    private ObservationService os;
    private PostQcService gpService;
    private StatisticalResultService srService;
    private GeneService geneService;
    private ImageService imageService;
    private PhenodigmService phenodigmService;
    private MpService mpService;
    private ImpressService is;

    @Inject
    public LandingPageController(ObservationService os, PostQcService gpService, StatisticalResultService srService, GeneService geneService, ImageService imageService, PhenodigmService phenodigmService, MpService mpService, ImpressService is) {

        Assert.notNull(os, "Observationservice cannot be null");
        Assert.notNull(gpService, "Genotype phenotype service cannot be null");
        Assert.notNull(srService, "Statistical result service cannot be null");
        Assert.notNull(geneService, "Gene service cannot be null");
        Assert.notNull(imageService, "Image service cannot be null");
        Assert.notNull(phenodigmService, "Phenodigm service cannot be null");
        Assert.notNull(mpService, "MP service cannot be null");
        Assert.notNull(is, "Impress service cannot be null");

        this.os = os;
        this.gpService = gpService;
        this.srService = srService;
        this.geneService = geneService;
        this.imageService = imageService;
        this.phenodigmService = phenodigmService;
        this.mpService = mpService;
        this.is = is;

    }


//	@RequestMapping("/biological-system")
//	public String getAlleles(Model model, HttpServletRequest request) throws IOException {
//
//        String baseUrl = request.getAttribute("baseUrl").toString();
//
//        List<LandingPageDTO> bsPages = new ArrayList<>();
//        LandingPageDTO cardiovascular = new LandingPageDTO();
//
//        cardiovascular.setTitle("Cardiovascular");
//        cardiovascular.setImage(impcMediaBaseUrl + "/render_thumbnail/211474/400/");
//        cardiovascular.setDescription("This page aims to present cardiovascular system related phenotypes lines which have been produced by IMPC.");
//        cardiovascular.setLink("biological-system/cardiovascular");
//        bsPages.add(cardiovascular);
//
//        // don't show deafness or vision pages on live until ready
//        Boolean isLive= Boolean.valueOf((String) request.getAttribute("liveSite"));
//        
//            LandingPageDTO deafness = new LandingPageDTO();
//            deafness.setTitle("Hearing");
//            deafness.setImage(baseUrl + "/img/landing/deafnessIcon.png");
//            deafness.setDescription("This page aims to relate deafnessnes to phenotypes which have been produced by IMPC.");
//            deafness.setLink("biological-system/hearing");
//            bsPages.add(deafness);
//            if(!isLive){
//	            LandingPageDTO vision = new LandingPageDTO();
//	            vision.setTitle("Vision");
//	            vision.setImage(baseUrl + "/img/landing/deafnessIcon.png");
//	            vision.setDescription("This page aims to relate vision to phenotypes which have been produced by IMPC.");
//	            vision.setLink("biological-system/vision");
//	            bsPages.add(vision);
//	            
//	            LandingPageDTO metabolism = new LandingPageDTO();
//	            metabolism.setTitle("Metabolism");
//	            metabolism.setImage(baseUrl + "/img/landing/deafnessIcon.png");
//	            metabolism.setDescription("This page aims to relate metabolism to phenotypes which have been produced by IMPC.");
//	            metabolism.setLink("biological-system/metabolism");
//	            bsPages.add(metabolism);
//	            
//	            LandingPageDTO cmg = new LandingPageDTO();
//	            cmg.setTitle("Center for Mendelian Genomics ");
//	            cmg.setImage(baseUrl + "/img/landing/cmg-logo_1.png");
//	            cmg.setDescription("This page aims to relate CMG mouse lines to phenotypes which have been produced by IMPC.");
//	            cmg.setLink("biological-system/cmg");
//	            bsPages.add(cmg);
//            }
//
//        model.addAttribute("pages", bsPages);
//
//        return "landing";
//	}

    @RequestMapping(value = "/embryo", method = RequestMethod.GET)
    public String loadEmbryoPage(Model model, HttpServletRequest request, RedirectAttributes attributes)
            throws OntologyTermNotFoundException, IOException, URISyntaxException, SolrServerException, SQLException {

        AnalyticsChartProvider chartsProvider = new AnalyticsChartProvider();
        List<String> resources = Arrays.asList( "IMPC" );
        Map<String, Set<String>> viabilityRes = os.getViabilityCategories(resources, true);

        Map<String, Long> viabilityMap = os.getViabilityCategories(viabilityRes);
        List<CountTableRow> viabilityTable = os.consolidateZygosities(viabilityRes);

        model.addAttribute("viabilityChart", chartsProvider.getSlicedPieChart(new HashMap<String, Long>(), viabilityMap, "", "viabilityChart"));
        model.addAttribute("viabilityTable", viabilityTable);

        return "embryo";
    }

    @RequestMapping(value = "/biological-system/pain", method = RequestMethod.GET)
    public String loadPainBiologicalSystemPage(Model model, HttpServletRequest request, RedirectAttributes attributes) {
	    	System.out.println("pain page called");
	    	return "landing_pain";
    }
    
    @RequestMapping(value = "/biological-system/conservation", method = RequestMethod.GET)
    public String loadPainBiologicalSystemPage(Model model, HttpServletRequest request, RedirectAttributes attributes) {
	    	System.out.println("conservation page called");
	    	return "landing_conservation";
    }

    @RequestMapping(value = "/biological-system/{page}", method = RequestMethod.GET)
    public String loadBiologicalSystemPage(@PathVariable String page, Model model, HttpServletRequest request, RedirectAttributes attributes)
            throws OntologyTermNotFoundException, IOException, URISyntaxException, SolrServerException, SQLException, ExecutionException, InterruptedException, JSONException {

        String pageTitle = "";
        String baseUrl = request.getAttribute("baseUrl").toString();
        List<String> resources = new ArrayList<>();
        resources.add("IMPC");
        List<String> anatomyIds = new ArrayList<>(); // corresponding anatomical system, used for images
        MpDTO mpDTO = null;
        ArrayList<JSONObject> cmg_genes = null;

        if (page.equalsIgnoreCase("hearing")) { // Need to decide if we want deafness only or top level hearing/vestibular phen
            mpDTO = mpService.getPhenotype("MP:0005377");
            anatomyIds.add("MA:0002443");
            anatomyIds.add("EMAPA:36002");
            pageTitle = "Hearing";
        } else if (page.equalsIgnoreCase("cardiovascular")) {
            mpDTO = mpService.getPhenotype("MP:0005385");
            anatomyIds.add("MA:0000010");
            anatomyIds.add("EMAPA:16104");
            pageTitle = "Cardiovascular System";
        } else if (page.equalsIgnoreCase("vision")) {
        		mpDTO = mpService.getPhenotype("MP:0005391");
          	anatomyIds.add("EMAPA:36003");
          	anatomyIds.add("MA:0002444");
          	pageTitle = "Vision";
        } else if (page.equalsIgnoreCase("metabolism")) {
            mpDTO = mpService.getPhenotype("MP:0005376");
            pageTitle = "Metabolism";
        } else if (page.equalsIgnoreCase("cmg")) {
        		// mpDTO = mpService.getPhenotype("MP:0000001");
        		pageTitle = "Centers for Mendelian Genomics";
        		String phenotypeOverlapScoreFile = "cmg_best_phenodigm.json";
			String cmgOrthologuesJsonFile = "cmg_orthologues_json.json";
			
			cmg_genes = GetCmgGenes(cmgOrthologuesJsonFile);
			cmg_genes = GetBestPhenodigm(phenotypeOverlapScoreFile, cmg_genes);
			cmg_genes = GetLatestProjectStatus(cmg_genes);
			
			// System.out.println(cmg_genes);
			
        }
        
        //else if (page.equalsIgnoreCase("vision")) {
//            mpDTO = mpService.getPhenotype("MP:0005391");
//            anatomyIds.add("EMAPA:36003");
//            anatomyIds.add("MA:0002444");
//            pageTitle = "Vision";
//        } else if (page.equalsIgnoreCase("nervous")) {
//            mpDTO = mpService.getPhenotype("MP:0003631");
//            anatomyIds.add("MA:0000016");
//            anatomyIds.add("EMAPA:16469");
//            pageTitle = "Nervous phenotypes";
//        } else if (page.equalsIgnoreCase("neurological")) {
//            mpDTO = mpService.getPhenotype("MP:0005386");
//            pageTitle = "Behavioural/neurological phenotypes";
//        }

        // IMPC image display at the bottom of the page
        if (mpDTO != null) {
	        List<Group> groups = imageService.getPhenotypeAssociatedImages(null, mpDTO.getMpId(), anatomyIds, true, 1);
	        Map<String, String> paramToNumber = new HashMap<>();
	        for (Group group : groups) {
	            if (!paramToNumber.containsKey(group.getGroupValue())) {
	                paramToNumber.put(group.getGroupValue(), Long.toString(group.getResult().getNumFound()));
	            }
	        }

	        List<ImpressDTO> procedures = new ArrayList<>();
	        procedures.addAll(is.getProceduresByMpTerm(mpDTO.getMpId(), true));

	        // Per Terry 2017-08-31
	        // On the hearing landing page, filter out all procedures except Shirpa and ABR
	        if (page.equalsIgnoreCase("hearing")) {
	            procedures = procedures
	                    .stream()
	                    .filter(x -> "Combined SHIRPA and Dysmorphology".equals(x.getProcedureName()) || "Auditory Brain Stem Response".equals(x.getProcedureName()))
	                    .collect(Collectors.toList());
	            model.addAttribute("adultOnly", true);
	        }
	        
	        // Per Alba 2017-11-07
	        // On the metabolism landing page, filter out all procedures except Gross Path and Tissue Collect
	        if (page.equalsIgnoreCase("metabolism")) {
	            procedures = procedures
	                    .stream()
	                    .filter(x -> !"Gross Pathology and Tissue Collection".equals(x.getProcedureName()))
	                    .collect(Collectors.toList());
	            model.addAttribute("adultOnly", true);
	        }
	
	        Collections.sort(procedures, ImpressDTO.getComparatorByProcedureName());
	        String description="for genes with at least one " + pageTitle + " phenotype";
	        
	        Set<String>filterOnMarkerAccession=null;
	        if(page.equalsIgnoreCase("hearing")){
		        	filterOnMarkerAccession = getHearingPublicationGeneSet();
		        	description="for the 67 genes in the gene table above";
	        }
        		
        		model.addAttribute("paramToNumber", paramToNumber);
        		model.addAttribute("impcImageGroups", groups);
        		model.addAttribute("phenotypeChart", ScatterChartAndTableProvider.getScatterChart("phenotypeChart", gpService.getTopLevelPhenotypeIntersection(mpDTO.getMpId(), filterOnMarkerAccession), "Gene pleiotropy",
                    description, "Number of phenotype associations to " + pageTitle, "Number of associations to other phenotypes",
                    "Other phenotype calls: ", pageTitle + " phenotype calls: ", "Gene"));
	        model.addAttribute("genePercentage", ControllerUtils.getPercentages(mpDTO.getMpId(), srService, gpService));
	        model.addAttribute("phenotypes", gpService.getAssociationsCount(mpDTO.getMpId(), resources));
	        model.addAttribute("mpId", mpDTO.getMpId());
	        model.addAttribute("mpDTO", mpDTO);
	
	        String systemNAme = mpDTO.getMpTerm();
	        if (mpDTO.getMpTerm().contains("hearing/vestibular/ear")) {
	            systemNAme = "hearing";
	        }
	        
	        model.addAttribute("systemName", systemNAme.replace(" phenotype", ""));
	        model.addAttribute("procedures", procedures);
        }
        
        model.addAttribute("pageTitle", pageTitle);
        
        if (cmg_genes != null) {
        	 	model.addAttribute("cmg_genes", cmg_genes);
        	 	model.addAttribute("columnChart1", CmgColumnChart.getColumnChart(cmg_genes, "tier1", "columnChart1", "CMG Tier 1 candidates", ""));
        	 	model.addAttribute("columnChart2", CmgColumnChart.getColumnChart(cmg_genes, "tier2", "columnChart2", "CMG Tier 2 candidates", ""));
        }

//        model.addAttribute("dataJs", getData(null, null, null, mpDTO.getAccession(), request) + ";");

        return "landing_" + page;

    }
    
    
    
    private ArrayList<JSONObject> GetLatestProjectStatus(ArrayList<JSONObject> cmg_genes_bestphenodigm) throws SolrServerException, IOException {
    		for (JSONObject gene : cmg_genes_bestphenodigm) {
    			if (!gene.isNull("mouse_orthologue") && !gene.get("mouse_orthologue").equals("") && !gene.get("mouse_orthologue").equals("-")) {
    				String mouse_orthologue = gene.get("mouse_orthologue").toString();
    				String latestProjectStatus = geneService.getLatestProjectStatusForGeneSet(mouse_orthologue);
    				gene.remove("impc_status");
    				gene.put("impc_status", latestProjectStatus);
    			} 
    		}
    		return cmg_genes_bestphenodigm;
    }
    
    private ArrayList<JSONObject> GetBestPhenodigm (String phenotypeOverlapScoreFile, ArrayList<JSONObject> cmg_genes_information) throws IOException {
    		// reads from /src/main/resources/20171206-CMG-best-phenodigm.json and compose the page
    		BufferedReader in = new BufferedReader(new FileReader(new ClassPathResource(phenotypeOverlapScoreFile).getFile()));
    		if (in != null) {
    			String json = in.lines().collect(Collectors.joining(" "));
    			JSONArray info = null;
    			try {
    				info = new JSONArray(json);
    			} catch (JSONException e) {
    				e.printStackTrace();
    			}
    			
    			for (int i = 0; i < info.length(); i++) {
    				JSONObject jsonObj = null;
    				try {
    					jsonObj = info.getJSONObject(i);
    				} catch (JSONException e) {
    					e.printStackTrace();
    				}
				if (!jsonObj.isNull("gene_id") && !jsonObj.isNull("CMG_disease")) {
					String gene_id = jsonObj.getString("gene_id");
					String CMG_disease = jsonObj.getString("CMG_disease");
					for (JSONObject gene : cmg_genes_information) {
						if (!gene.isNull("mouse_orthologue") && !gene.isNull("omim_id")) {
							String mouse_orthologue = gene.getString("mouse_orthologue");
							String omim_id = gene.getString("omim_id");
							if (gene_id.equals(mouse_orthologue) && CMG_disease.equals(omim_id)) {  
								if (!jsonObj.isNull("best_phenoscore_IMPC")) {
									gene.remove("impc_mouse");
									gene.put("impc_mouse", jsonObj.get("best_phenoscore_IMPC"));
								} 
								if (!jsonObj.isNull("best_phenoscore_MGI")) {
									gene.remove("published_mouse");
									gene.put("published_mouse", jsonObj.get("best_phenoscore_MGI"));
								} 
								continue;
							} 
						} 
					}
				}
    			}
		}
    		return cmg_genes_information;
    }
    
    private ArrayList<JSONObject> GetCmgGenes (String file_path_genes) throws IOException {
    		ArrayList<JSONObject> file_content = new ArrayList<JSONObject>();
    		// reads from /src/main/resources/cmg_orthologues_json.json and compose the page
    		BufferedReader in = new BufferedReader(new FileReader(new ClassPathResource(file_path_genes).getFile()));
    		if (in != null) {
    			String json = in.lines().collect(Collectors.joining(" "));
    			JSONArray genes = null;
    			try {
    				genes = new JSONArray(json);
    			} catch (JSONException e) {
    				e.printStackTrace();
    			}
    			for (int i = 0; i < genes.length(); i++) {
    				JSONObject jsonObj = null;
    				JSONObject jsonObjFiltered = new JSONObject(); 
    				try {
    					jsonObj = genes.getJSONObject(i);
    				} catch (JSONException e) {
    					e.printStackTrace();
    				}
    				try {
    					if (!jsonObj.isNull("Phenotype")) {
    						jsonObjFiltered.put("disease", jsonObj.getString("Phenotype"));
    					} else {
    						jsonObjFiltered.put("disease", "");
    					}
    				} catch (JSONException e) {
    					e.printStackTrace();
    				}
    				try {
    					if (!jsonObj.isNull("OMIM")) {
    						jsonObjFiltered.put("omim_id", jsonObj.getString("OMIM"));
    					} else {
    						jsonObjFiltered.put("omim_id", "");
    					}
    					
    				} catch (JSONException e) {
    					e.printStackTrace();
    				}
    				try {
    					if (!jsonObj.isNull("Tier_1_Gene")) {
    						jsonObjFiltered.put("tier_1_gene", jsonObj.getString("Tier_1_Gene"));
    					} else {
    						jsonObjFiltered.put("tier_1_gene", "");
    					}
    				} catch (JSONException e) {
    					e.printStackTrace();
    				}
    				try {
    					if (!jsonObj.isNull("Tier_2_Gene")) {
    						jsonObjFiltered.put("tier_2_gene", jsonObj.getString("Tier_2_Gene"));
    					} else {
    						jsonObjFiltered.put("tier_2_gene", "");
    					}
    					
    				} catch (JSONException e) {
    					e.printStackTrace();
    				}
    				try {
    					if (!jsonObj.isNull("Approved_symbol")) {
    						jsonObjFiltered.put("approved_symbol", jsonObj.getString("Approved_symbol"));
    					} else {
    						jsonObjFiltered.put("approved_symbol", "");
    					}
    				} catch (JSONException e) {
    					e.printStackTrace();
    				}
    				try {
    					if (!jsonObj.isNull("HGNC_ID")) {
    						jsonObjFiltered.put("hgnc_id", jsonObj.getString("HGNC_ID"));
    					} else {
    						jsonObjFiltered.put("hgnc_id", "");
    					}
    				} catch (JSONException e) {
    					e.printStackTrace();
    				}
    				try {
    					if (!jsonObj.isNull("mgi_id")) {
    						jsonObjFiltered.put("mouse_orthologue", jsonObj.getString("mgi_id"));
    					} else {
    						jsonObjFiltered.put("mouse_orthologue", "");
    					}
    				} catch (JSONException e) {
    					e.printStackTrace();
    				}
    				try {
    					if (!jsonObj.isNull("link_IMPC")) {
    						jsonObjFiltered.put("link_IMPC", jsonObj.getString("link_IMPC"));
    					} else {
    						jsonObjFiltered.put("link_IMPC", "");
    					}
    				} catch (JSONException e) {
    					e.printStackTrace();
    				}
    				try {
    					if (!jsonObj.isNull("support_count")) {
    						jsonObjFiltered.put("support_count", jsonObj.get("support_count"));
    					} else {
    						jsonObjFiltered.put("support_count", "");
    					}
    				} catch (JSONException e) {
    					e.printStackTrace();
    				}
    				try {
    					if (!jsonObj.isNull("support")) {
    						String list_inferences = jsonObj.getString("support").replaceAll(",", ", ");
    						jsonObjFiltered.put("support", list_inferences);
    					} else {
    						jsonObjFiltered.put("support", "");
    					}
    				} catch (JSONException e) {
    					e.printStackTrace();
    				}
    				jsonObjFiltered.put("impc_mouse", "NA");
    				jsonObjFiltered.put("published_mouse", "NA");
    				jsonObjFiltered.put("impc_status", "");
    				file_content.add(jsonObjFiltered);    			
    			}
    		}
    		return file_content;
    }

	private Set<String> getHearingPublicationGeneSet() {
		Set<String> filterOnMarkerAccession;
		filterOnMarkerAccession=new HashSet<String>();
		filterOnMarkerAccession.add("MGI:2442934");
				filterOnMarkerAccession.add("MGI:1098687");
				filterOnMarkerAccession.add("MGI:1354713");
				filterOnMarkerAccession.add("MGI:102806");
				filterOnMarkerAccession.add("MGI:1933736");
				filterOnMarkerAccession.add("MGI:1274784");
				filterOnMarkerAccession.add("MGI:107189");
				filterOnMarkerAccession.add("MGI:1924337");
				filterOnMarkerAccession.add("MGI:1929214");
				filterOnMarkerAccession.add("MGI:1337062");
				filterOnMarkerAccession.add("MGI:104653");
				filterOnMarkerAccession.add("MGI:3588238");
				filterOnMarkerAccession.add("MGI:2652819");
				filterOnMarkerAccession.add("MGI:1915589");
				filterOnMarkerAccession.add("MGI:106485");
				filterOnMarkerAccession.add("MGI:1929293");
				filterOnMarkerAccession.add("MGI:2388124");
				filterOnMarkerAccession.add("MGI:88466");
				filterOnMarkerAccession.add("MGI:2444415");
				filterOnMarkerAccession.add("MGI:103157");
				filterOnMarkerAccession.add("MGI:1914061");
				filterOnMarkerAccession.add("MGI:3583900");
				filterOnMarkerAccession.add("MGI:95321");
				filterOnMarkerAccession.add("MGI:1914675");
				filterOnMarkerAccession.add("MGI:99960");
				filterOnMarkerAccession.add("MGI:95662");
				filterOnMarkerAccession.add("MGI:2146207");
				filterOnMarkerAccession.add("MGI:2387006");
				filterOnMarkerAccession.add("MGI:2685519");
				filterOnMarkerAccession.add("MGI:1333877");
				filterOnMarkerAccession.add("MGI:1914393");
				filterOnMarkerAccession.add("MGI:96546");
				filterOnMarkerAccession.add("MGI:2146574");
				filterOnMarkerAccession.add("MGI:107953");
				filterOnMarkerAccession.add("MGI:2143315");
				filterOnMarkerAccession.add("MGI:2446166");
				filterOnMarkerAccession.add("MGI:1914249");
				filterOnMarkerAccession.add("MGI:1343489");
				filterOnMarkerAccession.add("MGI:1339711");
				filterOnMarkerAccession.add("MGI:104510");
				filterOnMarkerAccession.add("MGI:1933754");
				filterOnMarkerAccession.add("MGI:103296");
				filterOnMarkerAccession.add("MGI:105108");
				filterOnMarkerAccession.add("MGI:1928323");
				filterOnMarkerAccession.add("MGI:108077");
				filterOnMarkerAccession.add("MGI:97401");
				filterOnMarkerAccession.add("MGI:2686003");
				filterOnMarkerAccession.add("MGI:2149209");
				filterOnMarkerAccession.add("MGI:1918248");
				filterOnMarkerAccession.add("MGI:99878");
				filterOnMarkerAccession.add("MGI:1096347");
				filterOnMarkerAccession.add("MGI:2150150");
				filterOnMarkerAccession.add("MGI:2149330");
				filterOnMarkerAccession.add("MGI:2384936");
				filterOnMarkerAccession.add("MGI:1916205");
				filterOnMarkerAccession.add("MGI:2442082");
				filterOnMarkerAccession.add("MGI:1921050");
				filterOnMarkerAccession.add("MGI:2181659");
				filterOnMarkerAccession.add("MGI:2139535");
				filterOnMarkerAccession.add("MGI:1924817");
				filterOnMarkerAccession.add("MGI:102944");
				filterOnMarkerAccession.add("MGI:1914378");
				filterOnMarkerAccession.add("MGI:1919338");
				filterOnMarkerAccession.add("MGI:1855699");
				filterOnMarkerAccession.add("MGI:2685541");
				filterOnMarkerAccession.add("MGI:2159407");
				filterOnMarkerAccession.add("MGI:2444708");
		return filterOnMarkerAccession;
	}

// commented out all venn diagram code as
// (1) we do not show this data anymore
// (2) new phenodigm has fields removed that we used with old phenodigm. So need to update the code in the future if we bring the venn diagram back


//    @ResponseBody
//    @RequestMapping(value = "/orthology.jsonp", method = RequestMethod.GET)
//    public String getOrthologyJson(
//            @RequestParam(required = true, value = "mpId") String mpId,
//            @RequestParam( required =  true, value = "phenotypeShort") String phenotypeShort,
//            Model model,
//            HttpServletRequest request,
//            RedirectAttributes attributes)
//            throws OntologyTermNotFoundException, IOException, URISyntaxException, SolrServerException, SQLException, ExecutionException, InterruptedException {
//
//
//        Set<String> diseaseClasses = new HashSet<>();
//        diseaseClasses.add("cardiac");
//        diseaseClasses.add("cardiac malformations");
//        diseaseClasses.add("circulatory system");
//
//        return "var mgiSets =  " + getOrthologyDiseaseModelVennDiagram(mpId, diseaseClasses, true, false, false, phenotypeShort) + ";"
//            + "var impcSets = " +  getOrthologyDiseaseModelVennDiagram(mpId, diseaseClasses, false, true, false, phenotypeShort ) + ";";
//
//    }
//
//
//
//    @ResponseBody
//    @RequestMapping(value = "/orthology.tsv", method = RequestMethod.GET)
//    public String getOrthologyDownload(
//            @RequestParam(required = true, value = "mpId") String mpId,
//            @RequestParam( required =  true, value = "diseaseClasses") Set<String> diseaseClasses,
//            @RequestParam( required =  true, value = "phenotypeShort") String phenotypeShort,
//            Model model,
//            HttpServletRequest request,
//            RedirectAttributes attributes)
//            throws OntologyTermNotFoundException, IOException, URISyntaxException, SolrServerException, SQLException, ExecutionException, InterruptedException {
//
//        StringBuffer result = new StringBuffer();
//        result.append("IMPC sets\tSet label\tGenes\n");
//
//        JSONArray jsonArray = getOrthologyDiseaseModelVennDiagram(mpId, diseaseClasses, false, true, true, phenotypeShort);
//        for (int i = 0; i < jsonArray.size(); i++){
//            JSONObject object = jsonArray.getJSONObject(i);
//            result.append(object.getString("sets")).append("\t");
//            result.append(object.containsKey("label") ? object.getString("label") : "").append("\t");
//            result.append(object.getString("set")).append("\t");
//            result.append("\n");
//        }
//
//        return result.toString();
//
//    }
//
//    private JSONArray getOrthologyDiseaseModelVennDiagram(String mpId, Set<String> diseaseClasses, Boolean mgi, Boolean impc, Boolean download, String phenotypeShort) throws IOException, SolrServerException {
//
//        Map<String, Set<String>> sets = new HashMap<>();
//
//        // get gene sets for human orthology (with/without)
//        sets.put(" IMPC " + phenotypeShort + " phenotypes", geneService.getGenesSymbolsBy(mpId).stream().map(geneDTO -> {return geneDTO.getMarkerSymbol();}).collect(Collectors.toSet()));
//        // get gene sets for IMPC and MGI disease models
//
//        Map<String, Set<String>> genesWithDisease = phenodigmService.getGenesWithDisease(diseaseClasses);
//
//        if (mgi && !impc){
//            sets.put("Human curated (orthology) in " + phenotypeShort + " phenotypes", genesWithDisease.get("Human curated (orthology)"));
//            sets.put("MGI " + phenotypeShort + "D predicted", genesWithDisease.get("MGI predicted"));
//        }
//        else if (!mgi && impc) {
//            sets.put("IMPC " + phenotypeShort + "D predicted", genesWithDisease.get("IMPC predicted"));
//            sets.put("Human curated (orthology) in " + phenotypeShort + " phenotypes", genesWithDisease.get("Human curated (orthology)"));
//        } else {
//            sets.putAll(genesWithDisease);
//        }
//        return getJsonForVenn(sets, download);
//    }
//
//    /**
//     *
//     * @param allSets
//     * @param download true if you need data for download - all gene names, false if you need it for display - gene counts only
//     * @return
//     */
//    private JSONArray getJsonForVenn( Map<String, Set<String>> allSets, Boolean download){
//
//        // get counts for intersections
//        JSONArray sets = new JSONArray();
//        JSONArray wholeSets = new JSONArray();
//        List<String> keysIndex = new ArrayList<>(allSets.keySet()); // need this to get the index of each key
//
//        // Add whole sets to the object
//        for (int i = 0; i < keysIndex.size(); i++) {
//            JSONArray currentSets = new JSONArray();
//            currentSets.add(i);
//            sets.add(getSetVennFormat(keysIndex.get(i), currentSets, allSets.get(keysIndex.get(i)).size()));
//            wholeSets.add(getSetJSON(keysIndex.get(i), currentSets, allSets.get(keysIndex.get(i))));
//        }
//
//        // Intersections of 2 sets at a time
//        for (int i = 0; i < keysIndex.size()-1; i++) {
//            for (int j = i + 1; j < keysIndex.size(); j++) {
//                JSONArray currentSets = new JSONArray();
//                currentSets.add(i);
//                currentSets.add(j);
//                Set<String> intersection = new HashSet<>(CollectionUtils.intersection(allSets.get(keysIndex.get(i)), allSets.get(keysIndex.get(j))));
//                sets.add(getSetVennFormat(null, currentSets, intersection.size()));
//                wholeSets.add(getSetJSON(null, currentSets, intersection));
//            }
//        }
//
//        // Intersections of 3 sets at a time
//        for (int i = 0; i < keysIndex.size()-2; i++) {
//            for (int j = i + 1; j < keysIndex.size()-1; j++) {
//                for (int k = j + 1; k < keysIndex.size(); k++) {
//                    JSONArray currentSets = new JSONArray();
//                    currentSets.add(i);
//                    currentSets.add(j);
//                    currentSets.add(k);
//                    Set<String> intersection = new HashSet<>(CollectionUtils.intersection(allSets.get(keysIndex.get(i)),
//                            CollectionUtils.intersection(allSets.get(keysIndex.get(j)), allSets.get(keysIndex.get(k)))));
//                    sets.add(getSetVennFormat(null, currentSets, intersection.size()));
//                    wholeSets.add(getSetJSON(null, currentSets, intersection));
//                }
//            }
//        }
//
////        // Intersections of 4 sets
////        JSONArray currentSets = new JSONArray();
////        currentSets.add(0);
////        currentSets.add(1);
////        currentSets.add(2);
////        currentSets.add(3);
////        int intersectionSize = CollectionUtils.intersection(allSets.get(keysIndex.get(0)),
////                CollectionUtils.intersection(allSets.get(keysIndex.get(1)), CollectionUtils.intersection(allSets.get(keysIndex.get(2)),allSets.get(keysIndex.get(3))))).size();
////        sets.add(getSetVennFormat(null, currentSets, intersectionSize));
//
//        // return in right format for venn diagram http://benfred.github.io/venn.js/examples/styled.html
//        JSONArray result = new JSONArray();
//        if (download) {
//            result.addAll(wholeSets);
//        } else {
//            result.addAll(sets);
//        }
//
//        return result;
//
//        }

//
//
//    /**
//     * @param label
//     * @param sets
//     * @param size
//     * @return JSON objects in the format required by venn diagram library. At the moment used on biological system pages.
//     */
//    private JSONObject getSetVennFormat(String label, JSONArray sets, Integer size) {
//
//        JSONObject set = new JSONObject();
//        set.put("sets", sets);
//        if (label != null) {
//            set.put("label", label);
//        }
//        set.put("size", size);
//
//        return set;
//
//    }

//    private JSONObject getSetJSON(String label, JSONArray sets, Set<String> set) {
//
//        JSONObject obj = new JSONObject();
//        obj.put("sets", sets);
//        if (label != null) {
//            obj.put("label", label);
//        }
//        obj.put("set", set);
//
//        return obj;
//
//    }

    @RequestMapping(value = "/embryo/vignettes", method = RequestMethod.GET)
    public String loadVignettes(Model model, HttpServletRequest request, RedirectAttributes attributes)
            throws OntologyTermNotFoundException, IOException, URISyntaxException, SolrServerException, SQLException {

        return "embryoVignettes";
    }

    
}
//just hold these here until we create the new set of pages from Nat, Terry and Violeta
//{
//    "title": "Embryo Landing Page",
//    "description": "Each IMPC gene knockout strain is assessed for viability by examination of litters produced from mating heterozygous animals. Embryonic lethal and subviable lines are assessed in a dedicated phenotyping pipeline.",
//    "image": "/img/Tmem100_het.jpeg" ,
//    "link": "embryo"
//  },
//  {
//    "title": "Deafness Landing Page",
//    "image": "" ,
//    "description": "We have undertaken a deafness screen in the IMPC cohort of mouse knockout strains. We detected known deafness genes and the vast majority of loci were novel.",
//    "link": "biological-system/deafness"
//  },
//  {
//    "title": "Metabolism Landing Page",
//    "image": "" ,
//    "description": "description....",
//    "link": "biological-system/metabolism"
//  },
//  {
//    "title": "Cardiovascular Landing Page",
//    "image": "render_thumbnail/211474/400/" ,
//    "description": "Cardiovascular system refers to the observable morphological and physiological characteristics of the mammalian heart, blood vessels, or circulatory system that are manifested through development and lifespan",
//    "link": "biological-system/cardiovascular"
//  },
//  {
//    "title": "Vision Landing Page",
//    "image": "" ,
//    "description": "Description...",
//    "link": "biological-system/vision"
//  },
//  {
//    "title": "Nervous System Landing Page",
//    "image": "" ,
//    "description": "Description...",
//    "link": "biological-system/nervous"
//  },
//  {
//    "title": "Neurological and Behavioural Landing Page",
//    "image": "" ,
//    "description": "Description...",
//    "link": "biological-system/neurological"
//  }
