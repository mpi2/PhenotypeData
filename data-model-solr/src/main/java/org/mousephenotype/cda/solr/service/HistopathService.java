package org.mousephenotype.cda.solr.service;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.PivotField;
import org.apache.solr.common.util.NamedList;
import org.mousephenotype.cda.enumerations.SexType;
import org.mousephenotype.cda.enumerations.ZygosityType;
import org.mousephenotype.cda.solr.service.dto.ImpressBaseDTO;
import org.mousephenotype.cda.solr.service.dto.ObservationDTO;
import org.mousephenotype.cda.solr.service.dto.ObservationDTOBase;
import org.mousephenotype.cda.solr.service.dto.StatisticalResultDTO;
import org.mousephenotype.cda.solr.web.dto.HistopathPageTableRow;
import org.mousephenotype.cda.solr.web.dto.HistopathPageTableRow.ParameterValueBean;
import org.mousephenotype.cda.solr.web.dto.HistopathSumPageTableRow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toSet;

@Service
public class HistopathService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private ObservationService observationService;
    private StatisticalResultService statisticalResultService;
    private String delimeter = " - ";
    public static String histoDelimeter = "||";


    @Inject
    public HistopathService(ObservationService observationService, StatisticalResultService statisticalResultService) {
        this.observationService = observationService;
        this.statisticalResultService = statisticalResultService;
    }

    public HistopathService() {

    }

    public List<HistopathPageTableRow> getTableData(Map<String, List<ObservationDTO>> uniqueSampleSequeneAndAnatomyName, Map<String, String> sampleIds) {
        List<HistopathPageTableRow> rows = new ArrayList<>();

        for (String key : uniqueSampleSequeneAndAnatomyName.keySet()) {

            // just for images here as no anatomy currently

            List<ObservationDTO> observations = uniqueSampleSequeneAndAnatomyName.get(key);
            Set<String> parameterNames = new TreeSet<>();

            HistopathPageTableRow row = new HistopathPageTableRow();// a row is a unique sampleId and anatomy and sequence id combination
            row.setAnatomyName(this.getAnatomyStringFromObservation(observations.get(0)));//anatomy should be the same from any in this dataset
            row.setSampleId(sampleIds.get(observations.get(0).getExternalSampleId()));
            row.setSex(SexType.valueOf(observations.get(0).getSex()));

            for (ObservationDTO obs : observations) {

                String zyg = ZygosityType.valueOf(obs.getZygosity()).getShortName();
                row.setZygosity(zyg);
                if (obs.getAgeInWeeks() != null) {
                    row.setAgeInWeeks(obs.getAgeInWeeks());
                }

                //System.out.println("sequenceId in observation="+obs.getSequenceId());
                row.setSequenceId(obs.getSequenceId());


                ImpressBaseDTO parameter = new ImpressBaseDTO(null, null, obs.getParameterStableId(),
                        obs.getParameterName());

                parameterNames.add(obs.getParameterName());

                if (obs.getObservationType().equalsIgnoreCase("categorical")) {
                    row.addCategoricalParam(parameter, obs.getCategory());
                    if (parameter.getName().contains("Significance")) {
                        row.addSignficance(parameter, obs.getCategory());
                    }
                    if (parameter.getName().contains("Severity")) {
                        row.addSeverity(parameter, obs.getCategory());
                    }
                }
                if (obs.getObservationType().equalsIgnoreCase("ontological")) {
                    if (obs.getSubTermName() != null) {
                        for (int i = 0; i < obs.getSubTermId().size(); i++) {
                            //System.out.println("subtermId=" + obs.getSubTermId() + "subtermname="
                            //+ obs.getSubTermName().get(i));
                            String termName = obs.getSubTermName().get(i);
                            String termId = obs.getSubTermId().get(i);
                            String termDesc = i < obs.getSubTermDescription().size() ? obs.getSubTermDescription().get(i) : "";
                            OntologyBean subOntologyBean = new OntologyBean(termId, termName, termDesc);

                            row.addOntologicalParam(parameter, subOntologyBean);
                            if (parameter.getName().contains("MPATH process term")) {
                                row.addMpathProcessParam(parameter, subOntologyBean);
                            }
                            if (parameter.getName().contains("MPATH diagnostic term")) {
                                row.addMpathDiagnosticParam(parameter, subOntologyBean);
                            }
                            if (parameter.getName().contains("PATO")) {
                                row.addPatoParam(parameter, subOntologyBean);
                            }
                        }
                    } else {
                        logger.info("subterms are null for ontological data=" + obs);
                    }
                }
                if (obs.getObservationType().equalsIgnoreCase("text")) {
                    row.addTextParam(parameter, obs.getTextValue());
                    if (obs.getParameterName().contains("Free text")) {
                        row.addFreeTextParam(parameter, obs.getTextValue());
                    }
                    if (obs.getParameterName().contains("Description")) {
                        //System.out.println("setting description="+obs.getTextValue());
                        row.addDescriptionTextParam(parameter, obs.getTextValue());
                    }
                }

            }

            rows.add(row);
            row.setParameterNames(parameterNames);
        }
        System.out.println(rows);
        return rows;

    }

    private String getAnatomyStringFromObservation(ObservationDTO obs) {
        String anatomyString = null;
        String paramName = obs.getParameterName();
        if (paramName.contains(delimeter)) {
            anatomyString = paramName.substring(0, paramName.indexOf(delimeter));
        }

        return anatomyString;
    }

    public List<ObservationDTO> getObservationsForHistopathForGene(String acc) throws SolrServerException, IOException {
        return observationService.getObservationsByProcedureNameAndGene("Histopathology", acc);
    }

    public HeatmapData getHeatmapData() throws SolrServerException, IOException {
        NamedList<List<PivotField>> pivots = observationService.getHistopathGeneParameterNameCategoryPivots();
        HashSet<String> anatomyParamName = new HashSet<>();//in the histopath case this is anatomy/parameter names
        //then lest order this set as a list alphabetically into a list to then dictate column numbers
        HashSet<String> geneSymbols = new HashSet<>();// for histopath it's gene symbols
        Map<String, Map<String, String>> geneToParameterToCategory = new HashMap<>();
        HashSet<String> uniqueCategories = new HashSet<>();

        Map<String, Set<String>> map = new HashMap<>();
        for (Map.Entry<String, List<PivotField>> pivotFacet : pivots) {
            for (PivotField phenotypePivotFacet : pivotFacet.getValue()) {
                String geneSYMBOL = phenotypePivotFacet.getValue().toString();
                if (!geneToParameterToCategory.containsKey(geneSYMBOL)) {
                    geneToParameterToCategory.put(geneSYMBOL, new HashMap<>());
                }
                geneSymbols.add(geneSYMBOL);
                map.putIfAbsent(geneSYMBOL, new HashSet<>());
                for (PivotField genePivotFacet : phenotypePivotFacet.getPivot()) {
                    String[] withAnatomyAndSignificance = genePivotFacet.getValue().toString().split(" - ");
                    String parameterName = withAnatomyAndSignificance[0].trim();
                    anatomyParamName.add(parameterName);//get a unique set

                    map.get(geneSYMBOL).add(parameterName);
                    if (genePivotFacet.getPivot() != null) {
                        for (PivotField pivotCategory : genePivotFacet.getPivot()) {
                            //System.out.println("Category-" + pivotCategory.getValue());
                            String newValue = pivotCategory.getValue().toString();
                            if (geneToParameterToCategory.get(geneSYMBOL).containsKey(parameterName)) {
                                String oldValue = geneToParameterToCategory.get(geneSYMBOL).get(parameterName);
                                //System.err.println("error we already have this combination lets get the most significant one of the two??"+geneSYMBOL+" "+ parameterName +" "+geneToParameterToCategory.get(geneSYMBOL).get(parameterName)+" vs "+pivotCategory.getValue().toString());
                                if (this.getIntValueForString(oldValue) < this.getIntValueForString(newValue)) {
                                    geneToParameterToCategory.get(geneSYMBOL).put(parameterName, pivotCategory.getValue().toString());
                                }
                            } else {
                                geneToParameterToCategory.get(geneSYMBOL).put(parameterName, pivotCategory.getValue().toString());
                            }
                            uniqueCategories.add((String) pivotCategory.getValue());
                        }
                    }
                    //System.out.println("geneSYMBOL="+geneSYMBOL+"parameterName="+parameterName);
                }
            }
        }

        List<String> anatomyList = new ArrayList<>(anatomyParamName);
        Collections.sort(anatomyList);
        List<String> geneList = new ArrayList<>(geneSymbols);
        geneList.sort(Collections.reverseOrder());//so we get alphabetical order starting at top of heatmap
        System.out.println("uniqueCategories=" + uniqueCategories);
        //generate the data array here from the data structures we have just created as we need to know all column headers before we do this
        JSONArray allCells = new JSONArray();
        int row = 0;
        for (String geneSymbol : geneList) {
            int column = 0;
            for (String parameterName : anatomyList) {
                String value;
                //we need an empty cell value even if nothing in original data
                value = geneToParameterToCategory.get(geneSymbol).getOrDefault(parameterName, "No value found");
                int significance = this.getIntValueForString(value);
                geneToParameterToCategory.get(geneSymbol).get(parameterName);
                JSONArray cell = new JSONArray();
                cell.put(column);
                cell.put(row);
                cell.put(significance);
                allCells.put(cell);
                column++;
            }
            row++;
        }


        return new HeatmapData(anatomyList, geneList, allCells);
    }

    public HeatmapData getHeatmapDatadt() throws SolrServerException, IOException {
        ArrayList<String> geneList = new ArrayList<>();
        ArrayList<String> anatomyList = new ArrayList<>();

        List<StatisticalResultDTO> histopathStatsResults = statisticalResultService.getStatisticalResultByDataType("histopathology");
        List<ObservationDTO> histopathNAObservations = observationService.getNotApplicableHistopathObs();

        geneList = (ArrayList<String>) histopathStatsResults.stream().map(StatisticalResultDTO::getMarkerSymbol).filter(Objects::nonNull).distinct().collect(Collectors.toList());
        anatomyList = (ArrayList<String>) histopathStatsResults.stream().map(s -> s.getParameterName().split(" - ")[0]).distinct().collect(Collectors.toList());
        Collections.sort(geneList);
        Collections.sort(anatomyList);
        Map<String, Map<String, Set<Boolean>>> geneAnatomyValueMap = histopathStatsResults
                .stream()
                .filter(s -> s.getMarkerSymbol() != null && s.getParameterName() != null)
                .collect(
                        groupingBy(
                                StatisticalResultDTO::getMarkerSymbol,
                                groupingBy(s -> s.getParameterName().split(" - ")[0], Collectors.mapping(StatisticalResultDTO::getSignificant, toSet())
                                )
                        )
                );
        Map<String, Set<String>> geneNotApplicableAnatomyMap = histopathNAObservations
                .stream()
                .filter(s -> s.getGeneSymbol() != null && s.getParameterName() != null)
                .collect(
                        groupingBy(
                                ObservationDTOBase::getGeneSymbol,
                                Collectors.mapping(o -> o.getParameterName().split(" - ")[0], toSet())
                        )
                );
        List<List<Integer>> rows = new ArrayList<>();

        for (String gene : geneList) {
            List<Integer> row = new ArrayList<>();
            Map<String, Set<Boolean>> geneValuesByAnatomy = geneAnatomyValueMap.get(gene);
            for (String anatomy : anatomyList) {
                int significance = 0;
                if (geneValuesByAnatomy.containsKey(anatomy)) {
                    boolean significant = geneValuesByAnatomy.get(anatomy).stream().anyMatch(s -> s);
                    significance = significant ? 4 : 2;
                } else if (geneNotApplicableAnatomyMap.containsKey(gene) && geneNotApplicableAnatomyMap.get(gene).contains(anatomy)) {
                    significance = 1;
                }
                row.add(significance);
            }
            rows.add(row);
        }

        return new HeatmapData(anatomyList, geneList, rows);
    }

    private int getIntValueForString(String value) {
        //[Significant, Not applicable, Not significant, not significant, Significant,
        // significant ]
        int significance = 0;
        //value is now returned as a String number from new spark infrastructure so now don't need to convert as below

        switch (value) {
            case "0":
                significance = 0;
                break;
            case "1":
                significance = 1;
                break;
            case "2":
                significance = 2;
                break;
            case "3":
                significance = 3;
                break;
            case "4":
                significance = 4;
                break;


            case "No value found":
                significance = 0;
                break;
            case "":
                significance = 0;
                break;
            case "Not applicable":
                significance = 1;
                break;
            case "Not significant":
                significance = 2;
                break;
            case "not significant":
                significance = 2;
                break;
            case "significant":
                significance = 4;
                break;
        }
        System.out.println("value=" + value + " sig returned=" + significance);
        return significance;
    }

    public List<ObservationDTO> screenOutObservationsThatAreNormal(List<ObservationDTO> observations) {

        List<ObservationDTO> filteredObservations = new ArrayList<>();
        for (ObservationDTO obs : observations) {

            boolean addObservation = true;
            if (obs.getObservationType().equalsIgnoreCase("categorical")) {
                if (obs.getCategory().equalsIgnoreCase("0")) {
                    addObservation = false;
                }

            }
            if (obs.getObservationType().equalsIgnoreCase("ontological")) {
                if (obs.getSubTermName() != null) {
                    for (String name : obs.getSubTermName()) {
                        if (name.equalsIgnoreCase("normal")) {
                            addObservation = false;
                            break;
                        }
                    }
                }
            }

            if (addObservation) {
                filteredObservations.add(obs);
            }

        }
        return filteredObservations;
    }

    public List<HistopathPageTableRow> collapseHistopathTableRows(List<HistopathPageTableRow> histopathRows) {
        List<HistopathPageTableRow> collapsedRows = new ArrayList<>();
        Map<String, HistopathSumPageTableRow> anatomyToRowMap = new HashMap<>();
        for (HistopathPageTableRow row : histopathRows) {
            String anatomy = row.getAnatomyName();
            if (!anatomyToRowMap.containsKey(anatomy)) {
                anatomyToRowMap.put(anatomy, new HistopathSumPageTableRow());
            }
            HistopathSumPageTableRow anatomyRow = anatomyToRowMap.get(anatomy);
            anatomyRow.setAnatomyName(anatomy);
            //anatomyRow.getSignificance().addAll(row.getSignificance());
            //anatomyRow.getSeverity().addAll(row.getSeverity());
            boolean significant = false;
            for (ParameterValueBean sign : row.getSignificance()) {
                String text = sign.getTextValue();
                //System.out.println("text="+text+"|");
                if (text.equals("Significant")) {
                    //System.out.println("significant!!!!!!!!!!!!");
                    anatomyRow.setSignificantCount(anatomyRow.getSignificantCount() + 1);
                    significant = true;
                    //if significant then set the text and parameters of the row that is significant to the row we are collapsing so we display the most appropriate info
                    //anatomyRow.setDescriptionTextParameters(anatomyRow.getDescriptionTextParameters().addAll(row.get));
                    //anatomyRow.setFreeTextParameters(row.get);

                } else {//assume non significant if not significant
                    anatomyRow.setNonSignificantCount(anatomyRow.getNonSignificantCount() + 1);
                }
            }
            if (significant) {
                //if significant lets copy the main attributes so that we have a summary for that significant hit.
                anatomyRow.setSampleId(row.getSampleId());
                anatomyRow.setMpathProcessOntologyBeans(row.getMpathProcessOntologyBeans());
                anatomyRow.setMpathDiagnosticOntologyBeans(row.getMpathDiagnosticOntologyBeans());
                anatomyRow.setDescriptionTextParameters(row.getDescriptionTextParameters());
                anatomyRow.setFreeTextParameters(row.getFreeTextParameters());
                anatomyRow.setPatoOntologyBeans(row.getPatoOntologyBeans());
                if (!row.getImageList().isEmpty()) {
                    anatomyRow.setHasImages(true);
                }


            }


        }
        for (String anatomy : anatomyToRowMap.keySet()) {
            if (anatomyToRowMap.get(anatomy).getSignificantCount() > 0) {
                collapsedRows.add(anatomyToRowMap.get(anatomy));
            }
        }
        return collapsedRows;
    }

    //get observations that have the same anatomy name, sampleid, sequence_id
    public Map<String, List<ObservationDTO>> getUniqueInfo(List<ObservationDTO> allObservations) {
        Map<String, List<ObservationDTO>> uniqueDataSets = new HashMap<>();

        for (ObservationDTO obs : allObservations) {
            String key = this.getAnatomyStringFromObservation(obs) + histoDelimeter + obs.getExternalSampleId() + histoDelimeter + obs.getSequenceId();
            if (uniqueDataSets.containsKey(key)) {
                uniqueDataSets.get(key).add(obs);
            } else {
                ArrayList<ObservationDTO> newList = new ArrayList<>();
                newList.add(obs);
                uniqueDataSets.put(key, newList);
            }
        }
        logger.info("unique datasets for histopath size = " + uniqueDataSets.size());
        return uniqueDataSets;
    }
}
