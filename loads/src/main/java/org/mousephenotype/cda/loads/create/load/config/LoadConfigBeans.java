/*******************************************************************************
 * Copyright © 2015 EMBL - European Bioinformatics Institute
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

package org.mousephenotype.cda.loads.create.load.config;

import org.mousephenotype.cda.loads.common.CdaSqlUtils;
import org.mousephenotype.cda.loads.common.DataSourcesConfigApp;
import org.mousephenotype.cda.loads.common.DccSqlUtils;
import org.mousephenotype.cda.loads.create.load.steps.ImpressUpdater;
import org.mousephenotype.cda.loads.create.load.steps.SampleLoader;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mrelac on 03/09/16.
 */
@Configuration
@Import(DataSourcesConfigApp.class)
public class LoadConfigBeans {

    private int externalIdDbId;

    @Autowired
    private NamedParameterJdbcTemplate jdbcCda;

    @Autowired
    private NamedParameterJdbcTemplate jdbcDcc;

    @Autowired
    private NamedParameterJdbcTemplate jdbcDccEurophenome;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;
// // FIXME: 04/11/16
//    @Bean
//    public List<SampleLoader> sampleLoaders() {
//        List<SampleLoader>  sampleLoaders = new ArrayList<>();
//
////        String externalDbShortName = "EuroPhenome";
////        String externalDbShortName = "3i";
//        String externalDbShortName = "IMPC";
//
//        SampleLoader sampleLoader = new SampleLoader(jdbcCda, stepBuilderFactory, cdaSqlUtils(), dccSqlUtils(), externalDbShortName);
//
//        return sampleLoader;
//    }

    @Bean
    public List<SampleLoader> sampleLoaders() {
        List<SampleLoader> sampleLoaders = new ArrayList<>();

        sampleLoaders.add(new SampleLoader(jdbcCda, stepBuilderFactory, cdaSqlUtils(), dccSqlUtils()));
        sampleLoaders.add(new SampleLoader(jdbcCda, stepBuilderFactory, cdaSqlUtils(), dccEurophenomeSqlUtils()));

        return sampleLoaders;
    }

    @Bean
    public ImpressUpdater impressUpdater() {

        ImpressUpdater impressUpdater = new ImpressUpdater(jdbcCda, stepBuilderFactory, cdaSqlUtils());

        return impressUpdater;
    }

    @Bean
    public CdaSqlUtils cdaSqlUtils() {
        return new CdaSqlUtils(jdbcCda);
    }

    @Bean
    public DccSqlUtils dccSqlUtils() {
        return new DccSqlUtils(jdbcDcc);
    }

    @Bean
    public DccSqlUtils dccEurophenomeSqlUtils() {
        return new DccSqlUtils(jdbcDccEurophenome);
    }
}