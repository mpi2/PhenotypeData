package org.mousephenotype.cda.solr.service;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.mousephenotype.cda.solr.service.dto.EssentialGeneDTO;
import org.mousephenotype.cda.web.WebStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
public class EssentialGeneService extends BasicService implements WebStatus {

    private static final Logger log = LoggerFactory.getLogger(GeneService.class);
    private SolrClient essentialGeneCore;

    @Inject
    public EssentialGeneService(@Named("essentialGeneCore") SolrClient essentialGeneCore) {
        super();
        this.essentialGeneCore = essentialGeneCore;
    }

    public EssentialGeneService() { super();
    }

    @Override
    public long getWebStatus() {
        return 0;
    }

    @Override
    public String getServiceName() {
        return "essentialgenes";
    }

    public EssentialGeneDTO getGeneByMgiId(String mgiId, String... fields) throws IOException, SolrServerException {
        SolrQuery solrQuery = new SolrQuery()
                .setQuery(EssentialGeneDTO.MGI_ACCESSION + ":\"" + mgiId + "\"")
                .setRows(1);
        if (fields != null) {
            solrQuery.setFields(fields);
        }

        QueryResponse rsp = essentialGeneCore.query(solrQuery);
        long numberFound = rsp.getResults().getNumFound();
        log.debug("number of essential genes found = " + numberFound);
        if (numberFound > 0) {
            return rsp.getBeans(EssentialGeneDTO.class).get(0);
        }
        return null;
    }

    public List<EssentialGeneDTO> getGeneListByMgiId(String mgiId, String... fields) throws IOException, SolrServerException {
        SolrQuery solrQuery = new SolrQuery()
                .setQuery(EssentialGeneDTO.MGI_ACCESSION + ":\"" + mgiId + "\"");
        if (fields != null) {
            solrQuery.setFields(fields);
        }

        QueryResponse rsp = essentialGeneCore.query(solrQuery);
        long numberFound = rsp.getResults().getNumFound();
        log.debug("number of essential genes found = " + numberFound);
        if (numberFound > 0) {
            return rsp.getBeans(EssentialGeneDTO.class);
        }
        return null;
    }

//    @Cacheable("allIdgGeneList")
    public List<EssentialGeneDTO> getAllIdgGeneList(String... fields) throws IOException, SolrServerException {
        List<EssentialGeneDTO> idgGeneDTOS;
        SolrQuery solrQuery = new SolrQuery()
                .setQuery("idg_family:Kinase OR idg_family:IonChannel OR idg_family:GPCR")
                .setRows(Integer.MAX_VALUE);
        if (fields != null) {
            solrQuery.setFields(fields);
        }

        QueryResponse rsp = essentialGeneCore.query(solrQuery);
        long numberFound = rsp.getResults().getNumFound();
        log.debug("number of essential genes found = " + numberFound);
        if (numberFound > 0) {
            idgGeneDTOS = rsp.getBeans(EssentialGeneDTO.class);
            List<EssentialGeneDTO> distinctIdgList = idgGeneDTOS.stream()
                    .filter(distinctByKey(EssentialGeneDTO::getHumanGeneSymbol))
                    .collect(Collectors.toList());
            log.debug("distinctIdgList size = " + distinctIdgList.size());
            return distinctIdgList;
        }

        return null;
    }

    public List<EssentialGeneDTO> getAllIdgGeneListByGroupLabel(String groupLabel, String... fields) throws IOException, SolrServerException {

        final List<EssentialGeneDTO> allIdgGeneList = getAllIdgGeneList(fields);

        if (allIdgGeneList == null) return null;

        return allIdgGeneList.stream()
                .filter(x->x.getIdgFamily().equalsIgnoreCase(groupLabel))
                .collect(Collectors.toList());
    }


    public static <T> Predicate<T> distinctByKey(
            Function<? super T, ?> keyExtractor) {

        Map<Object, Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }


}
