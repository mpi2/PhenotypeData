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

import org.mousephenotype.cda.db.pojo.AnalyticsPvalueDistribution;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface AnalyticsPvalueDistributionRepository extends CrudRepository<AnalyticsPvalueDistribution, Long> {

    String getAllStatisticalMethodsQuery = "SELECT DISTINCT datatype, statistical_method, null AS pvalue_bin, null AS interval_scale, null AS pvalue_count FROM analytics_pvalue_distribution";
    @Query(value = getAllStatisticalMethodsQuery, nativeQuery = true)
    List<AnalyticsPvalueDistribution> getAllStatisticalMethods();

    List<AnalyticsPvalueDistribution> getAllByDatatypeAndStatisticalMethodOrderByPvalueBinAsc(String datatype, String statisticalMethod);
}