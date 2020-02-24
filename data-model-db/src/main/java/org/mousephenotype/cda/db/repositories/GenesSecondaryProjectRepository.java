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

package org.mousephenotype.cda.db.repositories;

import org.mousephenotype.cda.db.pojo.GenesSecondaryProject;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.repository.CrudRepository;

import java.util.Set;

@ComponentScan(basePackages = "org.mousephenotype.cda.db.utilities")
public interface GenesSecondaryProjectRepository extends CrudRepository<GenesSecondaryProject, Long> {

    @Cacheable("secondaryProjects")
    Set<GenesSecondaryProject> getAllBySecondaryProjectId(String secondaryProjectId);

    @Cacheable("secondaryProjects")
    Set<GenesSecondaryProject> getAllBySecondaryProjectIdAndGroupLabel(String secondaryProjectId, String groupLabel);
}