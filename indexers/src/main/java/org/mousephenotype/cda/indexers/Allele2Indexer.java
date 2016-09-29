package org.mousephenotype.cda.indexers;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.mousephenotype.cda.indexers.exceptions.IndexerException;
import org.mousephenotype.cda.solr.service.dto.Allele2DTO;
import org.mousephenotype.cda.utilities.RunStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;

import javax.validation.constraints.NotNull;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ilinca on 23/09/2016.
 */

@EnableAutoConfiguration
public class Allele2Indexer  extends AbstractIndexer implements CommandLineRunner {

    private final Logger logger = LoggerFactory.getLogger(Allele2Indexer.class);


    @NotNull
    @Value("${allele2File}")
    private String pathToAlleleFile;

    @Autowired
    @Qualifier("allele2Indexing")
    private SolrClient allele2Indexing;

    Integer alleleDocCount;
    Map<String, Integer> columns = new HashMap<>();

    @Override
    public RunStatus run() throws IndexerException, IOException, SolrServerException, SQLException {

        RunStatus runStatus = new RunStatus();

        allele2Indexing.deleteByQuery("*:*");
        allele2Indexing.commit();

        long time = System.currentTimeMillis();
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

            doc.setAlleleCategory(getValueFor(Allele2DTO.ALLELE_CATEGORY, array, columns, runStatus));
            doc.setAlleleDescription(getValueFor(Allele2DTO.ALLELE_DESCRIPTION,array, columns, runStatus));
            doc.setAlleleImage(getValueFor(Allele2DTO.ALLELE_IMAGE,array, columns, runStatus));
            doc.setAlleleMgiAccessionId(getValueFor(Allele2DTO.ALLELE_MGI_ACCESSION_ID,array, columns, runStatus));
            doc.setAlleleName(getValueFor(Allele2DTO.ALLELE_NAME,array, columns, runStatus));
            doc.setAlleleSimpleImage(getValueFor(Allele2DTO.ALLELE_SIMPLE_IMAGE,array, columns, runStatus));
            doc.setAlleleType(getValueFor(Allele2DTO.ALLELE_TYPE,array, columns, runStatus));
            doc.setAlleleFeatures(getListValueFor(Allele2DTO.ALLELE_FEATURES, array, columns, runStatus));
            doc.setAlleleSymbol(getListValueFor(Allele2DTO.ALLELE_SYMBOL, array, columns, runStatus));
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
            doc.setLatestProjectStatus(getValueFor(Allele2DTO.LATEST_PROJECT_STATUS,array, columns, runStatus));
            doc.setLatestProjectStatusLegacy(getValueFor(Allele2DTO.LATEST_PROJECT_STATUS_LEGACY,array, columns, runStatus));
            doc.setLinks(getListValueFor(Allele2DTO.LINKS,array, columns, runStatus));
            doc.setMarkerType(getValueFor(Allele2DTO.MARKER_TYPE,array, columns, runStatus));
            doc.setMarkerSymbol(getValueFor(Allele2DTO.MARKER_SYMBOL, array, columns, runStatus));
            doc.setMgiAccessionId(getValueFor(Allele2DTO.MGI_ACCESSION_ID, array, columns, runStatus));
            doc.setMarkerName(getValueFor(Allele2DTO.MARKER_NAME,array, columns, runStatus));
            doc.setMouseAvailable(getBooleanValueFor(Allele2DTO.MOUSE_AVAILABLE, array, columns, runStatus));
            doc.setMutationType(getValueFor(Allele2DTO.MUTATION_TYPE,array,columns, runStatus));
            doc.setMouseStatus(getValueFor(Allele2DTO.MOUSE_STATUS, array,columns, runStatus));
            doc.setPhenotypeStatus(getValueFor(Allele2DTO.PHENOTYPE_STATUS,array, columns, runStatus));
            doc.setPhenotypingCentre(getValueFor(Allele2DTO.PHENOTYPING_CENTRE,array, columns, runStatus));
            doc.setPhenotypingCentres(getListValueFor(Allele2DTO.PHENOTYPING_CENTRES,array, columns, runStatus));
            doc.setProductionCentre(getValueFor(Allele2DTO.PRODUCTION_CENTRE,array, columns, runStatus));
            doc.setProductionCentres(getListValueFor(Allele2DTO.PRODUCTION_CENTRES,array, columns, runStatus));
            doc.setPipeline(getListValueFor(Allele2DTO.PIPELINE,array, columns, runStatus));
            doc.setSequenceMapLinks(getListValueFor(Allele2DTO.SEQUENCE_MAP_LINKS, array, columns, runStatus));
            doc.setSynonym(getListValueFor(Allele2DTO.SYNONYM, array, columns, runStatus));
            doc.setTargetingVectorAvailable(getBooleanValueFor(Allele2DTO.TARGETING_VECTOR_AVAILABLE, array, columns, runStatus));
            doc.setType(getValueFor(Allele2DTO.TYPE, array, columns, runStatus));
            doc.setVectorAlleleImage(getValueFor(Allele2DTO.VECTOR_ALLELE_IMAGE, array, columns, runStatus));
            doc.setVectorGenbankLink(getValueFor(Allele2DTO.VECTOR_GENBANK_LINK, array, columns, runStatus));
            doc.setWithoutAlleleFeatures(getListValueFor(Allele2DTO.WITHOUT_ALLELE_FEATURES, array, columns, runStatus));

            line = in.readLine();

            allele2Indexing.addBean(doc, 30000);

        }

        allele2Indexing.commit();
        alleleDocCount = index;
        System.out.println("Indexing took " + (System.currentTimeMillis() - time));
        logger.info("Added {} documents", alleleDocCount);
        return runStatus;

    }


    @Override
    public RunStatus validateBuild() throws IndexerException {

        RunStatus runStatus = new RunStatus();
        Long actualSolrDocumentCount = getDocumentCount(allele2Indexing);

        if (actualSolrDocumentCount < alleleDocCount) {
           runStatus.addError("Expected " + alleleDocCount + " documents. Actual count: " + actualSolrDocumentCount + ".");
        }

        return runStatus;
    }


    public static void main(String[] args) throws IndexerException {
        SpringApplication.run(Allele2Indexer.class, args);
    }

}
