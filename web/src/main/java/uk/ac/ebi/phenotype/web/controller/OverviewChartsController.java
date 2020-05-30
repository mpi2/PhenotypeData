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

import org.apache.solr.client.solrj.SolrServerException;
import org.mousephenotype.cda.constants.OverviewChartsConstants;
import org.mousephenotype.cda.dto.DiscreteTimePoint;
import org.mousephenotype.cda.enumerations.ObservationType;
import org.mousephenotype.cda.solr.service.GenotypePhenotypeService;
import org.mousephenotype.cda.solr.service.ImpressService;
import org.mousephenotype.cda.solr.service.ObservationService;
import org.mousephenotype.cda.solr.service.StatisticalResultService;
import org.mousephenotype.cda.solr.service.dto.ParameterDTO;
import org.mousephenotype.cda.solr.web.dto.CategoricalSet;
import org.mousephenotype.cda.solr.web.dto.StackedBarsData;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import uk.ac.ebi.phenotype.chart.CategoricalChartAndTableProvider;
import uk.ac.ebi.phenotype.chart.ChartData;
import uk.ac.ebi.phenotype.chart.TimeSeriesChartAndTableProvider;
import uk.ac.ebi.phenotype.chart.UnidimensionalChartAndTableProvider;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


@Controller
public class OverviewChartsController {


    private final GenotypePhenotypeService genotypePhenotypeService;
    private final CategoricalChartAndTableProvider cctp;
    private final TimeSeriesChartAndTableProvider tstp;
    private final UnidimensionalChartAndTableProvider uctp;
    private final ImpressService impressService;
    private final ObservationService observationService;
    private final StatisticalResultService statisticalResultService;

    @Inject
    public OverviewChartsController(
            @NotNull @Named("genotype-phenotype-service") GenotypePhenotypeService genotypePhenotypeService,
            @NotNull CategoricalChartAndTableProvider cctp,
            @NotNull TimeSeriesChartAndTableProvider tstp,
            @NotNull UnidimensionalChartAndTableProvider uctp,
            @NotNull ImpressService impressService,
            @NotNull ObservationService observationService,
            @NotNull StatisticalResultService statisticalResultService) {
        this.genotypePhenotypeService = genotypePhenotypeService;
        this.cctp = cctp;
        this.tstp = tstp;
        this.uctp = uctp;
        this.impressService = impressService;
        this.observationService = observationService;
        this.statisticalResultService = statisticalResultService;
    }


    @RequestMapping(value = "/chordDiagram", method = RequestMethod.GET)
    public String getGraph(
            @RequestParam(required = false, value = "phenotype_name") List<String> phenotypeName,
            Model model) {

        model.addAttribute("phenotypeName", (phenotypeName != null) ? new JSONArray(phenotypeName.stream().distinct().collect(Collectors.toList())) : null);
        return "chordDiagram";

    }


    @ResponseBody
    @CrossOrigin(origins = "*", maxAge = 3600)
    @RequestMapping(value = "/chordDiagram.json", method = RequestMethod.GET)
    public String getMatrix(
            @RequestParam(required = false, value = "phenotype_name") List<String> phenotypeName,
            @RequestParam(required = false, value = "idg") Boolean idg,
            @RequestParam(required = false, value = "idgClass") String idgClass) {

        return genotypePhenotypeService.getPleiotropyMatrix(phenotypeName, idg, idgClass).toString();

    }


    @ResponseBody
    @RequestMapping(value = "/chordDiagram.csv", method = RequestMethod.GET)
    public String getChordDiagramDownload(
            @RequestParam(required = false, value = "phenotype_name") List<String> phenotypeName,
            @RequestParam(required = false, value = "idg") Boolean idg,
            @RequestParam(required = false, value = "idgClass") String idgClass
    ) {

        try {
            return genotypePhenotypeService.getPleiotropyDownload(phenotypeName, idg, idgClass);
        } catch (IOException | SolrServerException e) {
            e.printStackTrace();
        }
        return "";
    }

    @RequestMapping(value="/overviewCharts/{phenotype_id}", method=RequestMethod.GET)
    public String getGraph(
            @PathVariable String phenotype_id,
            @RequestParam(required = true, value = "parameter_id") String parameterId,
            @RequestParam(required = false, value = "center") String center,
            @RequestParam(required = false, value = "sex") String sex,
            @RequestParam(required = false, value = "all_centers") String allCenters,
            Model model,
            HttpServletRequest request,
            RedirectAttributes attributes) throws SolrServerException, IOException , URISyntaxException, SQLException {

        String[] centerArray = (center != null) ? center.split(",") : null;
        String[] sexArray = (sex != null) ? sex.split(",") : null;
        String[] allCentersArray = (allCenters != null) ? allCenters.split(",") : null;
        String[] centers = (centerArray != null) ? centerArray : allCentersArray;

        model.addAttribute("chart", getDataOverviewChart(phenotype_id, model, parameterId, centers, sexArray));

        return "overviewChart";

    }


    public ChartData getDataOverviewChart(String mpId, Model model, String parameterStableId, String[] center, String[] sex)
            throws SolrServerException, IOException , URISyntaxException, SQLException{

        ParameterDTO parameter = impressService.getParameterByStableId(parameterStableId);
        ChartData chartRes = null;
        List<String> genes = null;
        String[] centerToFilter = center;

        // Assuming that different versions of a procedure will keep the same name.
        String procedureName = parameter.getProcedureNames().get(0);

        if (parameter != null && procedureName != null){

            genes = genotypePhenotypeService.getGenesAssocByParamAndMp(parameter.getStableId(), mpId);

            if (centerToFilter == null) { // first time we load the page.
                // We need to know centers for the controls, otherwise we show all controls
                Set<String> tempCenters = observationService.getCenters(parameter.getStableId(), genes, OverviewChartsConstants.B6N_STRAINS, "experimental");
                centerToFilter = tempCenters.toArray(new String[0]);
            }

            if(parameter.getDataType().equals(ObservationType.categorical.name()) ){
                CategoricalSet controlSet = observationService.getCategories(parameter.getStableId(), null , "control", OverviewChartsConstants.B6N_STRAINS, centerToFilter, sex);
                controlSet.setName("Control");
                CategoricalSet mutantSet = observationService.getCategories(parameter.getStableId(), null, "experimental", OverviewChartsConstants.B6N_STRAINS, centerToFilter, sex);
                mutantSet.setName("Mutant");
                List<ChartData> chart = cctp.doCategoricalDataOverview(controlSet, mutantSet);
                if (chart.size() > 0){
                    chartRes = chart.get(0);
                }
            }

            else if ( parameter.getDataType().equals(ObservationType.time_series.toString()) ){
                Map<String, List<DiscreteTimePoint>> data = new HashMap<>();
                data.put("Control", observationService.getTimeSeriesControlData(parameter.getStableId(), OverviewChartsConstants.B6N_STRAINS, centerToFilter, sex));
                data.putAll(observationService.getTimeSeriesMutantData(parameter.getStableId(), genes, OverviewChartsConstants.B6N_STRAINS, centerToFilter, sex));
                ChartData chart = tstp.doTimeSeriesOverviewData(data, parameter);
                chart.setId(parameter.getName());
                chartRes = chart;
            }

            else if ( parameter.getDataType().equals(ObservationType.unidimensional.name()) ){
                StackedBarsData data = statisticalResultService.getUnidimensionalData(parameterStableId, genes, OverviewChartsConstants.B6N_STRAINS, "experimental", centerToFilter, sex);
                chartRes = uctp.getStackedHistogram(data, parameter.getName(), parameterStableId, procedureName);
            }

            if (chartRes != null && center == null && sex == null){ // we don't do a filtering
                // we want to offer all filter values, not to eliminate males if we filtered on males
                // plus we don't want to do another SolR call each time to get the same data
                Set<String> centerFitlers =	observationService.getCenters(parameter.getStableId(), genes, OverviewChartsConstants.B6N_STRAINS, "experimental");
                model.addAttribute("centerFilters", centerFitlers);
            }
        }

        return chartRes;
    }

}