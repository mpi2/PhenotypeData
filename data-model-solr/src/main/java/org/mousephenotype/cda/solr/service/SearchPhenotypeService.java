package org.mousephenotype.cda.solr.service;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.PivotField;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.util.NamedList;
import org.mousephenotype.cda.solr.service.dto.GenotypePhenotypeDTO;
import org.mousephenotype.cda.solr.service.dto.MpDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Search for phenotypes in the MP core and return minimal information needed for search results
 *
 * @author jwarren
 */

@Service
public class SearchPhenotypeService {

    @Autowired
    @Qualifier("mpCore")
    private SolrClient solr;

    @Autowired
    @Qualifier("genotypePhenotypeCore")
    private SolrClient gpSolr;

    /**
     * Return all phenotypes from the MP core filtered by keyword.
     *
     * @param keywords Filter the results to documents containing these keywords
     * @param rows     the number of results to return
     * @param start    the document number to begin
     * @return matching phenotypes from the MP core
     */
    public QueryResponse searchPhenotypes(String keywords, Integer start, Integer rows) throws SolrServerException, IOException {

        //current query used by BZ is just taken from old one which has DisMax and boost in the URL (boosts could be in solr config as defaults??)
        //https://wwwdev.ebi.ac.uk/mi/impc/dev/solr/mp/select?facet.field=top_level_mp_term_inclusive&fl=mp_id,mp_term,mixSynQf,mp_definition&fq=+*:*&rows=10&bq=mp_term:("abnormal")^1000+mp_term_synonym:("abnormal")^500+mp_definition:("abnormal")^100&q="abnormal"&facet.limit=-1&defType=edismax&qf=mixSynQf&wt=json&facet=on&facet.sort=index&indent=true&start=0

        final SolrQuery query = new SolrQuery("\"" + keywords + "\"");
        query.add("defType", "edismax");
        query.setFields(MpDTO.MP_ID, MpDTO.MP_TERM, MpDTO.MP_TERM_SYNONYM, MpDTO.MP_DEFINITION);

        //boost looks like this bq=mp_term:("abnormal")^1000+mp_term_synonym:("abnormal")^500+mp_definition:("abnormal")^100&
        query.add("bq", "mp_term:(\"" + keywords + "\")^1000");
        query.add("bq", "mp_term_synonym:(\"" + keywords + "\")^500");
        query.add("bq", "mp_definition:(\"" + keywords + "\")^100");
        query.add("qf", "mixSynQf");

        query.setStart(start);
        query.setRows(rows);

        System.out.println("phenotype search query=" + query);
        return solr.query(query);
    }

    public QueryResponse searchPhenotypeSuggestions(String keyword, Integer distance) throws IOException, SolrServerException {

        String search = keyword.isEmpty() ? "*:*" : keyword.replaceAll("\\w", "") + "~" + distance;

        final SolrQuery query = new SolrQuery(search);
        query.add("defType", "edismax");
        query.setFields(MpDTO.MP_TERM);
        query.add("qf", "mixSynQf");
        query.setStart(0);
        query.setRows(3);
        query.setSort("score", SolrQuery.ORDER.desc);

        System.out.println("phenotype suggestion query=" + query);
        return solr.query(query);
    }

    @Cacheable("genePhenotypeMap")
    public Map<String, Integer> getGenesByPhenotype() throws SolrServerException, IOException {
        final Map<String, Set<String>> genesByPhenotype = executeGenesByPhenotypeQuery(GenotypePhenotypeDTO.MP_TERM_NAME+","+GenotypePhenotypeDTO.MARKER_ACCESSION_ID);
        final Map<String, Set<String>> stringSetMapInt = executeGenesByPhenotypeQuery(GenotypePhenotypeDTO.INTERMEDIATE_MP_TERM_NAME + "," + GenotypePhenotypeDTO.MARKER_ACCESSION_ID);
        final Map<String, Set<String>> stringSetMapTop = executeGenesByPhenotypeQuery(GenotypePhenotypeDTO.TOP_LEVEL_MP_TERM_NAME + "," + GenotypePhenotypeDTO.MARKER_ACCESSION_ID);

        Map<String, Set<String>> combined = Stream.of(genesByPhenotype, stringSetMapInt, stringSetMapTop)
                .flatMap(map -> map.entrySet().stream())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (set1, set2) -> Stream.of(set1, set2)
                                .flatMap(Collection::stream)
                                .collect(Collectors.toSet())));

        return combined.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e->e.getValue().size()));
    }

    private Map<String, Set<String>> executeGenesByPhenotypeQuery(String pivotFields) throws IOException, SolrServerException {

        Map<String, Set<String>> map = new HashMap<>();

        final SolrQuery queryTemplate = new SolrQuery("*:*")
                .setRows(0)
                .setFacet(true)
                .setFacetLimit(-1)
                .setFacetMinCount(1)
                .setFacetLimit(-1)
                .setFacetMinCount(1);

        SolrQuery query = queryTemplate
                .addFacetPivotField(pivotFields);

        System.out.println("gene count search query= " + query);
        final QueryResponse response = gpSolr.query(query);

        // Unwrap the results into a Map
        final NamedList<List<PivotField>> facetPivots = response.getFacetPivot();
        for (Map.Entry<String, List<PivotField>> pivotFacet : facetPivots) {
            for(PivotField phenotypePivotFacet : pivotFacet.getValue()) {
                String mpTermName = phenotypePivotFacet.getValue().toString();
                map.putIfAbsent(mpTermName, new HashSet<>());
                for(PivotField genePivotFacet : phenotypePivotFacet.getPivot()) {
                    String geneAccessionId = genePivotFacet.getValue().toString();
                    map.get(mpTermName).add(geneAccessionId);
                }
            }
        }

        return map;
    }

}
