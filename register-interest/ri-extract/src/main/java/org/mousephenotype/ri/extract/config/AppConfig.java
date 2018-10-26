/*******************************************************************************
 *  Copyright © 2017 EMBL - European Bioinformatics Institute
 *
 *  Licensed under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License. You may obtain a copy of the License at
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 *  either express or implied. See the License for the specific
 *  language governing permissions and limitations under the
 *  License.
 ******************************************************************************/

package org.mousephenotype.ri.extract.config;

import org.mousephenotype.cda.ri.core.entities.Gene;
import org.mousephenotype.cda.ri.core.entities.ImitsStatus;
import org.mousephenotype.cda.ri.core.exceptions.InterestException;
import org.mousephenotype.cda.ri.core.utils.SqlUtils;
import org.mousephenotype.ri.extract.Downloader;
import org.mousephenotype.ri.extract.GeneLoader;
import org.mousephenotype.ri.extract.GeneProcessor;
import org.mousephenotype.ri.extract.GeneWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.data.neo4j.Neo4jDataAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.jms.JndiConnectionFactoryAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.autoconfigure.web.EmbeddedServletContainerAutoConfiguration;
import org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.sql.DataSource;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by mrelac on 02/05/2017.
 */
@Configuration
@PropertySource("file:${user.home}/configfiles/${profile}/application.properties")
@EnableBatchProcessing
@EnableAutoConfiguration(exclude = {
        JndiConnectionFactoryAutoConfiguration.class,
        DataSourceAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class,
        JpaRepositoriesAutoConfiguration.class,
        DataSourceTransactionManagerAutoConfiguration.class,
        Neo4jDataAutoConfiguration.class,
        WebMvcAutoConfiguration.class,
        EmbeddedServletContainerAutoConfiguration.class
})
public class AppConfig {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private StepBuilderFactory stepBuilderFactory;

    @NotNull
    @Value("${GeneStatusChangeUrl}")
    protected String geneStatusChangeUrl;

    @NotNull
    @Value("${download.workspace}")
    protected String downloadWorkspace;


    private enum DownloadFileEnum {
        GENE_STATUS_CHANGE
    }

    public DownloadFilename[] filenames;


    @PostConstruct
    public void initialise() {
        filenames = new DownloadFilename[] {
                new DownloadFilename(DownloadFileEnum.GENE_STATUS_CHANGE, geneStatusChangeUrl, downloadWorkspace + "/GeneStatusChange.tsv")
        };
    }


    @Lazy
    @Inject
    public AppConfig(StepBuilderFactory stepBuilderFactory) {
        this.stepBuilderFactory = stepBuilderFactory;
    }


    public class DownloadFilename {
        public final DownloadFileEnum downloadFileEnum;
        public String sourceUrl;
        public final String targetFilename;

        public DownloadFilename(DownloadFileEnum downloadFileEnum, String sourceUrl, String targetFilename) {
            this.downloadFileEnum = downloadFileEnum;
            this.sourceUrl = sourceUrl;
            this.targetFilename = targetFilename;
        }
    }


    @Bean
    public NamedParameterJdbcTemplate jdbc() {
        return new NamedParameterJdbcTemplate(riDataSource());
    }

    @Bean
    public SqlUtils sqlUtils() {
        return new SqlUtils(jdbc());
    }

    @NotNull
    @Value("${datasource.ri.url}")
    String riUrl;

    @NotNull
    @Value("${datasource.ri.username}")
    String username;

    @NotNull
    @Value("${datasource.ri.password}")
    String password;



    @Bean
    public DataSource riDataSource() {
        return SqlUtils.getConfiguredDatasource(riUrl, username, password);
    }

    @Bean
    public List<Downloader> downloader() {
        List<Downloader> downloaderList = new ArrayList<>();

        for (DownloadFilename download : filenames) {
            downloaderList.add(new Downloader(download.sourceUrl, download.targetFilename));
        }

        return downloaderList;
    }


    // LOADERS, PROCESSORS, AND WRITERS


    @Bean(name = "geneLoader")
    public GeneLoader geneLoader() throws InterestException {
            return new GeneLoader(
                    geneProcessor(), stepBuilderFactory, geneWriter());
    }


    @Bean(name = "geneProcessor")
    public GeneProcessor geneProcessor() throws InterestException {

        Map<Integer, Gene>        genesByPk = sqlUtils().getGenesByPk();
        Map<String, ImitsStatus> imitsStatusByStatus = sqlUtils().getImitsStatusByStatus();

        return new GeneProcessor(imitsStatusByStatus, genesByPk);
    }

    @Bean(name = "geneWriter")
    public GeneWriter geneWriter() {
        return new GeneWriter();
    }
}