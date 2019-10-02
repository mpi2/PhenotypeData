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
@Table(name = "analytics_significant_calls_procedures")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AnalyticsSignificantCallsProcedures {

    @Id
    private Long    id;

    @Column(name = "significant_calls")
    private long significantCalls;

    @Column(name = "phenotyping_center")
    private String  phenotypingCenter;

    @Column(name = "procedure_stable_id")
    private String  procedureStableId;

    @Column(name = "procedure_name")
    private String procedureName;
}