package org.mousephenotype.cda.solr.service;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.mousephenotype.cda.solr.service.dto.EssentialGeneDTO;
import org.mousephenotype.cda.solr.service.dto.GeneDTO;
import org.mousephenotype.cda.web.WebStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class EssentialGeneService extends BasicService implements WebStatus {

    //http://ves-ebi-d0.ebi.ac.uk:8986/solr/#/essentialgenes/query
    //http://ves-ebi-d0.ebi.ac.uk:8986/solr/essentialgenes/select?q=*:*

    private static final Logger log = LoggerFactory.getLogger(GeneService.class);
    private SolrClient essentialGeneCore;

    @Inject
    public EssentialGeneService(SolrClient essentialGeneCore)
    {
        super();
        this.essentialGeneCore = essentialGeneCore;
    }

    @Override
    public long getWebStatus() throws Exception {
        return 0;
    }

    @Override
    public String getServiceName() {
        return "essentialgenes";
    }

    public EssentialGeneDTO getGeneByMgiId(String mgiId, String ...fields) throws IOException, SolrServerException {
        SolrQuery solrQuery = new SolrQuery()
                .setQuery(EssentialGeneDTO.MGI_ACCESSION + ":\"" + mgiId + "\"").setRows(1);
        if(fields != null){
            solrQuery.setFields(fields);
        }

        QueryResponse rsp = essentialGeneCore.query(solrQuery);
        long numberFound=rsp.getResults().getNumFound();
        System.out.println("number of essential genes found="+numberFound);
        if (numberFound > 0) {
            return rsp.getBeans(EssentialGeneDTO.class).get(0);
        }
        return null;
    }

    public List<EssentialGeneDTO> getGeneListByMgiId(String mgiId, String ...fields) throws IOException, SolrServerException {
        SolrQuery solrQuery = new SolrQuery()
                .setQuery(EssentialGeneDTO.MGI_ACCESSION + ":\"" + mgiId + "\"");
        if(fields != null){
            solrQuery.setFields(fields);
        }

        QueryResponse rsp = essentialGeneCore.query(solrQuery);
        long numberFound=rsp.getResults().getNumFound();
        System.out.println("number of essential genes found="+numberFound);
        if (numberFound > 0) {
            return rsp.getBeans(EssentialGeneDTO.class);
        }
        return null;
    }

    public List<EssentialGeneDTO> getAllIdgGeneList( String ...fields) throws IOException, SolrServerException {
        List<EssentialGeneDTO>idgGeneDTOS = null;
        SolrQuery solrQuery = new SolrQuery()
                .setQuery("idg_symbol:*").setRows(500000);//hopefully this gets all the idg results which here is 125147 found instead of 235817 entries for all
        if(fields != null){
            solrQuery.setFields(fields);
        }

        QueryResponse rsp = essentialGeneCore.query(solrQuery);
        long numberFound=rsp.getResults().getNumFound();
        System.out.println("number of essential genes found="+numberFound);
        if (numberFound > 0) {
            idgGeneDTOS= rsp.getBeans(EssentialGeneDTO.class);
            List<EssentialGeneDTO> distinctIdgList=idgGeneDTOS.stream().filter(distinctByKey(x -> x.getIdgSymbol())).collect(Collectors.toList());
            log.info("distinctIdgList size"+distinctIdgList.size());
            return distinctIdgList;
        }


        return null;
    }


    public static <T> Predicate<T> distinctByKey(
            Function<? super T, ?> keyExtractor) {

        Map<Object, Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }
}
