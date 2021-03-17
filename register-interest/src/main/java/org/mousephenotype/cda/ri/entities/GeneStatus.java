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

package org.mousephenotype.cda.ri.entities;

import java.util.Date;

/**
 * Created by mrelac on 12/05/2017.
 * For v2: This entity is not needed, as gene data now comes from the gene core.
 */
@Deprecated
public class GeneStatus {
    private int pk;
    private int genePk;
    private String status;
    private Date createdAt;
    private Date updatedAt;

    public static final String MORE_PHENOTYPING_DATA_AVAILABLE      = "More phenotyping data available";
    public static final String MOUSE_PRODUCED                       = "Genotype confirmed mice";
    public static final String MOUSE_PRODUCTION_STARTED             = "Started";
    public static final String NOT_PLANNED                          = "Not planned";
    public static final String PHENOTYPING_DATA_AVAILABLE           = "Phenotyping data available";
    public static final String PRODUCTION_AND_PHENOTYPING_PLANNED   = "Selected for production and phenotyping";
    public static final String REGISTER                             = "register";
    public static final String UNREGISTER                           = "unregister";
    public static final String WITHDRAWN                            = "Withdrawn";

    public int getPk() {
        return pk;
    }

    public void setPk(int pk) {
        this.pk = pk;
    }

    public int getGenePk() {
        return genePk;
    }

    public void setGenePk(int genePk) {
        this.genePk = genePk;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}
