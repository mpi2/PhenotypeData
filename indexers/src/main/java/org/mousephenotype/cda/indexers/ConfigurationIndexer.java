package org.mousephenotype.cda.indexers;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.util.Assert;

import javax.inject.Inject;
import javax.inject.Named;
import javax.sql.DataSource;

/**
 * Core to contain the configuration properties for a web app instance
 *
 * This Indexer will index the relevant documents from various sources (i.e. metadata from DB, counts from imits core, etc.)
 *
 */
@EnableAutoConfiguration
public class ConfigurationIndexer {

    DataSource komp2DataSource;

    @Inject
    @Named("komp2DataSource")
    public ConfigurationIndexer(DataSource dataSource) {
        Assert.notNull(dataSource, "Datasource must not be null");
        this.komp2DataSource = dataSource;
    }

}
