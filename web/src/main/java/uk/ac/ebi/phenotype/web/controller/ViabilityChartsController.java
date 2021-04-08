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

import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.mousephenotype.cda.common.Constants;
import org.mousephenotype.cda.dto.LifeStage;
import org.mousephenotype.cda.enumerations.EmbryoViability;
import org.mousephenotype.cda.enumerations.ObservationType;
import org.mousephenotype.cda.enumerations.SexType;
import org.mousephenotype.cda.enumerations.ZygosityType;
import org.mousephenotype.cda.solr.service.*;
import org.mousephenotype.cda.solr.service.dto.*;
import org.mousephenotype.cda.solr.service.exception.SpecificExperimentException;
import org.mousephenotype.cda.solr.web.dto.EmbryoViability_DTO;
import org.mousephenotype.cda.solr.web.dto.ViabilityDTO;
import org.mousephenotype.cda.utilities.LifeStageMapper;
import org.mousephenotype.cda.web.ChartType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import uk.ac.ebi.phenotype.chart.*;
import uk.ac.ebi.phenotype.error.GenomicFeatureNotFoundException;
import uk.ac.ebi.phenotype.error.ParameterNotFoundException;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.springframework.web.bind.annotation.ValueConstants.DEFAULT_NONE;

/**
 * Just handle viability charts for a given gene only - so simple show all viability for this gene
 */
@Controller
public class ViabilityChartsController {

	private final Logger                              log = LoggerFactory.getLogger(this.getClass());
	private final ViabilityChartAndDataProvider       viabilityChartAndDataProvider;
	private final ExperimentService                   experimentService;
	private final StatisticalResultService            srService;
	private final GeneService                         geneService;
	private final ImageService                        imageService;
	private final ImpressService                      impressService;
	private final GenotypePhenotypeService            gpService;

    @Resource(name = "globalConfiguration")
    private Map<String, String> config;


    @Value("${solr_url}")
    public String SOLR_URL;


    @Inject
    public ViabilityChartsController(
			@NotNull ViabilityChartAndDataProvider viabilityChartAndDataProvider,
			@NotNull ExperimentService experimentService,
			@NotNull GeneService geneService,
			@NotNull ImpressService impressService,
			@NotNull ImageService imageService,
			@NotNull @Named("statistical-result-service") StatisticalResultService srService,
			@NotNull @Named("genotype-phenotype-service") GenotypePhenotypeService gpService
			) {
        this.viabilityChartAndDataProvider = viabilityChartAndDataProvider;
        this.experimentService = experimentService;
        this.srService = srService;
        this.geneService = geneService;
        this.impressService = impressService;
        this.imageService=imageService;
        this.gpService = gpService;
	}


    /**
     * This method should take in the parameters and then generate a skeleton
     * jsp page with urls that can be called by a jquery ajax requests for each
     * graph div and table div
     *
     * @param accessionsParams
     * @param model
     * @return
     * @throws GenomicFeatureNotFoundException
     * @throws ParameterNotFoundException
     * @throws IOException
     * @throws URISyntaxException
     * @throws SolrServerException, IOException
     */

    @RequestMapping("/viabilitycharts")
    public String charts(@RequestParam(required = true, value = "accession") String[] accessionsParams,
                         @RequestParam(required = false, value = "pageTitle") String pageTitle,
                         @RequestParam(required = false, value = "pageLinkBack") String pageLinkBack,
                         HttpServletRequest request, HttpServletResponse response,
                         Model model) {

            response.addHeader("Access-Control-Allow-Origin", "*");//allow javascript requests from other domain - note spring way of doing this does not work!!!! as usual!!!

            model.addAttribute("pageTitle", pageTitle);
            //get viability top table
		//methods here to get phenotype calls and stats results
		//http://wwwdev.ebi.ac.uk/mi/impc/dev/solr/genotype-phenotype/select?q=*:*&fq=parameter_stable_id:IMPC_VIA_*
		//http://wwwdev.ebi.ac.uk/mi/impc/dev/solr/genotype-phenotype/select?q=marker_accession_id:%22MGI:2136171%22&fq=parameter_stable_id:IMPC_VIA_*
		List<GenotypePhenotypeDTO> genotypePhenotypeForViability=null;
		try {
			genotypePhenotypeForViability = gpService.getGenotypePhenotypeForViability(accessionsParams[0]);
			for(GenotypePhenotypeDTO phenotypeDTO:genotypePhenotypeForViability){
				System.out.println(" Associated phenotype="+phenotypeDTO.getMpTermName()+" phenotyping center "+phenotypeDTO.getPhenotypingCenter()+"testing protocol="+phenotypeDTO.getProcedureName()+" measured value="+ phenotypeDTO.getParameterName());
				System.out.println(phenotypeDTO);
			}
		} catch (SolrServerException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}


		//create charts
		//method here to look through all viability and embryo viability etc and get one chart per set i.e. don't seperate on Zygosity like other charts are seperated
		//what are the rules going to be for this??
		//get all the procedures parameters for which we have data first in the experiment core?

//		if (procedureStableId.equals("IMPC_VIA_001")) {
//			if (parameterStableId.startsWith("IMPC_VIA_")) {
//
//				// IMPC VIA 001
//
//				// Its a viability outcome param which means its a line level query
//				// so we don't use the normal experiment query in experiment service
//				ViabilityDTO viability = experimentService.getSpecificViabilityVersion1ExperimentDTO(parameterStableId, pipelineStableId, accession[0], phenotypingCenter, strain, metaDataGroupString, alleleAccession);
//				ViabilityDTO viabilityDTO = viabilityChartAndDataProvider.doViabilityData(viability, parameterStableId);
//				model.addAttribute("viabilityDTO", viabilityDTO);
//				//if viability data we want to have a message at the top which comes up on stats.jsp
//				model.addAttribute("isViability", true);
//
//			}
//		}

		return "viabilityChartsForGene";
    }

}
