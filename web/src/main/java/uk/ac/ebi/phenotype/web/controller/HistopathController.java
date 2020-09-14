package uk.ac.ebi.phenotype.web.controller;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrDocument;
import org.mousephenotype.cda.solr.service.AnatomyService;
import org.mousephenotype.cda.solr.service.GeneService;
import org.mousephenotype.cda.solr.service.HistopathService;
import org.mousephenotype.cda.solr.service.ImageService;
import org.mousephenotype.cda.solr.service.dto.AnatomyDTO;
import org.mousephenotype.cda.solr.service.dto.GeneDTO;
import org.mousephenotype.cda.solr.service.dto.ImageDTO;
import org.mousephenotype.cda.solr.service.dto.ObservationDTO;
import org.mousephenotype.cda.solr.web.dto.HistopathPageTableRow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

@Controller
public class HistopathController {

	private final Logger log = LoggerFactory.getLogger(HistopathController.class);

	@Autowired
	HistopathService histopathService;

	@Autowired
	GeneService geneService;

	@Autowired
	ImageService imageService;
	
	@Autowired
	AnatomyService anatomyService;

	@RequestMapping("/histopath/{accOrSymbol}")
	public String histopath(@PathVariable String accOrSymbol, Model model) throws SolrServerException, IOException {
		// exmple Lpin2 MGI:1891341
		GeneDTO gene = null;
		if(accOrSymbol.contains(":")){
			gene = geneService.getGeneById(accOrSymbol);
		}else{
			gene = geneService.getGeneByGeneSymbolWithLimitedFields(accOrSymbol);
		}
		model.addAttribute("gene", gene);

		List<ObservationDTO> allObservations = histopathService.getObservationsForHistopathForGene(gene.getMgiAccessionId());
		Map<String, String> sampleIds = getSimpleIds(allObservations);
//		List<ObservationDTO> abnormalObservationsOnly = histopathService
//				.screenOutObservationsThatAreNormal(allObservations);
		//get observations that have the same sampleid, sequence_id and anatomy name
		Map<String, List<ObservationDTO>> uniqueSampleSequeneAndAnatomyName = histopathService
				.getUniqueInfo(allObservations);
		
		List<HistopathPageTableRow> histopathRows = histopathService.getTableData(uniqueSampleSequeneAndAnatomyName, sampleIds);
		Set<String> parameterNames = new TreeSet<>();

		// get image data
		List< SolrDocument> histopathImagesForGene = new ArrayList<>();
		for (ObservationDTO obs : allObservations) {
			if(obs.getDownloadFilePath()!=null){
				SolrDocument image = imageService.getImageByDownloadFilePath(obs.getDownloadFilePath());
				//System.out.println("image="+image);
				histopathImagesForGene.add(image);
			}
			
		}

		// chop the parameter names so we have just the beginning as we have
		// parameter names like "Brain - Description" and "Brain - MPATH
		// Diagnostic Term" we want to lump all into Brain related
		List<HistopathPageTableRow> histopathRowsFiltered=new ArrayList<>();
		Map<String,String> anatomyNameToTermId=new HashMap<>();
		for (HistopathPageTableRow row : histopathRows) {
			filterOutImageRows(histopathRowsFiltered, row);// need to remove
															// image parameters
															// as these are
															// dealt with
															// seperately and
															// otherwise show as
															// "not annotated"
			if (row.getAnatomyName() != null && !row.getAnatomyName().equals("")) {
				String anatomyTerm = row.getAnatomyName();
				if(anatomyNameToTermId.containsKey(anatomyTerm)){
					row.setAnatomyId(anatomyNameToTermId.get(anatomyTerm));
				}
				else {
					AnatomyDTO anatomy = anatomyService.getTermByName(anatomyTerm);
					if (anatomy != null) {
						anatomyNameToTermId.put(anatomyTerm, anatomy.getAnatomyId());
					} else {
						System.err.println("anatomy missing for "+row.getAnatomyName());
						anatomyNameToTermId.put(anatomyTerm, "");
					}
					row.setAnatomyId(anatomyNameToTermId.get(anatomyTerm));
				}
				
				for (SolrDocument image : histopathImagesForGene) {
					if (image != null && image.containsKey(ImageDTO.PARAMETER_ASSOCIATION_VALUE)) {
						//System.out.println(
						//		"looping through image " + image.getFieldValues(ImageDTO.PARAMETER_ASSOCIATION_VALUE));
						for (Object maId : image.getFieldValues(ImageDTO.PARAMETER_ASSOCIATION_NAME)) {
							//System.out.println("row sampleid="+row.getSampleId());
							if (maId.equals(row.getAnatomyName()) && row.getSampleId().equals(sampleIds.get(image.get(ImageDTO.EXTERNAL_SAMPLE_ID)))) {
								//System.out.println("hurrah anatomy found="+maId);
								row.addImage(image);
							}
						}
					}
				}
			}
			parameterNames.addAll(row.getParameterNames());
			
			
		}

		// Collections.sort(histopathRows, new HistopathAnatomyComparator());
		model.addAttribute("histopathRows", histopathRowsFiltered);
		//model.addAttribute("extSampleIdToObservations", abnormalObservationsOnly);
		model.addAttribute("parameterNames", parameterNames);
		model.addAttribute("histopathImagesForGene",histopathImagesForGene);
		return "histopath";
	}

	private void filterOutImageRows(List<HistopathPageTableRow> histopathRowsFiltered, HistopathPageTableRow row) {
		boolean addRow=true;
		if(row.getSignificance().size()==0){
			if(row.getParameterNames().size()==1){
				for(String tempName:row.getParameterNames()){
					if(tempName.equals("Images"))addRow=false;
				}
			}
		}
		if(addRow){
			histopathRowsFiltered.add(row);
		}
	}

	private Map<String, String> getSimpleIds(List<ObservationDTO> allObservations) {
		Map<String, String> sampleIds=new HashMap<>();
		for(ObservationDTO obs:allObservations){
			if(!sampleIds.containsKey(obs.getExternalSampleId())){
				sampleIds.put(obs.getExternalSampleId(), "#"+Integer.toString(sampleIds.keySet().size()+1));
			}
		}
		return sampleIds;
	}

	/**
	 *
	 * @param accOrSymbol - can now be acc but if no : then treated as symbol and look for gene with gene symbol
	 * @param model
	 * @return
	 * @throws SolrServerException
	 * @throws IOException
	 */
	@RequestMapping("/histopathsum/{accOrSymbol}")
	public String histopathSummary(@PathVariable String accOrSymbol, Model model) throws SolrServerException, IOException {
		// exmple Lpin2 MGI:1891341
		GeneDTO gene = null;
		if(accOrSymbol.contains(":")){
			gene = geneService.getGeneById(accOrSymbol);
		}else{
			gene = geneService.getGeneByGeneSymbolWithLimitedFields(accOrSymbol);
		}

		model.addAttribute("gene", gene);

		List<ObservationDTO> allObservations = histopathService.getObservationsForHistopathForGene(gene.getMgiAccessionId());
		List<ObservationDTO> extSampleIdToObservations = histopathService
				.screenOutObservationsThatAreNormal(allObservations);
		Map<String, List<ObservationDTO>> uniqueSampleSequeneAndAnatomyName = histopathService
				.getUniqueInfo(allObservations);
		Map<String, String> sampleIds = getSimpleIds(allObservations);
		List<HistopathPageTableRow> histopathRows = histopathService.getTableData(uniqueSampleSequeneAndAnatomyName, sampleIds);
		// for the summary we add an extra method to count the significant
		// scores and collapse rows based on Anatomy
		List<HistopathPageTableRow> collapsedRows = histopathService.collapseHistopathTableRows(histopathRows);

		Set<String> parameterNames = new TreeSet<>();
		// chop the parameter names so we have just the beginning as we have
		// parameter names like "Brain - Description" and "Brain - MPATH
		// Diagnostic Term" we want to lump all into Brain related
		for (HistopathPageTableRow row : histopathRows) {
			parameterNames.addAll(row.getParameterNames());

		}

		model.addAttribute("histopathRows", collapsedRows);
		model.addAttribute("extSampleIdToObservations", extSampleIdToObservations);
		model.addAttribute("parameterNames", parameterNames);
		return "histopathSummary";
	}

}
