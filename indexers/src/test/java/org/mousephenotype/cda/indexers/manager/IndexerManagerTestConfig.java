package org.mousephenotype.cda.indexers.manager;

import org.mousephenotype.cda.indexers.IndexerManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;

@Configuration
public class IndexerManagerTestConfig {

    @Autowired
    private ApplicationContext applicationContext;

    @Inject
    public IndexerManagerTestConfig(@NotNull ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Lazy
    @Bean
    public IndexerManager indexerManager() {
        return new IndexerManager(applicationContext);
    }
}
