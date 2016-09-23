package org.mousephenotype.cda.indexers;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.mousephenotype.cda.indexers.exceptions.IndexerException;
import org.mousephenotype.cda.solr.service.dto.Allele2DTO;
import org.mousephenotype.cda.utilities.RunStatus;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by ilinca on 23/09/2016.
 */

@EnableAutoConfiguration
public class Allele2Indexer  extends AbstractIndexer implements CommandLineRunner {

    //TODO REPLACE
    String pathToAlleleFile = "/Users/ilinca/Documents/temp/allele2-2016-09-23_15-50-37.tsv";
    SolrClient allele2Core = new HttpSolrClient("http://localhost:8086/solr-example/allele");
    Map<String, Integer> columns = new HashMap<>();

    @Override
    public RunStatus run() throws IndexerException, IOException, SolrServerException, SQLException {

        allele2Core.deleteByQuery("*:*");
        allele2Core.commit();

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

            doc.setAlleleCategory(getValueFor(Allele2DTO.ALLELE_CATEGORY, array));
            doc.setAlleleDescription(getValueFor(Allele2DTO.ALLELE_DESCRIPTION,array));
            doc.setAlleleImage(getValueFor(Allele2DTO.ALLELE_IMAGE,array));
            doc.setAlleleMgiAccessionId(getValueFor(Allele2DTO.ALLELE_MGI_ACCESSION_ID,array));
            doc.setAlleleName(getValueFor(Allele2DTO.ALLELE_NAME,array));
            doc.setAlleleSimpleImage(getValueFor(Allele2DTO.ALLELE_SIMPLE_IMAGE,array));
            doc.setAlleleType(getValueFor(Allele2DTO.ALLELE_TYPE,array));
            doc.setAlleleFeatures(getListValueFor(Allele2DTO.ALLELE_FEATURES, array));
            doc.setCassette(getValueFor(Allele2DTO.CASSETTE,array));
            doc.setDesignId(getValueFor(Allele2DTO.DESIGN_ID,array));
            doc.setEsCellStatus(getValueFor(Allele2DTO.ES_CELL_STATUS,array));
            doc.setEsCellAvailable(getBooleanValueFor(Allele2DTO.ES_CELL_AVAILABLE,array));
            doc.setFeatureChromosome(getValueFor(Allele2DTO.FEATURE_CHROMOSOME,array));
            doc.setFeatureStrand(getValueFor(Allele2DTO.FEATURE_STRAND,array));
            doc.setFeatureCoordEnd(getLongValueFor(Allele2DTO.FEATURE_COORD_END,array));
            doc.setFeatureCoordStart(getLongValueFor(Allele2DTO.FEAURE_COORD_START,array));
            doc.setFeatureType(getValueFor(Allele2DTO.FEATURE_TYPE,array));
            doc.setGenbankFile(getValueFor(Allele2DTO.GENBANK_FILE,array));
            doc.setGeneModelIds(getListValueFor(Allele2DTO.GENE_MODEL_IDS,array));
            doc.setGeneticMapLinks(getListValueFor(Allele2DTO.GENETIC_MAP_LINKS,array));
            doc.setIkmcProject(getListValueFor(Allele2DTO.IKMC_PROJECT,array));
            doc.setLatestEsCellStatus(getValueFor(Allele2DTO.LATEST_ES_CELL_STATUS,array));
            doc.setLatestMouseStatus(getValueFor(Allele2DTO.LATEST_MOUSE_STATUS,array));
            doc.setLatestPhenotypeComplete(getValueFor(Allele2DTO.LATEST_PHENOTYPE_COMPLETE,array));
            doc.setLatestPhenotypeStarted(getValueFor(Allele2DTO.LATEST_PHENOTYPE_STARTED,array));
            doc.setLatestProjectStatus(getValueFor(Allele2DTO.LATEST_PROJECT_STATUS,array));
            doc.setLatestProjectStatusLegacy(getValueFor(Allele2DTO.LATEST_PROJECT_STATUS_LEGACY,array));
            doc.setMarkerType(getValueFor(Allele2DTO.MARKER_TYPE,array));
            doc.setMarkerSymbol(getValueFor(Allele2DTO.MARKER_SYMBOL, array));
            doc.setMarkerName(getValueFor(Allele2DTO.MARKER_NAME,array));
            doc.setPhenotypeStatus(getValueFor(Allele2DTO.PHENOTYPE_STATUS,array));
            doc.setPhenotypingCentre(getValueFor(Allele2DTO.PHENOTYPING_CENTRE,array));
            doc.setPhenotypingCentres(getListValueFor(Allele2DTO.PHENOTYPING_CENTRES,array));
            doc.setPipeline(getListValueFor(Allele2DTO.PIPELINE,array));
            doc.setSequenceMapLinks(getListValueFor(Allele2DTO.SEQUENCE_MAP_LINKS, array));
            doc.setSynonym(getListValueFor(Allele2DTO.SYNONYM, array));
            doc.setTargetingVectorAvailable(getBooleanValueFor(Allele2DTO.TARGETING_VECTOR_AVAILABLE, array));
            doc.setType(getValueFor(Allele2DTO.TYPE, array));
            doc.setVectorAlleleImage(getValueFor(Allele2DTO.VECTOR_ALLELE_IMAGE, array));
            doc.setVectorGenbankLink(getValueFor(Allele2DTO.VECTOR_GENBANK_LINK, array));
            doc.setWithoutAlleleFeatures(getListValueFor(Allele2DTO.WITHOUT_ALLELE_FEATURES, array));

            line = in.readLine();

            allele2Core.addBean(doc);
            if (index % 5000 == 0) {
                allele2Core.commit();
            }
        }

        allele2Core.commit();

        System.out.println("Indexing took " + (System.currentTimeMillis() - time));
        return null;
    }


    private String getValueFor (String field, String[] array){

        if (columns.containsKey(field)) {
            String el = array[columns.get(field)];
            if(el.isEmpty()){
                return null;
            } else if (el.equals("\"\"")){
                return "";
            }
            return el;
        } else {
            System.out.println("Field not found " + field);
            return null;
        }
    }

    private Boolean getBooleanValueFor (String field, String[] array){

        if (columns.containsKey(field)) {
            String el = array[columns.get(field)];
            if(el.isEmpty()){
                return null;
            }
            return new Boolean(el);
        } else {
            System.out.println("Field not found " + field);
            return null;
        }
    }

    private List<String> getListValueFor (String field, String[] array){

        List<String> list = new ArrayList<>();

        if (columns.containsKey(field)) {
            String el = array[columns.get(field)];
            if(el.isEmpty()){
                return null;
            }
            return Arrays.asList(el.split("\\|", -1));
        } else {
            System.out.println("Field not found " + field);
            return null;
        }
    }

    private Long getLongValueFor (String field, String[] array){

        if (columns.containsKey(field)) {
            String el = array[columns.get(field)];
            if(el.isEmpty()){
                return null;
            }
            return new Long(el);
        } else {
            System.out.println("Field not found " + field);
            return null;
        }
    }



    @Override
    public RunStatus validateBuild() throws IndexerException {
        return null;
    }


    public static void main(String[] args) throws IndexerException {
        SpringApplication.run(Allele2Indexer.class, args);
    }

}
