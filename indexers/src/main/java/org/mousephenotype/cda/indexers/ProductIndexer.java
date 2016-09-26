package org.mousephenotype.cda.indexers;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.mousephenotype.cda.indexers.exceptions.IndexerException;
import org.mousephenotype.cda.solr.service.dto.ProductDTO;
import org.mousephenotype.cda.utilities.RunStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ilinca on 26/09/2016.
 */
public class ProductIndexer  extends AbstractIndexer implements CommandLineRunner {



    @Autowired
    @Qualifier("productCore")
    private SolrClient productCore;


    //TODO REPLACE
    String pathToProductFile = "/Users/ilinca/Documents/temp/product-2016-09-23_15-32-40.tsv";
    Map<String, Integer> columns = new HashMap<>();
    Integer productDocCount;

    @Override
    public RunStatus run() throws IndexerException, IOException, SolrServerException, SQLException {

        productCore.deleteByQuery("*:*");
        productCore.commit();

        long time = System.currentTimeMillis();

        RunStatus runStatus = new RunStatus();
        BufferedReader in = new BufferedReader(new FileReader(new File(pathToProductFile)));
        String[] header = in.readLine().split("\t");
        for (int i = 0; i < header.length; i++){
            columns.put(header[i], i);
        }

        int index = 0 ;

        String line = in.readLine();
        while (line != null){

            String[] array = line.split("\t", -1);
            index ++;
            ProductDTO doc = new ProductDTO();

            //doc.setAlleleHasIssues();
            doc.setAlleleId(getValueFor(ProductDTO.ALLELE_ID, array, columns, runStatus));

            doc.setAlleleName(getValueFor(ProductDTO.ALLELE_NAME, array, columns, runStatus));
            doc.setAlleleType(getValueFor(ProductDTO.ALLELE_TYPE, array, columns, runStatus));
            doc.setAssociatedProductColonyName(getValueFor(ProductDTO.ASSOCIATED_PRODUCT_COLONY_NAME, array, columns, runStatus));
            doc.setAssociatedProductColonyNames(getListValueFor(ProductDTO.ASSOCIATED_PRODUCT_COLONY_NAMES, array, columns, runStatus));
            doc.setAssociatedProductEsCellName(getValueFor(ProductDTO.ASSOCIATED_PRODUCT_ES_CELL_NAME, array, columns, runStatus));
            doc.setAssociatedProductEsCellNames(getListValueFor(ProductDTO.ASSOCIATED_PRODUCTS_ES_CELL_NAMES, array, columns, runStatus));
            doc.setAssociatedProductVectorName(getValueFor(ProductDTO.ASSOCIATED_PRODUCT_VECTOR_NAME, array, columns, runStatus));
            doc.setCassette(getValueFor(ProductDTO.CASSETTE, array, columns, runStatus));
            doc.setContactLinks(getListValueFor(ProductDTO.CONTACT_LINKS, array, columns, runStatus));
            doc.setContractNames(getListValueFor(ProductDTO.CONTRACT_NAMES, array, columns, runStatus));
            doc.setDesignId(getValueFor(ProductDTO.DESIGN_ID, array, columns, runStatus));
            doc.setGeneticInfo(getListValueFor(ProductDTO.GENETIC_INFO, array, columns, runStatus));
            doc.setIkmcProjectId(getValueFor(ProductDTO.IKMC_PROJECT_ID, array, columns, runStatus));
            doc.setLaoAssays(getListValueFor(ProductDTO.LOA_ASSAYS, array, columns, runStatus));
            doc.setMarkerSymbol(getValueFor(ProductDTO.MARKER_SYMBOL, array, columns, runStatus));
            doc.setMgiAccessionId(getValueFor(ProductDTO.MGI_ACCESSION_ID, array, columns, runStatus));
            doc.setName(getValueFor(ProductDTO.NAME, array, columns, runStatus));
            doc.setOrderLinks(getListValueFor(ProductDTO.ORDER_LINKS, array, columns, runStatus));
            doc.setOrderNames(getListValueFor(ProductDTO.ORDER_NAMES, array, columns, runStatus));
            doc.setOtherLinks(getListValueFor(ProductDTO.OTHER_LINKS, array, columns, runStatus));
            doc.setProductId(getValueFor(ProductDTO.PRODUCT_ID, array, columns, runStatus));
            doc.setProductionCentre(getValueFor(ProductDTO.PRODUCTION_CENTRE, array, columns, runStatus));
            doc.setProductionCompleted(getBooleanValueFor(ProductDTO.PRODUCTION_COMPLETED, array, columns, runStatus));
            doc.setProductionInfo(getListValueFor(ProductDTO.PRODUCTION_INFO, array, columns, runStatus));
            doc.setProductionPipeline(getValueFor(ProductDTO.PRODUCTION_PIPELINE, array, columns, runStatus));
            doc.setQcData(getListValueFor(ProductDTO.QC_DATA, array, columns, runStatus));
            doc.setStatus(getValueFor(ProductDTO.STATUS, array, columns, runStatus));
            doc.setStatusDate(getValueFor(ProductDTO.STATUS_DATE, array, columns, runStatus));
            doc.setType(getValueFor(ProductDTO.TYPE, array, columns, runStatus));

            line = in.readLine();

            productCore.addBean(doc);
            if (index % 1000 == 0) {
                productCore.commit();
                System.out.println("Commited " + index);
            }

        }

        productCore.commit();
        productDocCount = index;
        System.out.println("Indexing took " + (System.currentTimeMillis() - time));
        return null;
    }

    @Override
    public RunStatus validateBuild() throws IndexerException {

        RunStatus runStatus = new RunStatus();
        Long actualSolrDocumentCount = getDocumentCount(productCore);

        if (actualSolrDocumentCount <= productDocCount) {
            runStatus.addError("Expected " + productDocCount + " documents. Actual count: " + actualSolrDocumentCount + ".");
        }

        return runStatus;
    }

    public static void main(String[] args) throws IndexerException {
        SpringApplication.run(Allele2Indexer.class, args);
    }


}
