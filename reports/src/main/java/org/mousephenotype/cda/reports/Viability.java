/*******************************************************************************
 * Copyright © 2015 EMBL - European Bioinformatics Institute
 * <p>
 * Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this targetFile except in compliance
 * with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 ******************************************************************************/

package org.mousephenotype.cda.reports;

import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrServerException;
import org.mousephenotype.cda.common.Constants;
import org.mousephenotype.cda.reports.support.ReportException;
import org.mousephenotype.cda.solr.service.GeneService;
import org.mousephenotype.cda.solr.service.ObservationService;
import org.mousephenotype.cda.solr.service.dto.GeneDTO;
import org.mousephenotype.cda.solr.service.dto.ObservationDTO;
import org.mousephenotype.cda.solr.service.dto.ViabilityReportDTO;
import org.mousephenotype.cda.solr.web.dto.ViabilityDTOVersion1;
import org.mousephenotype.cda.solr.web.dto.ViabilityDTOVersion2;
import org.mousephenotype.cda.utilities.CommonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.beans.Introspector;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Viability report.
 *
 * Created by mrelac on 24/07/2015.
 */
@Component
public class Viability extends AbstractReport {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());
    private Map<String, String> chromosomeByGeneAccessionId;
    private boolean errorsFound = false;

    @Autowired
    private ObservationService observationService;

    @Autowired
    private GeneService geneService;


    public Viability() {
        super();
    }

    @Override
    public String getDefaultFilename() {
        return Introspector.decapitalize(ClassUtils.getShortClassName(this.getClass()));
    }

    Set<String>         genesMissingAcc = new HashSet<>();
    Set<String>         genesMissingChr = new HashSet<>();

    @Override
    protected void initialise(String[] args) throws ReportException {
        super.initialise(args);
        try {
            List<GeneDTO> geneData = geneService.getAllGeneDTOs();
            chromosomeByGeneAccessionId = geneData.stream()
                .filter(d -> {
                    if (d.getMgiAccessionId() == null) genesMissingAcc.add(d.getMarkerSymbol()); return d.getMgiAccessionId() != null;})
                .filter(d -> {
                    if (d.getChrName() == null) genesMissingChr.add(d.getMarkerSymbol()); return d.getChrName() != null;})
                .collect(Collectors.toMap(GeneDTO::getMgiAccessionId, GeneDTO::getChrName));
        } catch (SolrServerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if ( ! genesMissingAcc.isEmpty()) {
            logger.info("The following {} gene symbols have no accessionid:", genesMissingAcc.size());
            genesMissingAcc.stream().forEach(s -> System.out.println(s));
        }

        if ( ! genesMissingChr.isEmpty()) {
            logger.info("The following {} gene symbols have no associated chromosome:", genesMissingChr.size());
            genesMissingChr.stream().forEach(s -> System.out.println(s));
        }
    }

    public void run(String[] args) throws ReportException {
        List<String> errors = parser.validate(parser.parse(args));
        if ( ! errors.isEmpty()) {
            logger.error("Viability report parser validation error: " + StringUtils.join(errors, "\n"));
            return;
        }
        initialise(args);

        int  count = 0;
        long start = System.currentTimeMillis();

        List<ViabilityReportDTO> data = new ArrayList<>();
        try {
            logger.info("Query for viabilityObservations started.");

            Map<String, List<ObservationDTO>> results = observationService.getViabilityReportData();
            Map<String, Set<ObservationDTO>> observationsByCompositeKey;
            List<ObservationDTO> v1Dtos = results.get("IMPC_VIA_001");
            observationsByCompositeKey = getObservationsByCompositeKey(v1Dtos);
            for (Set<ObservationDTO> observationByCompositeKey : observationsByCompositeKey.values()) {
                // For each set, look for any category value. If found, use it.
                ObservationDTO dto = observationByCompositeKey
                    .stream()
                    .filter(d -> ((d.getCategory() != null) && ( ! d.getCategory().isEmpty())))
                    .findAny().orElse(null);
                if (dto != null) {
                    data.add(createViabilityReportRow(dto, getCountsByParameterStableId(observationByCompositeKey)));
                }
            }

            List<ObservationDTO> v2Dtos = results.get("IMPC_VIA_002");
            observationsByCompositeKey = getObservationsByCompositeKey(v2Dtos);
            for (Set<ObservationDTO> observationByCompositeKey : observationsByCompositeKey.values()) {
                // For each set, look for a text_value for parameter:
                //   'Homozygous males viability' (IMPC_VIA_067_001) OR
                //   'Hemizygous males viability' (IMPC_VIA_055_001) OR
                //   'Anzygous females viability' (IMPC_VIA_056_001).
                // There may be 0 (e.g. null) or 1 (findAny()).
                ObservationDTO dto = observationByCompositeKey
                    .stream()
                    .filter(d -> ((d.getTextValue() != null)
                        && ( ! d.getTextValue().isEmpty())
                        && ((d.getParameterStableId().equalsIgnoreCase(Constants.HOM_VIABILITY_ALL_ID))
                        || (d.getParameterStableId().equalsIgnoreCase(Constants.HEM_VIABILITY_MALE_ID)))))
                    .findAny().orElse(null);
                if (dto != null) {
                    data.add(createViabilityReportRow(dto, getCountsByParameterStableId(observationByCompositeKey)));
                }
            }
        } catch (IOException | SolrServerException e) {
            throw new ReportException(e);
        }

        csvWriter.write(getHeading());
        count++;

        // Compute comment column
        data = computeCommentColumnStrings(data);

        // Sort by: geneSymbol, alleleSymbol, strainName, phenotypingCenter, colonyId, viabilityPhenotype
        Collections.sort(data,
             Comparator.comparing(ViabilityReportDTO::getGeneSymbol)
                 .thenComparing(ViabilityReportDTO::getAlleleSymbol)
                 .thenComparing(ViabilityReportDTO::getBackgroundStrainName)
                 .thenComparing(ViabilityReportDTO::getPhenotypingCenter)
                 .thenComparing(ViabilityReportDTO::getColonyId)
                 .thenComparing(ViabilityReportDTO::getViabilityPhenotype)
        );

        // Write the data
        for (ViabilityReportDTO rDto : data) {
            csvWriter.write(getReportRow(rDto));
            count++;
        }

        csvWriter.closeQuietly();

        log.info(String.format(
            "Finished. %s detail rows written in %s",
            count, commonUtils.msToHms(System.currentTimeMillis() - start)));

        if (errorsFound) {
            throw new ReportException();
        }
    }

    // PRIVATE METHODS

    private ViabilityReportDTO addTotalsToReportDTO(ViabilityReportDTO rDto,
                                                    String procedureStableId,
                                                    Map<String, Double> dataPointsByParamId) throws ReportException
    {
        Double totalFemaleHom; ;
        Double totalMaleHom;
        Double totalMaleHem;
        Double totalPups;
        ViabilityDTOVersion1 v1 = new ViabilityDTOVersion1();
        ViabilityDTOVersion2 v2 = new ViabilityDTOVersion2();
        switch(procedureStableId) {
            case "IMPC_VIA_001":
                totalFemaleHom = dataPointsByParamId.get(v1.getTotalFemaleHom());
                totalMaleHom = dataPointsByParamId.get(v1.getTotalMaleHom());
                totalMaleHem = 0.0;
                totalPups = dataPointsByParamId.get(v1.getTotalPups());
                Double d = (totalFemaleHom + totalMaleHom + totalMaleHem) / totalPups;
                rDto.setPercentageHoms(d.toString());
                rDto.setTotalFemaleAnz(Constants.NO_INFORMATION_AVAILABLE);
                rDto.setTotalFemalePups(toStringTrunc(dataPointsByParamId.getOrDefault(v1.getTotalFemalePups(), -1.0)));
                rDto.setTotalMaleHem(Constants.NO_INFORMATION_AVAILABLE);
                rDto.setTotalFemaleHet(toStringTrunc(dataPointsByParamId.getOrDefault(v1.getTotalFemaleHet(), -1.0)));
                rDto.setTotalMaleHet(toStringTrunc(dataPointsByParamId.getOrDefault(v1.getTotalMaleHet(), -1.0)));
                rDto.setTotalFemaleHom(toStringTrunc(dataPointsByParamId.getOrDefault(v1.getTotalFemaleHom(), -1.0)));
                rDto.setTotalMaleHom(toStringTrunc(dataPointsByParamId.getOrDefault(v1.getTotalMaleHom(), -1.0)));
                rDto.setTotalMalePups(toStringTrunc(dataPointsByParamId.getOrDefault(v1.getTotalMalePups(), -1.0)));
                rDto.setTotalPups(toStringTrunc(dataPointsByParamId.getOrDefault(v1.getTotalPups(), -1.0)));
                rDto.setTotalFemaleWt(toStringTrunc(dataPointsByParamId.getOrDefault(v1.getTotalFemaleWt(), -1.0)));
                rDto.setTotalMaleWt(toStringTrunc(dataPointsByParamId.getOrDefault(v1.getTotalMaleWt(), -1.0)));
                break;

            case "IMPC_VIA_002":
                totalFemaleHom = dataPointsByParamId.getOrDefault(v2.getTotalFemaleHom(), -1.0);
                totalMaleHom = dataPointsByParamId.getOrDefault(v2.getTotalMaleHom(), -1.0);
                totalMaleHem = dataPointsByParamId.getOrDefault(v2.getTotalMaleHem(), -1.0);
                totalPups = dataPointsByParamId.getOrDefault(v2.getTotalPups(), -1.0);
                d = (totalFemaleHom + totalMaleHom + totalMaleHem) / totalPups;
                rDto.setPercentageHoms(d.toString());
                rDto.setTotalFemaleAnz(toStringTrunc(dataPointsByParamId.getOrDefault(v2.getTotalFemaleAnz(), -1.0)));
                rDto.setTotalFemalePups(toStringTrunc(dataPointsByParamId.getOrDefault(v2.getTotalFemalePups(), -1.0)));
                rDto.setTotalMaleHem(toStringTrunc(dataPointsByParamId.getOrDefault(v2.getTotalMaleHem(), -1.0)));
                rDto.setTotalFemaleHet(toStringTrunc(dataPointsByParamId.getOrDefault(v2.getTotalFemaleHet(), -1.0)));
                rDto.setTotalMaleHet(toStringTrunc(dataPointsByParamId.getOrDefault(v2.getTotalMaleHet(), -1.0)));
                rDto.setTotalFemaleHom(toStringTrunc(dataPointsByParamId.getOrDefault(v2.getTotalFemaleHom(), -1.0)));
                rDto.setTotalMaleHom(toStringTrunc(dataPointsByParamId.getOrDefault(v2.getTotalMaleHom(), -1.0)));
                rDto.setTotalMalePups(toStringTrunc(dataPointsByParamId.getOrDefault(v2.getTotalMalePups(), -1.0)));
                rDto.setTotalPups(toStringTrunc(dataPointsByParamId.getOrDefault(v2.getTotalPups(), -1.0)));
                rDto.setTotalFemaleWt(toStringTrunc(dataPointsByParamId.getOrDefault(v2.getTotalFemaleWt(), -1.0)));
                rDto.setTotalMaleWt(toStringTrunc(dataPointsByParamId.getOrDefault(v2.getTotalMaleWt(), -1.0)));
                break;

            default:
                throw new ReportException("Expected procedureStableId IMPC_VIA_001 or IMPC_VIA_002");
        }

        return rDto;
    }

    private List<ViabilityReportDTO> computeCommentColumnStrings(List<ViabilityReportDTO> data) {
        Map<String, Map<String, List<ViabilityReportDTO>>> viabilitiesByGeneSymbol =
            data.stream().collect(Collectors.groupingBy(
                ViabilityReportDTO::getGeneSymbol,
                Collectors.groupingBy(ViabilityReportDTO::getViabilityPhenotype)));

        Set<String> conflicts = new HashSet<>();
        for (Map.Entry e : viabilitiesByGeneSymbol.entrySet()) {
            if (((HashMap)e.getValue()).size() > 1) {
                conflicts.add(e.getKey().toString());
            }
        }

        data.stream()
            .forEach(vDto -> {
                if (conflicts.contains(vDto.getGeneSymbol()))
                    vDto.setComment("Conflicting data");
            });

        return data;
    }

    private ViabilityReportDTO createViabilityReportRow(ObservationDTO oDto, Map<String, Double> dataPointsByParamId) {
        ViabilityReportDTO rDto = new ViabilityReportDTO();
        try {
            rDto.setGeneSymbol(oDto.getGeneSymbol());
            rDto.setGeneAccessionId(oDto.getGeneAccession());
            rDto.setAlleleSymbol(oDto.getAlleleSymbol());
            rDto.setAlleleAccessionId(oDto.getAlleleAccession());
            rDto.setBackgroundStrainName(oDto.getStrainName());
            rDto.setBackgroundStrainAccessionId(oDto.getStrainAccessionId());
            rDto.setColonyId(oDto.getColonyId());
            rDto.setPhenotypingCenter(oDto.getPhenotypingCenter());

            rDto.setBreedingStrategy(parseBreedingStrategy(oDto));
            String s = chromosomeByGeneAccessionId.get(oDto.getGeneAccession());
            rDto.setChromosome(s != null ? s : Constants.NO_INFORMATION_AVAILABLE);
            rDto = addTotalsToReportDTO(rDto,oDto.getProcedureStableId(), dataPointsByParamId);

            List<String> errors = validateData(rDto, oDto.getProcedureStableId());
            if ( ! errors.isEmpty()) {
                logger.error("Validation failure: ");
                errors.stream().forEach(e -> logger.error(e));
            }

            if (oDto.getProcedureStableId().equalsIgnoreCase("IMPC_VIA_001")) {
                rDto.setSupportingData(oDto.getCategory());
                rDto.setViabilityCallMethod("curated");
                rDto.setImpressInfoLink("https://www.mousephenotype.org/impress/ProcedureInfo?action=list&procID=703&pipeID=7");
            } else if (oDto.getProcedureStableId().equalsIgnoreCase("IMPC_VIA_002")) {
                rDto.setSupportingData(oDto.getTextValue());
                rDto.setViabilityCallMethod("computed");
                rDto.setImpressInfoLink("https://www.mousephenotype.org/impress/ProcedureInfo?action=list&procID=1188&pipeID=7");
            }

            rDto.setViabilityPhenotype(getViabilityFromSupportingData(rDto.getSupportingData()));
            rDto.setComment("");

        } catch (Exception e) {
            logger.error("Exception processing ObservationDTO {}. Skipping... Error: {}", oDto, e.getLocalizedMessage());
            e.printStackTrace();
        }

        return rDto;
    }

    private Map<String, Double> getCountsByParameterStableId(Set<ObservationDTO> dtos) {
        Map<String, Double> results = new HashMap<>();
        ViabilityDTOVersion1 data1 = new ViabilityDTOVersion1();
        ViabilityDTOVersion2 data2 = new ViabilityDTOVersion2();

        if ((dtos != null) && ( ! dtos.isEmpty())) {
            if (dtos.stream().findFirst().get().getProcedureStableId().equalsIgnoreCase("IMPC_VIA_001")) {
                dtos.stream().forEach(dto -> {
                    if ((dto.getParameterStableId().equalsIgnoreCase(data1.getTotalPups()))
                        || (dto.getParameterStableId().equalsIgnoreCase(data1.getTotalMalePups()))
                        || (dto.getParameterStableId().equalsIgnoreCase(data1.getTotalFemalePups()))
                        || (dto.getParameterStableId().equalsIgnoreCase(data1.getTotalMaleHom()))
                        || (dto.getParameterStableId().equalsIgnoreCase(data1.getTotalFemaleHom()))
                        || (dto.getParameterStableId().equalsIgnoreCase(data1.getTotalMaleWt()))
                        || (dto.getParameterStableId().equalsIgnoreCase(data1.getTotalFemaleWt()))
                        || (dto.getParameterStableId().equalsIgnoreCase(data1.getTotalMaleHet()))
                        || (dto.getParameterStableId().equalsIgnoreCase(data1.getTotalFemaleHet())))
                    {
                        results.put(dto.getParameterStableId(), dto.getDataPoint().doubleValue());
                    }
                });
            } else if (dtos.stream().findFirst().get().getProcedureStableId().equalsIgnoreCase("IMPC_VIA_002")) {
                dtos.stream().forEach(dto -> {
                    if ((dto.getParameterStableId().equalsIgnoreCase(data2.getTotalPups()))
                        || (dto.getParameterStableId().equalsIgnoreCase(data2.getTotalMalePups()))
                        || (dto.getParameterStableId().equalsIgnoreCase(data2.getTotalFemalePups()))
                        || (dto.getParameterStableId().equalsIgnoreCase(data2.getTotalMaleHom()))
                        || (dto.getParameterStableId().equalsIgnoreCase(data2.getTotalFemaleHom()))
                        || (dto.getParameterStableId().equalsIgnoreCase(data2.getTotalMaleHem()))
                        || (dto.getParameterStableId().equalsIgnoreCase(data2.getTotalFemaleAnz()))
                        || (dto.getParameterStableId().equalsIgnoreCase(data2.getTotalMaleWt()))
                        || (dto.getParameterStableId().equalsIgnoreCase(data2.getTotalFemaleWt()))
                        || (dto.getParameterStableId().equalsIgnoreCase(data2.getTotalMaleHet()))
                        || (dto.getParameterStableId().equalsIgnoreCase(data2.getTotalFemaleHet())))
                    {
                        results.put(dto.getParameterStableId(), dto.getDataPoint().doubleValue());
                    }
                });
            }
        }

        return results;
    }

    private List<String> getHeading() {
        return Arrays.asList(
            "Gene Symbol",
            "Gene Accession Id",
            "Allele Symbol",
            "Allele Accession Id",
            "Background Strain Name",
            "Background Strain Accession Id",
            "Chromosome",
            "Phenotyping Center",
            "Colony Id",
            "Breeding Strategy",
            "Total # Pups",
            "Total # Male Pups",
            "Total # Female Pups",
            "Total # Hom Males",
            "Total # Hom Females",
            "Total # Hem Males",
            "Total # WT Males",
            "Total # WT Females",
            "Total # Het Males",
            "Total # Het Females",
            "Percentage HOMs / HEMs",
            "Supporting Data",
            "Viability Phenotype HOMs/HEMIs",
            "Viability Call Method",
            "Comment",
            "Procedure Link");
    }

    private Map<String, Set<ObservationDTO>> getObservationsByCompositeKey(List<ObservationDTO> dtos) {
        return dtos
            .stream()
            .collect(Collectors.groupingBy(d -> new StringBuilder()
                .append(d.getGeneSymbol()).append("::")
                .append(d.getAlleleSymbol()).append("::")
                .append(d.getStrainName())
                .append(d.getColonyId()).append("::")
                .toString(), Collectors.toSet()));
    }

    private List<String> getReportRow(ViabilityReportDTO rDto) {
        List<String> data = new ArrayList<>();
        data.add(rDto.getGeneSymbol());
        data.add(rDto.getGeneAccessionId());
        data.add(rDto.getAlleleSymbol());
        data.add(rDto.getAlleleAccessionId());
        data.add(rDto.getBackgroundStrainName());
        data.add(rDto.getBackgroundStrainAccessionId());
        data.add(rDto.getChromosome());
        data.add(rDto.getPhenotypingCenter());
        data.add(rDto.getColonyId());
        data.add(rDto.getBreedingStrategy());
        data.add(rDto.getTotalPups());
        data.add(rDto.getTotalMalePups());
        data.add(rDto.getTotalFemalePups());
        data.add(rDto.getTotalMaleHom());
        data.add(rDto.getTotalFemaleHom());
        data.add(rDto.getTotalMaleHem());
        data.add(rDto.getTotalMaleWt());
        data.add(rDto.getTotalFemaleWt());
        data.add(rDto.getTotalMaleHet());
        data.add(rDto.getTotalFemaleHet());
        data.add(rDto.getPercentageHoms());
        data.add(rDto.getSupportingData());
        data.add(rDto.getViabilityPhenotype());
        data.add(rDto.getViabilityCallMethod());
        data.add(rDto.getComment());
        data.add(rDto.getImpressProcedureLink());
        return data;
    }

    // Returned String formats: IMPC_VIA_001: HetXHet
    // MPC_VIA_002: Female parents genotype = Heterozygous X Male parents genotype = Heterozygous
    private String parseBreedingStrategy(ObservationDTO oDto) throws ReportException {
        final String BS_001_KEY        = "Breeding Strategy";
        final String BS_002_MALE_KEY   = "Male parents genotype";
        final String BS_002_FEMALE_KEY = "Female parents genotype";
        String       result            = Constants.NO_INFORMATION_AVAILABLE;

        Map<String, String> metadataMap = oDto.getMetadata().stream().collect(Collectors.toMap(
            s -> StringUtils.split(s, '=')[0].trim(),
            s -> StringUtils.split(s, '=')[1].trim()));

        switch (oDto.getProcedureStableId()) {
            // e.g. IMPC_VIA_001::Age of pups at genotype = 2, Breeding Strategy = HetXHet, Time of dark cycle end = 04:00:00, Time of dark cycle start = 18:00:00
            case "IMPC_VIA_001":
                if (metadataMap.containsKey(BS_001_KEY)) {
                    result = metadataMap.get(BS_001_KEY).trim();
                }
                break;

            // e.g. IMPC_VIA_002::[Age of pups at genotype = 2, Female parents genotype = Heterozygous, Gene category = Autosomal, Male parents genotype = Heterozygous
            case "IMPC_VIA_002":
                result = new StringBuilder()
                    .append(BS_002_FEMALE_KEY)
                    .append(" = ")
                    .append(metadataMap.containsKey(BS_002_FEMALE_KEY)
                                ? metadataMap.get(BS_002_FEMALE_KEY)
                                : Constants.NO_INFORMATION_AVAILABLE)
                    .append(" X ")
                    .append(BS_002_MALE_KEY)
                    .append(" = ")
                    .append(metadataMap.containsKey(BS_002_MALE_KEY)
                                ? metadataMap.get(BS_002_MALE_KEY)
                                : Constants.NO_INFORMATION_AVAILABLE)
                    .toString();
                break;

            default:
                throw new ReportException("Breeding strategy not found for dto " + oDto.toString());
        }

        return result;
    }

    private String toStringTrunc(Double d) {
        return new Integer(d.intValue()).toString();
    }

    private String getViabilityFromSupportingData(String supportingData) {
        if (supportingData.toLowerCase().contains("lethal")) {
            return "lethal";
        } else if (supportingData.toLowerCase().contains("subviable")) {
            return "subviable";
        } else {
            return "viable";
        }
    }

    // Returns empty list if validation passes; else list of error(s).
    List<String> validateData(ViabilityReportDTO rDto, String procedureStableId) {
        List<String> results = new ArrayList<>();

        // Procedure-specific validations
        switch (procedureStableId) {
            case "IMPC_VIA_001":
                break;

            case "IMPC_VIA:002":
                // Chromosome validation
                Integer totalMaleHem = CommonUtils.tryParseInt(rDto.getTotalMaleHem());
                totalMaleHem = (totalMaleHem == null ? 0 : totalMaleHem);
                if (totalMaleHem > 0) {
                    if ( ! rDto.getChromosome().equalsIgnoreCase("x")) {
                        results.add("Expected chromosome X because totalMaleHem = " + totalMaleHem.toString());
                    }
                }
                // total male hem/hom mutual exclusivity - if one is not zero, the other must be zero.
                Integer totalMaleHom = CommonUtils.tryParseInt(rDto.getTotalMaleHom());
                totalMaleHom = (totalMaleHom == null ? 0 : totalMaleHom);
                if (((totalMaleHom > 0) && (totalMaleHem != 0)) || ((totalMaleHem > 0) && (totalMaleHom != 0))) {
                    results.add(new StringBuilder("Failed totalMaleHem/Hom mutual exclusivity validation:")
                                    .append(" Total male hem: ").append(totalMaleHem.toString())
                                    .append(". Total male hom: ").append(totalMaleHom)
                                    .toString());
                }

                break;
        }

        // General validations
        Integer totalFemalePups = CommonUtils.tryParseInt(rDto.getTotalFemalePups());
        Integer totalMalePups = CommonUtils.tryParseInt(rDto.getTotalMalePups());
        Integer totalPups = CommonUtils.tryParseInt(rDto.getTotalPups());
        if (totalFemalePups + totalMalePups != totalPups) {
            results.add(new StringBuilder("pup count mismatch:")
                            .append(" Total female: ").append(totalFemalePups == null ? "0" : totalFemalePups.toString())
                            .append(". Total male: ").append(totalMalePups == null ? "0" : totalMalePups.toString())
                            .append(". Total pups: ").append(totalPups == null ? "0" : totalPups.toString())
                            .toString());
        }

        return results;
    }
}
