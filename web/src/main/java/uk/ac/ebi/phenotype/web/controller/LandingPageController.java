package uk.ac.ebi.phenotype.web.controller;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.Group;
import org.mousephenotype.cda.solr.service.*;
import org.mousephenotype.cda.solr.service.dto.CountTableRow;
import org.mousephenotype.cda.solr.service.dto.ImpressDTO;
import org.mousephenotype.cda.solr.service.dto.MpDTO;
import org.mousephenotype.cda.solr.service.embryoviewer.EmbryoViewerService;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import uk.ac.ebi.phenotype.chart.AnalyticsChartProvider;
import uk.ac.ebi.phenotype.chart.CmgColumnChart;
import uk.ac.ebi.phenotype.chart.ScatterChartAndTableProvider;
import uk.ac.ebi.phenotype.error.OntologyTermNotFoundException;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * LandingPage controller is the controller for all landing pages in the
 * portal.  The
 */

@Controller
public class LandingPageController {

	private final GeneService              geneService;
	private final GenotypePhenotypeService genotypePhenotypeService;
	private final ImageService             imageService;
	private final ImpressService           impressService;
	private final MpService                mpService;
    private final ObservationService       observationService;
    private final StatisticalResultService statisticalResultService;
    private final HistopathService         histopathService;
	private final EmbryoViewerService      embryoViewerService;

    @Inject
    public LandingPageController(
			@NotNull GeneService geneService,
			@NotNull ImageService imageService,
			@NotNull ImpressService impressService,
			@NotNull MpService mpService,
    		@NotNull ObservationService observationService,
			@NotNull @Named("genotype-phenotype-service") GenotypePhenotypeService genotypePhenotypeService,
			@NotNull @Named("statistical-result-service") StatisticalResultService statisticalResultService,
			@NotNull HistopathService histopathService,
			@NotNull EmbryoViewerService embryoViewerService)
	{
        this.geneService = geneService;
		this.genotypePhenotypeService = genotypePhenotypeService;
        this.imageService = imageService;
        this.impressService = impressService;
		this.mpService = mpService;
		this.observationService = observationService;
		this.statisticalResultService = statisticalResultService;
		this.histopathService=histopathService;
		this.embryoViewerService=embryoViewerService;
    }


	/**
	 *
	 * @param model
	 * @return
	 * @throws SolrServerException
	 * @throws IOException
	 */
	@RequestMapping("/histopath_old")
	public String histopathOld(Model model) throws SolrServerException, IOException {
		//To display the heatmap we need data in form of ints [ column , row, value ] but row starts from bottom left hand side
		HeatmapData heatmapData = histopathService.getHeatmapData();
		String [] headers=new String []{"blah", "bug"};
		JSONArray anatomyArray = null;
		anatomyArray= new JSONArray(heatmapData.getColumnHeaders());
		JSONArray geneSymbolsArray=new JSONArray(heatmapData.getGeneSymbols());
		model.addAttribute("anatomyHeaders", anatomyArray);
		model.addAttribute("geneSymbols", geneSymbolsArray);
		model.addAttribute("data", heatmapData.getData());
		return "histopathLandingPage_old";
	}

	/**
	 *
	 * @param model
	 * @return
	 * @throws SolrServerException
	 * @throws IOException
	 */
	@RequestMapping("landing_pages/histopath")
	public String histopath(Model model) throws SolrServerException, IOException {
		//To display the heatmap we need data in form of ints [ column , row, value ] but row starts from bottom left hand side
		HeatmapData heatmapData = histopathService.getHeatmapDatadt();
		//String [] headers=new String []{"blah", "bug"};
		//JSONArray anatomyArray = null;
		//anatomyArray= new JSONArray(heatmapData.getParameterNames());
		//JSONArray geneSymbolsArray=new JSONArray(heatmapData.getGeneSymbols());
		model.addAttribute("anatomyHeaders", heatmapData.getColumnHeaders());
		model.addAttribute("geneSymbols", heatmapData.getGeneSymbols());
		model.addAttribute("rows", heatmapData.getRows());
		return "histopathLandingPage";
	}

    @RequestMapping(value = "/embryo", method = RequestMethod.GET)
    public String loadEmbryoPage(Model model, HttpServletRequest request, RedirectAttributes attributes)
            throws OntologyTermNotFoundException, IOException, URISyntaxException, SolrServerException, SQLException {

        AnalyticsChartProvider chartsProvider = new AnalyticsChartProvider();
        List<String> resources = Arrays.asList( "IMPC" );
        Map<String, Set<String>> viabilityRes = observationService.getViabilityCategories(resources, true);

        Map<String, Long> viabilityMap = observationService.getViabilityCategories(viabilityRes);
        List<CountTableRow> viabilityTable = observationService.consolidateZygosities(viabilityRes);

        model.addAttribute("viabilityChart", chartsProvider.getSlicedPieChart(new HashMap<String, Long>(), viabilityMap, "", "viabilityChart"));
        model.addAttribute("viabilityTable", viabilityTable);

        return "embryo";
    }


	/**
	 * /embryo_heatmap has been renamed embryo_imaging
	 */
	@RequestMapping("/embryo_heatmap")
	public String rootForward() {
		return "redirect:/embryo_imaging";
	}

	@RequestMapping("/embryo_imaging")
	public String embryoHeatmap(Model model) throws SolrServerException, IOException {
		//To display the heatmap we need data in form of ints [ column , row, value ] but row starts from bottom left hand side
		System.out.println("hitting embryo heatmap endpoint");
		//HistopathHeatmapData heatmapData = histopathService.getHeatmapDatadt();
		HeatmapData heatmapData = embryoViewerService.getEmbryoHeatmap();
		assert(heatmapData.getColumnHeaders().size()>0);
		//String [] headers=new String []{"blah", "bug"};
		//JSONArray anatomyArray = null;
		//anatomyArray= new JSONArray(heatmapData.getParameterNames());
		//JSONArray geneSymbolsArray=new JSONArray(heatmapData.getGeneSymbols());
		model.addAttribute("modalityHeaders", heatmapData.getColumnHeaders());
		model.addAttribute("geneSymbols", heatmapData.getGeneSymbols());
		model.addAttribute("mgiAccessions", heatmapData.getMgiAccessions());
		model.addAttribute("rows", heatmapData.getRows());
		return "embryo_imaging";
	}

    @RequestMapping(value = "/biological-system/pain", method = RequestMethod.GET)
    public String loadPainBiologicalSystemPage(Model model, HttpServletRequest request, RedirectAttributes attributes) {
	    	System.out.println("pain page called");
	    	return "landing_pain";
    }
    
    @RequestMapping(value = "/biological-system/conservation", method = RequestMethod.GET)
    public String loadConservaionBiologicalSystemPage(Model model, HttpServletRequest request, RedirectAttributes attributes) {
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
        List<String>          anatomyIds = new ArrayList<>(); // corresponding anatomical system, used for images
        MpDTO                 mpDTO      = null;
        ArrayList<JSONObject> cmg_genes  = null;

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
	        procedures.addAll(impressService.getProceduresByMpTerm(mpDTO.getMpId(), true));

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
        		model.addAttribute("phenotypeChart", ScatterChartAndTableProvider.getScatterChart("phenotypeChart", genotypePhenotypeService.getTopLevelPhenotypeIntersection(mpDTO.getMpId(), filterOnMarkerAccession), "Gene pleiotropy",
																								  description, "Number of phenotype associations to " + pageTitle, "Number of associations to other phenotypes",
																								  "Other phenotype calls: ", pageTitle + " phenotype calls: ", "Gene"));
	        model.addAttribute("genePercentage", ControllerUtils.getPercentages(mpDTO.getMpId(), statisticalResultService, genotypePhenotypeService));
	        model.addAttribute("phenotypes", genotypePhenotypeService.getAssociationsCount(mpDTO.getMpId(), resources));
	        model.addAttribute("mpId", mpDTO.getMpId());
	        model.addAttribute("mpDTO", mpDTO);
	
	        String systemName = mpDTO.getMpTerm();
	        if (mpDTO.getMpTerm().contains("hearing/vestibular/ear")) {
	            systemName = "hearing";
	        }
	        String systemNameDisplay = systemName.substring(0, 1).toUpperCase() + systemName.substring(1);
	        model.addAttribute("systemName", systemNameDisplay.replace(" phenotype", ""));
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
    
    
    
    private ArrayList<JSONObject> GetLatestProjectStatus(ArrayList<JSONObject> cmg_genes_bestphenodigm) throws SolrServerException, IOException, JSONException {
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
    
    private ArrayList<JSONObject> GetBestPhenodigm (String phenotypeOverlapScoreFile, ArrayList<JSONObject> cmg_genes_information) throws IOException, JSONException {
    		// reads from /src/main/resources/20171206-CMG-best-phenodigm.json and compose the page
    		BufferedReader in = new BufferedReader(new FileReader(new ClassPathResource(phenotypeOverlapScoreFile).getFile()));
    		if (in != null) {
    			String    json = in.lines().collect(Collectors.joining(" "));
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
    
    private ArrayList<JSONObject> GetCmgGenes (String file_path_genes) throws IOException, JSONException {
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
