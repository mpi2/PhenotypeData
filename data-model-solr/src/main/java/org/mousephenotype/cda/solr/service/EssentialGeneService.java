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
}
