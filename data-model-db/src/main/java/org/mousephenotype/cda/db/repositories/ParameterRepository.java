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

import org.mousephenotype.cda.db.pojo.Parameter;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ParameterRepository extends CrudRepository<Parameter, Long> {

    Parameter getFirstByStableId(String stableId);

    List<Parameter> getByStableId(String stableId);

    @Query("SELECT parameter FROM Parameter parameter INNER JOIN parameter.procedures procedures INNER JOIN procedures.pipelines pipelines " +
            "WHERE parameter.stableId = :parameterStableId " +
            "AND procedures.stableId = :procedureStableId " +
            "AND pipelines.stableId = :pipelineStableId")
    Parameter getByStableIdAndProcedureAndPipeline(@Param("parameterStableId") String parameterStableId,
                                                   @Param("procedureStableId") String procedureStableId,
                                                   @Param("pipelineStableId") String pipelineStableId);
}