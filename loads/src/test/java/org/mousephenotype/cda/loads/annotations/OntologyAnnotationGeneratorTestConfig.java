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

import org.hibernate.SessionFactory;
import org.mousephenotype.cda.db.dao.OntologyTermDAO;
import org.mousephenotype.cda.db.dao.OntologyTermDAOImpl;
import org.mousephenotype.cda.db.dao.PhenotypePipelineDAO;
import org.mousephenotype.cda.db.dao.PhenotypePipelineDAOImpl;
import org.mousephenotype.cda.db.statistics.MpTermService;
import org.mousephenotype.cda.db.utilities.SqlUtils;
import org.mousephenotype.cda.loads.common.CdaSqlUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.orm.hibernate5.LocalSessionFactoryBuilder;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

@Configuration
public class OntologyAnnotationGeneratorTestConfig {


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
        return new MpTermService(ontologyTermDAO(), pipelineDAO());
    }


    ///////
    // DAOs
    ///////

    @Bean
    public OntologyTermDAO ontologyTermDAO() {
        return new OntologyTermDAOImpl(sessionFactory());
    }

    @Bean
    public PhenotypePipelineDAO pipelineDAO() {
        return new PhenotypePipelineDAOImpl(sessionFactory());
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

    @Bean(name = "sessionFactoryHibernate")
    public SessionFactory sessionFactory() {

        LocalSessionFactoryBuilder sessionBuilder = new LocalSessionFactoryBuilder(komp2DataSource());
        sessionBuilder.scanPackages("org.mousephenotype.cda.db.entity");
        sessionBuilder.scanPackages("org.mousephenotype.cda.db.pojo");

        return sessionBuilder.buildSessionFactory();
    }

    @Bean(name = "komp2TxManager")
    @Primary
    protected PlatformTransactionManager transactionManager(EntityManagerFactory emf) {
        JpaTransactionManager tm = new JpaTransactionManager();
        tm.setEntityManagerFactory(emf);
        tm.setDataSource(komp2DataSource());
        return tm;
    }
}