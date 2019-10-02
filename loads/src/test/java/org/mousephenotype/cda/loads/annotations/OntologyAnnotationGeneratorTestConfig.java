/*******************************************************************************
 * Copyright © 2018 EMBL - European Bioinformatics Institute
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 ******************************************************************************/

package org.mousephenotype.cda.loads.annotations;

import org.mousephenotype.cda.db.repositories.OntologyTermRepository;
import org.mousephenotype.cda.db.repositories.ParameterRepository;
import org.mousephenotype.cda.db.statistics.MpTermService;
import org.mousephenotype.cda.db.utilities.SqlUtils;
import org.mousephenotype.cda.loads.common.CdaSqlUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.inject.Inject;
import javax.sql.DataSource;
import javax.validation.constraints.NotNull;

@Configuration
@EnableJpaRepositories(basePackages = {"org.mousephenotype.cda.db.repositories"})
public class OntologyAnnotationGeneratorTestConfig {

    private OntologyTermRepository ontologyTermRepository;
    private ParameterRepository    parameterRepository;


    @Inject
    public OntologyAnnotationGeneratorTestConfig(
            @NotNull OntologyTermRepository ontologyTermRepository,
            @NotNull ParameterRepository parameterRepository)
    {
        this.ontologyTermRepository = ontologyTermRepository;
        this.parameterRepository = parameterRepository;
    }

    //////////////
    // DATASOURCES
    //////////////

    @Value("${datasource.komp2.jdbc-url}")
    protected String cdabaseUrl;

    @Value("${datasource.komp2.username}")
    protected String cdabaseUsername;

    @Value("${datasource.komp2.password}")
    protected String cdabasePassword;

    @Bean
    public DataSource komp2DataSource() {
        return SqlUtils.getConfiguredDatasource(cdabaseUrl, cdabaseUsername, cdabasePassword);
    }


    ///////////
    // SERVICES
    ///////////

    @Bean
    public MpTermService mpTermService() {
        return new MpTermService(ontologyTermRepository, parameterRepository);
    }


    ////////////////
    // Miscellaneous
    ////////////////

    @Bean
    public CdaSqlUtils cdaSqlUtils() {
        return new CdaSqlUtils(jdbcCda());
    }

    @Bean
    public NamedParameterJdbcTemplate jdbcCda() {
        return new NamedParameterJdbcTemplate(komp2DataSource());
    }

//    @Bean(name = "sessionFactoryHibernate")
//    public SessionFactory sessionFactory() {
//
//        LocalSessionFactoryBuilder sessionBuilder = new LocalSessionFactoryBuilder(komp2DataSource());
//        sessionBuilder.scanPackages("org.mousephenotype.cda.db.entity");
//        sessionBuilder.scanPackages("org.mousephenotype.cda.db.pojo");
//
//        return sessionBuilder.buildSessionFactory();
//    }
//
//    @Bean(name = "komp2TxManager")
//    @Primary
//    protected PlatformTransactionManager transactionManager(EntityManagerFactory emf) {
//        JpaTransactionManager tm = new JpaTransactionManager();
//        tm.setEntityManagerFactory(emf);
//        tm.setDataSource(komp2DataSource());
//        return tm;
//    }
}