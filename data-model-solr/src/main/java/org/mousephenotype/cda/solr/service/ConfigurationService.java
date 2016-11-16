package org.mousephenotype.cda.solr.service;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.springframework.util.Assert;

import javax.inject.Inject;
import javax.inject.Named;


/**
 * Created by jmason on 05/08/2016.
 */
public class ConfigurationService {

    private SolrClient server;

    @Inject
    @Named("configurationCore")
    public ConfigurationService(HttpSolrClient server) {
        Assert.notNull(server, "HttpServer must not be null");
        this.server = server;
    }


}
