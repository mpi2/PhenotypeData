package org.mousephenotype.cda.releasenotes;


import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.apache.solr.client.solrj.SolrServerException;
import org.mousephenotype.cda.db.pojo.AnalyticsSignificantCallsProcedures;
import org.mousephenotype.cda.db.pojo.MetaHistory;
import org.mousephenotype.cda.db.pojo.MetaInfo;
import org.mousephenotype.cda.db.repositories.AnalyticsSignificantCallsProceduresRepository;
import org.mousephenotype.cda.db.repositories.MetaHistoryRepository;
import org.mousephenotype.cda.db.repositories.MetaInfoRepository;
import org.mousephenotype.cda.enumerations.BiologicalSampleType;
import org.mousephenotype.cda.solr.service.GenotypePhenotypeService;
import org.mousephenotype.cda.solr.service.ObservationService;
import org.mousephenotype.cda.solr.service.dto.GenotypePhenotypeDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Populate meta_info table and associated tables to a new datarelease. Must be run at the end of the release process, after the solr cores are built as well.
 * This is a replacement for the one in AdminTools.
 */
@Component
public class ReleaseAnalyticsManager implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(ReleaseAnalyticsManager.class);

    public final List<String> INCLUDED_RESOURCE = Arrays.asList("IMPC");
    private final Set<MetaInfo> dataReleaseFacts = new HashSet<>();
    private final Set<AnalyticsSignificantCallsProcedures> significantProcedures = new HashSet<>();


    @Value("${git.branch}")
    private String gitBranch;

    private final ObservationService observationService;
    private final GenotypePhenotypeService genotypePhenotypeService;
    private final MetaInfoRepository metaInfoRepository;
    private final MetaHistoryRepository metaHistoryRepository;
    private final AnalyticsSignificantCallsProceduresRepository analyticsSignificantCallsProceduresRepository;

    @Inject
    public ReleaseAnalyticsManager(
            @NotNull ObservationService observationService,
            @NotNull @Named("genotype-phenotype-service") GenotypePhenotypeService genotypePhenotypeService,
            @NotNull MetaInfoRepository metaInfoRepository,
            @NotNull MetaHistoryRepository metaHistoryRepository,
            @NotNull AnalyticsSignificantCallsProceduresRepository analyticsSignificantCallsProceduresRepository
    ) throws SQLException {
        this.observationService = observationService;
        this.genotypePhenotypeService = genotypePhenotypeService;
        this.metaInfoRepository = metaInfoRepository;
        this.metaHistoryRepository = metaHistoryRepository;
        this.analyticsSignificantCallsProceduresRepository = analyticsSignificantCallsProceduresRepository;
    }

    @Override
    public void run(String... args) throws Exception {

        OptionParser parser = new OptionParser();

        parser.allowsUnrecognizedOptions();
        parser.accepts("dr").withRequiredArg().ofType(String.class).describedAs("Data release version (e.g., \"12.0\")").required();
        parser.accepts("drdate").withRequiredArg().ofType(String.class).describedAs("Data release date (e.g., \"16 October 2009\")").required();
        parser.accepts("psversion").withRequiredArg().ofType(String.class).describedAs("Statistical pipeline and version (e.g., \"OpenStats 1.0.2\")").required();

        OptionSet options;
        try {
            options = parser.parse(args);
        }catch (OptionException e) {
            logger.info("Error with supplied arguments: " + e.getMessage());
            return;
        }

        DATA_RELEASE_VERSION = (String) options.valueOf("dr");
        DATA_RELEASE_DATE = (String) options.valueOf("drdate");
        PHENSTAT_VERSION = (String) options.valueOf("psversion");
        DATA_RELEASE_DESCRIPTION = "Major data release " + DATA_RELEASE_VERSION + ", released on " + DATA_RELEASE_DATE + ", analysed using " + PHENSTAT_VERSION;

        logger.info("ReleaseAnalyticsManage preparing metadata for: " + DATA_RELEASE_DESCRIPTION);
        // Add all facts about this release to the dataReleaseFacts set

        updateStaticFacts();

        populateMetaInfo();

        final List<GenotypePhenotypeDTO> allGenotypePhenotypes = genotypePhenotypeService
                .getAllGenotypePhenotypes(INCLUDED_RESOURCE)
                .stream()
                .filter(x ->x.getTopLevelMpTermId()!=null)
                .collect(Collectors.toList());

        populateMetaInfoWithMPTerms(allGenotypePhenotypes);
        populateSignificanceCalls(allGenotypePhenotypes);

        // Persist all facts to the database
        metaInfoRepository.deleteAll();
        metaInfoRepository.saveAll(dataReleaseFacts);
        logger.info("\n\n\n\nFACTS\n");
        dataReleaseFacts.forEach(x -> logger.info(String.valueOf(x)));

        // Persist all facts to meta history
        metaHistoryRepository.deleteAllByDataReleaseVersion(DATA_RELEASE_VERSION);
        Set<MetaHistory> history = dataReleaseFacts.stream()
                .map(x -> new MetaHistory(x, DATA_RELEASE_VERSION))
                .collect(Collectors.toSet());
        metaHistoryRepository.saveAll(history);

        // Persist all facts to the database
        analyticsSignificantCallsProceduresRepository.deleteAll();
        analyticsSignificantCallsProceduresRepository.saveAll(significantProcedures);
        logger.info("\n\n\n\nSIGNIFICANT PROCEDURES\n");
        significantProcedures.forEach(x -> logger.info(String.valueOf(x)));
    }


    //
    // DATA RELEASE INFORMATION
    //
    private String DATA_RELEASE_VERSION;
    private String DATA_RELEASE_DATE;
    private String PHENSTAT_VERSION;
    private String DATA_RELEASE_DESCRIPTION;

    // Patterns for regular expressions
    public static final String ALLELE_NOMENCLATURE = "^[^<]+<([^\\(]+)\\(([^\\)]+)\\)([^>]+)>";
    public static final String TARGETED_ALLELE_CLASS_1 = "^tm(\\d+)([a-z]{0,1})";
    public static final String TARGETED_ALLELE_CLASS_2 = "^em(\\d+)([a-z]{0,1})";


    /**
     * Facts about the data release.
     */
    public void updateStaticFacts() {

        dataReleaseFacts.add(new MetaInfo("data_release_version", DATA_RELEASE_VERSION, DATA_RELEASE_DESCRIPTION));
        dataReleaseFacts.add(new MetaInfo("data_release_date", DATA_RELEASE_DATE, DATA_RELEASE_DATE));
        dataReleaseFacts.add(new MetaInfo("statistical_packages", "OpenStats", "BioConductor Statistical package used to compute genotype to phenotype significant associations"));
        dataReleaseFacts.add(new MetaInfo("PhenStat_release_version", PHENSTAT_VERSION, "Version of statistical pipeline used to analyse phenotype data"));
        dataReleaseFacts.add(new MetaInfo("PhenStat_repository", "https://github.com/mpi2/OpenStats","GitHub repository for statistical pipeline code"));
        dataReleaseFacts.add(new MetaInfo("code_release_version", gitBranch, "Code used to build the database and to run the portal"));
        dataReleaseFacts.add(new MetaInfo("code_repository", "https://github.com/mpi2/PhenotypeData", "GitHub repository for the mouse phenotype portal"));
        dataReleaseFacts.add(new MetaInfo("ftp_site", "ftp://ftp.ebi.ac.uk/pub/databases/impc/release-" + DATA_RELEASE_VERSION, "Location of latest release data on FTP site"));
        dataReleaseFacts.add(new MetaInfo("genome_assembly_version", "GRCm38", "Genome Reference Consortium GRCm38"));
        dataReleaseFacts.add(new MetaInfo("species", "Mus musculus", "Mus musculus phenotype database"));
        dataReleaseFacts.add(new MetaInfo("statistically_significant_threshold", "1x10-4", "Statistical significance threshold used to define a significant difference from the null hypothesis"));

    }




    Pattern allelePattern = Pattern.compile(ALLELE_NOMENCLATURE);
    Pattern targetedClass1Pattern = Pattern.compile(TARGETED_ALLELE_CLASS_1);
    Pattern targetedClass2Pattern = Pattern.compile(TARGETED_ALLELE_CLASS_2);

    Map<String, Integer>          alleleTypes      = new HashMap<>();
    Map<String, Integer>          vectorProjects   = new HashMap<>();
    Map<String, Integer>          phenotypedLines  = new HashMap<>();
    Map<String, Integer>          mutantSpecimens  = new HashMap<>();
    Map<String, Integer>          controlSpecimens = new HashMap<>();
    Map<String, List<String>>     centerPipelines  = new HashMap<>();


    public void populateSignificanceCalls(List<GenotypePhenotypeDTO> allGenotypePhenotypes) {

        Map<Triple<String, String, String>, AtomicLong> sigCalls = new HashMap<>();

        for (GenotypePhenotypeDTO doc : allGenotypePhenotypes) {
            Triple<String, String, String> call = Triple.of(doc.getPhenotypingCenter(), doc.getProcedureStableId().get(0), doc.getProcedureName());
            if (!sigCalls.containsKey(call)) {
                sigCalls.put(call, new AtomicLong(0));
            }
            sigCalls.get(call).incrementAndGet();
        }

        for (Triple<String, String, String> entry : sigCalls.keySet()) {
            significantProcedures.add(new AnalyticsSignificantCallsProcedures(entry.getLeft(), entry.getMiddle(), entry.getRight(), sigCalls.get(entry).get()));
        }


    }

    private void populateMetaInfoWithMPTerms(List<GenotypePhenotypeDTO> allGenotypePhenotypes) {


        // Top level MP categories
        final Set<Pair<String, String>> topLevels = allGenotypePhenotypes.stream()
                .map(x -> {
                    List<Pair<String, String>> pairs = new ArrayList<>();
                    for (int i = 0; i < x.getTopLevelMpTermName().size(); i++) {
                        pairs.add(Pair.of(x.getTopLevelMpTermId().get(i), x.getTopLevelMpTermName().get(i)));
                    }
                    return pairs;
                })
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());

        for (Pair<String, String> pair : topLevels) {
            dataReleaseFacts.add(new MetaInfo("top_level_"+pair.getLeft(), pair.getRight(), "Mammalian Phenotype top-level category " + pair.getLeft()));
        }

        dataReleaseFacts.add(new MetaInfo("top_level_mps", topLevels.stream().map(Pair::getLeft).collect(Collectors.joining(",")), "top-level MP terms"));

        final Map<String, AtomicLong> countBytopLevels = new HashMap<>();

        for (GenotypePhenotypeDTO doc : allGenotypePhenotypes) {
            for (String top : doc.getTopLevelMpTermId()) {
                if ( ! countBytopLevels.containsKey(top)) {
                    countBytopLevels.put(top, new AtomicLong(0));
                }
                countBytopLevels.get(top).incrementAndGet();
            }
        }
        for (String key : countBytopLevels.keySet()) {
            dataReleaseFacts.add(new MetaInfo("top_level_" + key + "_calls", countBytopLevels.get(key).toString(), "Number of associations to top-level MP " + key));
        }

    }



    public void populateMetaInfo() throws IOException, SolrServerException {

        // number of significant calls
        final List<GenotypePhenotypeDTO> impcPhenotypeCalls = genotypePhenotypeService.getAllGenotypePhenotypes(INCLUDED_RESOURCE);
        int significantCalls = impcPhenotypeCalls.size();
        logger.info("Significant phenotype calls:\t" + significantCalls);
        dataReleaseFacts.add(new MetaInfo("statistically_significant_calls", Integer.toString(significantCalls), "Number of statistically significant calls"));

        // number of genes
        final Set<String> impcGenes = observationService.getAllGeneIdsByResource(INCLUDED_RESOURCE, true);
        int totalGenes = impcGenes.size();
        logger.info("Total genes:\t" + totalGenes);
        dataReleaseFacts.add(new MetaInfo("phenotyped_genes", Integer.toString(totalGenes), "Number of distinct genes with phenotype data"));


        // total mutants per center
        final Set<String> impcLines = observationService.getAllColonyIdsByResource(INCLUDED_RESOURCE, true);
        int totalMutantLines = impcLines.size();
        logger.info("Total mutant lines:\t" + totalMutantLines);
        dataReleaseFacts.add(new MetaInfo("phenotyped_lines", Integer.toString(totalMutantLines), "Number of distinct lines with phenotype data"));


        final Map<String, Set<String>> coloniesByPhenotypingCenter = observationService.getColoniesByPhenotypingCenter(INCLUDED_RESOURCE, null);
        logger.info("Total mutant colonies for centers:\t" + Arrays.toString(coloniesByPhenotypingCenter.entrySet().toArray()));
        phenotypedLines = coloniesByPhenotypingCenter.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, x->x.getValue().size()));

        dataReleaseFacts.add(new MetaInfo("phenotyped_lines_centers", String.join(", ", (phenotypedLines.keySet())), "Comma separated list of centers having submitted complete phenotype data"));

        for (String center : phenotypedLines.keySet()) {
            dataReleaseFacts.add(new MetaInfo("phenotyped_lines_" + center, phenotypedLines.get(center).toString(), "Number of lines with phenotype data from " + center));
        }


        // Calculate counts of mutants and controls per center
        final Map<String, Map<String, Integer>> datapointsByPhenotypingCenterAndSampleGroup = observationService.getDatapointsByPhenotypingCenterAndSampleGroup(INCLUDED_RESOURCE);
        for (String center : datapointsByPhenotypingCenterAndSampleGroup.keySet()) {
            for (String sampleGroup : datapointsByPhenotypingCenterAndSampleGroup.get(center).keySet()) {
                final Map<String, Integer> correctSet = (sampleGroup.equals(BiologicalSampleType.control.getName())) ? controlSpecimens : mutantSpecimens;
                correctSet.put(center, datapointsByPhenotypingCenterAndSampleGroup.get(center).get(sampleGroup));
            }
        }
        logger.info("Total counts of mutants/controls by center:\t" + Arrays.toString(datapointsByPhenotypingCenterAndSampleGroup.entrySet().toArray()));
        for (String center : phenotypedLines.keySet()) {
            dataReleaseFacts.add(new MetaInfo("control_specimens_" + center, controlSpecimens.get(center).toString(), "Number of control specimens with phenotype data from " + center));
            dataReleaseFacts.add(new MetaInfo("mutant_specimens_" + center, mutantSpecimens.get(center).toString(), "Number of mutant specimens with phenotype data from " + center));
        }


        // List pipeline per center
        final Map<String, List<String>> pipelineByCenter = observationService.getPipelineByCenter(INCLUDED_RESOURCE);
        logger.info("Pipelines by centers:\t" + Arrays.toString(pipelineByCenter.entrySet().toArray()));
        centerPipelines = pipelineByCenter.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, x->new ArrayList<>(x.getValue())));
        for (String center : centerPipelines.keySet()) {
            dataReleaseFacts.add(new MetaInfo("phenotype_pipelines_" + center, String.join(", ", (centerPipelines.get(center))), "Comma separated list of phenotype pipeline for center " + center));
        }


        // Targeted allele type, check tm1a, tm1b, etc...
        final Set<String> alleles = observationService.getAllAlleleSymbolsByResource(INCLUDED_RESOURCE, true);
        logger.info("Total allele symbols:\t" + alleles.size());
        alleles.forEach(this::getAlleleType);

        logger.info("Vector projects:\t" + Arrays.toString(vectorProjects.entrySet().toArray()));
        dataReleaseFacts.add(new MetaInfo("mouse_knockout_programs", String.join(", ", (vectorProjects.keySet())), "Comma separated list of mouse knockout programs having contributed to IMPC"));
        for (String program : vectorProjects.keySet()) {
            dataReleaseFacts.add(new MetaInfo("alleles_" + program, vectorProjects.get(program).toString(), "Number of " + program + " alleles"));
        }

        logger.info("Allele types:\t" + Arrays.toString(alleleTypes.entrySet().toArray()));
        dataReleaseFacts.add(new MetaInfo("targeted_allele_types", String.join(", ", (alleleTypes.keySet())), "Comma separated list of mouse knockout allele types"));
        for (String alleleType : alleleTypes.keySet()) {
            dataReleaseFacts.add(new MetaInfo("targeted_allele_type_" + alleleType, alleleTypes.get(alleleType).toString(), "Number of " + alleleType + " alleles"));
        }


        // Data types
        final Map<String, Long> dataPointCountByType = observationService.getDataPointCountByType(INCLUDED_RESOURCE);
        dataReleaseFacts.add(new MetaInfo("datapoint_types", String.join(", ", (dataPointCountByType.keySet())), "Types for measured data"));
        for (String observationType : dataPointCountByType.keySet()) {
            dataReleaseFacts.add(new MetaInfo(observationType + "_datapoints_QC_passed", dataPointCountByType.get(observationType).toString(), "Total number of " + observationType + " datapoints that passed QC"));
            dataReleaseFacts.add(new MetaInfo(observationType + "_datapoints_QC_failed", "-", "Total number of " + observationType + " datapoints that failed QC"));
            dataReleaseFacts.add(new MetaInfo(observationType + "_datapoints_QC_failed_no_status_code", "-", "Total number of " + observationType + " datapoints that failed QC"));
            dataReleaseFacts.add(new MetaInfo(observationType + "_datapoints_issues", "-", "Total number of " + observationType + " datapoints that have issues"));
        }

    }


    protected void getAlleleType(String allele) {

        if (allele == null) {
            logger.info("Found null allele symbol");
            return;
        }

        Matcher matcher = allelePattern.matcher(allele);
        while (matcher.find()) {
            if (matcher.groupCount() == 3) {
                String vectorProject = matcher.group(2);

                // Required to fix bad data entry in the iMits system
                // The allele superscript is defined for gene Ctps2 as lowercase "impc" instead of the proper "IMPC"
                if (vectorProject.contains("impc")) {
                    vectorProject = vectorProject.toUpperCase();
                }
                if (!vectorProjects.containsKey(vectorProject)) {
                    vectorProjects.put(vectorProject, 0);
                }
                vectorProjects.put(vectorProject, vectorProjects.get(vectorProject) + 1);

                if (targetedClass1Pattern.matcher(matcher.group(1)).matches()) {
                    // This appears to be a targeted mutation
                    Matcher match_targeted = targetedClass1Pattern.matcher(matcher.group(1));
                    if (match_targeted.find()) {

                        String alleleType;

                        if (match_targeted.group(2).isEmpty()) {
                            alleleType = match_targeted.group(1);
                        } else {
                            alleleType = match_targeted.group(2);
                        }

                        if (!alleleTypes.containsKey(alleleType)) {
                            alleleTypes.put(alleleType, 0);
                        }
                        alleleTypes.put(alleleType, alleleTypes.get(alleleType) + 1);
                    } else {
                        System.err.println("Could not match targeted allele:" + allele);
                    }
                } else if (targetedClass2Pattern.matcher(matcher.group(1)).matches()) {
                    // This appears to be a crispr mutation
                    Matcher match_targeted = targetedClass2Pattern.matcher(matcher.group(1));
                    if (match_targeted.find()) {

                        String alleleType = "CRISPR";

                        if (!alleleTypes.containsKey(alleleType)) {
                            alleleTypes.put(alleleType, 0);
                        }
                        alleleTypes.put(alleleType, alleleTypes.get(alleleType) + 1);
                    } else {
                        System.err.println("Could not match crispr allele:" + allele);
                    }
                }
            }
        }
    }


}