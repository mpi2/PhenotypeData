package org.mousephenotype.cda.solr.service;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.mousephenotype.cda.solr.service.dto.ImageDTO;
import org.mousephenotype.cda.solr.service.dto.ObservationDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;

@Service
public class ExpressionServiceLacz {

    private static final Logger log = LoggerFactory.getLogger(ExpressionServiceLacz.class);

    private final SolrClient experimentCore;

    @Inject
    public ExpressionServiceLacz(
            SolrClient experimentCore)
    {
        super();
        this.experimentCore = experimentCore;
    }

    private Integer getNumDocs(SolrClient core, SolrQuery query) throws SolrServerException, IOException {
        query.setRows(0);
        QueryResponse response = core.query(query);
        return ((Long)response.getResults().getNumFound()).intValue();

    }

    /**
     * DO NOT MOVE THIS METHOD TO ExpressionService CLASS. IT WILL BREAK SPRING CACHING
     *
     * @param mgiAccession if mgi accession null assume a request for control data
     */
    @Cacheable("categoricalLaczDataDocs")
    public SolrDocumentList getCategoricalAdultLacZDocuments(String mgiAccession, boolean embryo, String... fields)
            throws SolrServerException, IOException {

        // e.g.
        // http://wp-np2-e2.ebi.ac.uk:8986/solr/experiment/select?q=biological_sample_group:%22control%22&fq=procedure_name:%22Adult+LacZ%22&fq=-parameter_name:%22LacZ+Images+Section%22&fq=-parameter_name:%22LacZ+Images+Wholemount%22&fq=observation_type:%22categorical%22&fl=zygosity,external_sample_id,observation_type,parameter_name,category,biological_sample_group&rows=50000&sort=id+asc
        SolrQuery solrQuery = new SolrQuery();

        List<String> baseQueryParameters = new ArrayList<>();
        baseQueryParameters.addAll(Arrays.asList(ObservationDTO.OBSERVATION_TYPE + ":\"categorical\"",
                "-" + ImageDTO.PARAMETER_NAME + ":\"LacZ Images Section\"",
                "-" + ImageDTO.PARAMETER_NAME + ":\"LacZ Images Wholemount\""));

        if (mgiAccession != null) {
            baseQueryParameters.add(ImageDTO.GENE_ACCESSION_ID + ":\"" + mgiAccession + "\"");
        } else {
            baseQueryParameters.add(ObservationDTO.BIOLOGICAL_SAMPLE_GROUP + ":\"" + "control" + "\"");
        }

        if (embryo) {
            baseQueryParameters.add(ImageDTO.PROCEDURE_NAME + ":\"Embryo LacZ\"");
        } else {
            baseQueryParameters.add(ImageDTO.PROCEDURE_NAME + ":\"Adult LacZ\"");
        }

        solrQuery.setQuery(String.join(" AND ", baseQueryParameters));

        solrQuery.setFields(fields);
        solrQuery.setSort(ObservationDTO.ID, SolrQuery.ORDER.asc);

        int batchSize = 1000;
        int numDocs = getNumDocs(experimentCore, solrQuery.getCopy());

        solrQuery.setRows(numDocs);

        SolrDocumentList docs = new SolrDocumentList();

        if (numDocs < batchSize) {
            // do single query
            docs.addAll(experimentCore.query(solrQuery).getResults());
        } else {
            // multiple queries required
            // Parallelize the queries for performance
            ExecutorService executor = Executors.newFixedThreadPool(10);
            List<Callable<SolrDocumentList>> queries = new ArrayList<>();

            // Select batchSize documents per query
            solrQuery.setRows(batchSize);

            int currentDoc = 0;
            while (currentDoc < numDocs) {
                SolrQuery batchedQuery = solrQuery.getCopy();
                batchedQuery.setStart(currentDoc);
                final int iteration = currentDoc /batchSize;
                Callable<SolrDocumentList> callableTask = () -> {
                    log.debug("Solr query "+ iteration +" to get expression results (experiment core): " + batchedQuery);
                    final long start = System.currentTimeMillis();
                    final SolrDocumentList results = experimentCore.query(batchedQuery).getResults();
                    log.debug("  Query took (ms): " + (System.currentTimeMillis() - start));
                    return results;
                };
                queries.add(callableTask);
                currentDoc += batchSize;
            }
            try {
                List<Future<SolrDocumentList>> documentLists = executor.invokeAll(queries);
                documentLists.forEach(x -> {
                    try {
                        docs.addAll(x.get());
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                executor.shutdown();
            }
        }

        return docs;
    }


}
