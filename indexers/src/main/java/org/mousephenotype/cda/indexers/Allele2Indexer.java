package org.mousephenotype.cda.indexers;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.mousephenotype.cda.db.repositories.OntologyTermRepository;
import org.mousephenotype.cda.indexers.exceptions.IndexerException;
import org.mousephenotype.cda.solr.service.dto.Allele2DTO;
import org.mousephenotype.cda.utilities.RunStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;

import javax.inject.Inject;
import javax.sql.DataSource;
import javax.validation.constraints.NotNull;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ilinca on 23/09/2016.
 */

@EnableAutoConfiguration
public class Allele2Indexer  extends AbstractIndexer implements CommandLineRunner {

    @Value("${allele2File}")
    private String pathToAlleleFile;

    private       Integer              alleleDocCount;
    private       Map<String, Integer> columns = new HashMap<>();
    private final Logger               logger  = LoggerFactory.getLogger(this.getClass());
    private       SolrClient           allele2Core;

    protected Allele2Indexer() {

    }

    @Inject
    public Allele2Indexer(
            @NotNull DataSource komp2DataSource,
            @NotNull OntologyTermRepository ontologyTermRepository,
            @NotNull SolrClient allele2Core) {
        super(komp2DataSource, ontologyTermRepository);
        this.allele2Core = allele2Core;
    }


    @Override
    public RunStatus run() throws IOException, SolrServerException {

        RunStatus runStatus = new RunStatus();

        allele2Core.deleteByQuery("*:*");
        allele2Core.commit();

        long start = System.currentTimeMillis();
        BufferedReader in = new BufferedReader(new FileReader(new File(pathToAlleleFile)));
        String[] header = in.readLine().split("\t");
        for (int i = 0; i < header.length; i++){
            columns.put(header[i], i);
        }

        int index = 0 ;
        String line = in.readLine();

        while (line != null){

            String[] array = line.split("\t", -1);
            index ++;
            Allele2DTO doc = new Allele2DTO();

            doc.setAllele2Id(String.valueOf(index));
            doc.setAlleleCategory(getValueFor(Allele2DTO.ALLELE_CATEGORY, array, columns, runStatus));
            doc.setAlleleDescription(getValueFor(Allele2DTO.ALLELE_DESCRIPTION,array, columns, runStatus));
            doc.setAlleleImage(getValueFor(Allele2DTO.ALLELE_IMAGE,array, columns, runStatus));
            doc.setAlleleMgiAccessionId(getValueFor(Allele2DTO.ALLELE_MGI_ACCESSION_ID,array, columns, runStatus));
            doc.setAlleleName(getValueFor(Allele2DTO.ALLELE_NAME,array, columns, runStatus));
            doc.setAlleleSimpleImage(getValueFor(Allele2DTO.ALLELE_SIMPLE_IMAGE,array, columns, runStatus));
            doc.setAlleleType(getValueFor(Allele2DTO.ALLELE_TYPE,array, columns, runStatus));
            doc.setAlleleFeatures(getListValueFor(Allele2DTO.ALLELE_FEATURES, array, columns, runStatus));
            doc.setAlleleSymbol(getValueFor(Allele2DTO.ALLELE_SYMBOL, array, columns, runStatus));
            doc.setAlleleSymbolSearchVariants(getListValueFor(Allele2DTO.ALLELE_SYMBOL_SEARCH_VARIANTS, array, columns, runStatus));
            doc.setCassette(getValueFor(Allele2DTO.CASSETTE,array, columns, runStatus));
            doc.setDesignId(getValueFor(Allele2DTO.DESIGN_ID,array, columns, runStatus));
            doc.setEsCellStatus(getValueFor(Allele2DTO.ES_CELL_STATUS,array, columns, runStatus));
            doc.setEsCellAvailable(getBooleanValueFor(Allele2DTO.ES_CELL_AVAILABLE,array, columns, runStatus));
            doc.setFeatureChromosome(getValueFor(Allele2DTO.FEATURE_CHROMOSOME,array, columns, runStatus));
            doc.setFeatureStrand(getValueFor(Allele2DTO.FEATURE_STRAND,array, columns, runStatus));
            doc.setFeatureCoordEnd(getIntValueFor(Allele2DTO.FEATURE_COORD_END,array, columns, runStatus));
            doc.setFeatureCoordStart(getIntValueFor(Allele2DTO.FEAURE_COORD_START,array, columns, runStatus));
            doc.setFeatureType(getValueFor(Allele2DTO.FEATURE_TYPE,array, columns, runStatus));
            doc.setGenbankFile(getValueFor(Allele2DTO.GENBANK_FILE,array, columns, runStatus));
            doc.setGeneModelIds(getListValueFor(Allele2DTO.GENE_MODEL_IDS,array, columns, runStatus));
            doc.setGeneticMapLinks(getListValueFor(Allele2DTO.GENETIC_MAP_LINKS,array, columns, runStatus));
            doc.setIkmcProject(getListValueFor(Allele2DTO.IKMC_PROJECT,array, columns, runStatus));
            doc.setLatestEsCellStatus(getValueFor(Allele2DTO.LATEST_ES_CELL_STATUS,array, columns, runStatus));
            doc.setLatestMouseStatus(getValueFor(Allele2DTO.LATEST_MOUSE_STATUS,array, columns, runStatus));
            doc.setLatestPhenotypeComplete(getValueFor(Allele2DTO.LATEST_PHENOTYPE_COMPLETE,array, columns, runStatus));
            doc.setLatestPhenotypeStarted(getValueFor(Allele2DTO.LATEST_PHENOTYPE_STARTED,array, columns, runStatus));
            doc.setLatestProductionCentre(getListValueFor(Allele2DTO.LATEST_PRODUCTION_CENTRE,array, columns, runStatus));
            doc.setLatestPhenotypingCentre(getListValueFor(Allele2DTO.LATEST_PHENOTYPING_CENTRE,array, columns, runStatus));
            doc.setLatestPhenotypeStatus(getValueFor(Allele2DTO.LATEST_PHENOTYPE_STATUS,array, columns, runStatus));
            doc.setLatestProjectStatus(getValueFor(Allele2DTO.LATEST_PROJECT_STATUS,array, columns, runStatus));
            doc.setLatestProjectStatusLegacy(getValueFor(Allele2DTO.LATEST_PROJECT_STATUS_LEGACY,array, columns, runStatus));
            doc.setLinks(getListValueFor(Allele2DTO.LINKS,array, columns, runStatus));
            doc.setMarkerType(getValueFor(Allele2DTO.MARKER_TYPE,array, columns, runStatus));
            doc.setMarkerSymbol(getValueFor(Allele2DTO.MARKER_SYMBOL, array, columns, runStatus));
            doc.setMgiAccessionId(getValueFor(Allele2DTO.MGI_ACCESSION_ID, array, columns, runStatus));
            doc.setMarkerName(getValueFor(Allele2DTO.MARKER_NAME,array, columns, runStatus));
            doc.setMarkerSynonym(getListValueFor(Allele2DTO.MARKER_SYNONYM, array, columns, runStatus));
            doc.setHumanGeneSymbol(getListValueFor(Allele2DTO.HUMAN_GENE_SYMBOL, array, columns, runStatus));
            doc.setMouseAvailable(getBooleanValueFor(Allele2DTO.MOUSE_AVAILABLE, array, columns, runStatus));
            doc.setMutationType(getValueFor(Allele2DTO.MUTATION_TYPE,array,columns, runStatus));
            doc.setMouseStatus(getValueFor(Allele2DTO.MOUSE_STATUS, array,columns, runStatus));
            doc.setPhenotypeStatus(getValueFor(Allele2DTO.PHENOTYPE_STATUS,array, columns, runStatus));
            doc.setPhenotypingCentres(getListValueFor(Allele2DTO.PHENOTYPING_CENTRES,array, columns, runStatus));
            doc.setProductionCentres(getListValueFor(Allele2DTO.PRODUCTION_CENTRES,array, columns, runStatus));
            doc.setPipeline(getListValueFor(Allele2DTO.PIPELINE,array, columns, runStatus));
            doc.setSequenceMapLinks(getListValueFor(Allele2DTO.SEQUENCE_MAP_LINKS, array, columns, runStatus));
            doc.setSynonym(getListValueFor(Allele2DTO.SYNONYM, array, columns, runStatus));
            doc.setTargetingVectorAvailable(getBooleanValueFor(Allele2DTO.TARGETING_VECTOR_AVAILABLE, array, columns, runStatus));
            doc.setType(getValueFor(Allele2DTO.TYPE, array, columns, runStatus));
            doc.setVectorAlleleImage(getValueFor(Allele2DTO.VECTOR_ALLELE_IMAGE, array, columns, runStatus));
            doc.setVectorGenbankLink(getValueFor(Allele2DTO.VECTOR_GENBANK_LINK, array, columns, runStatus));
            doc.setWithoutAlleleFeatures(getListValueFor(Allele2DTO.WITHOUT_ALLELE_FEATURES, array, columns, runStatus));
            doc.setAlleleDesignProject(getValueFor(Allele2DTO.ALLELE_DESIGN_PROJECT, array, columns, runStatus));
            doc.setTissuesAvailable(getBooleanValueFor(Allele2DTO.TISSUES_AVAILABLE, array, columns, runStatus));
            doc.setTissueTypes(getListValueFor(Allele2DTO.TISSUE_TYPES, array, columns, runStatus));
            doc.setTissueEnquiryLinks(getListValueFor(Allele2DTO.TISSUE_ENQUIRY_LINKS, array, columns, runStatus));
            doc.setTissueDistributionCentres(getListValueFor(Allele2DTO.TISSUE_DISTRIBUTION_CENTRES, array, columns, runStatus));

            line = in.readLine();

            allele2Core.addBean(doc, 30000);

        }

        allele2Core.commit();
        alleleDocCount = index;

        logger.info("  Added {} total beans in {}", alleleDocCount, commonUtils.msToHms(System.currentTimeMillis() - start));

        return runStatus;

    }


    @Override
    public RunStatus validateBuild() throws IndexerException {

        RunStatus runStatus = new RunStatus();
        Long actualSolrDocumentCount = getImitsDocumentCount(allele2Core);

        if (actualSolrDocumentCount < alleleDocCount) {
           runStatus.addError("Expected " + alleleDocCount + " documents. Actual count: " + actualSolrDocumentCount + ".");
        }

        return runStatus;
    }


    public static void main(String[] args) throws IndexerException {
        SpringApplication.run(Allele2Indexer.class, args);
    }

}
