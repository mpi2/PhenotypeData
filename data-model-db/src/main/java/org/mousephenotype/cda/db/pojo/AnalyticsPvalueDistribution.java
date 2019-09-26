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

package org.mousephenotype.cda.db.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "analytics_pvalue_distribution")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AnalyticsPvalueDistribution {

    @Id
    private Long    id;

    private String  datatype;

    @Column(name = "statistical_method")
    private String  statisticalMethod;

    @Column(name = "pvalue_bin")
    private Double  pvalueBin;

    @Column(name = "interval_scale")
    private Double  intervalScale;

    @Column(name = "pvalue_count")
    private Integer pvalueCount;
}