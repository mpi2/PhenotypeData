/*******************************************************************************
 * Copyright © 2019 EMBL - European Bioinformatics Institute
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

package org.mousephenotype.cda.datatests.repositories;

import org.mousephenotype.cda.db.HibernateConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.solr.repository.config.EnableSolrRepositories;

@Configuration
@EnableJpaRepositories(basePackages = {"org.mousephenotype.cda.db.repositories"})
@EnableSolrRepositories(basePackages = {"org.mousephenotype.cda.solr.repositories"})
@Import(HibernateConfig.class)
public class RepositoryTestConfig {

}