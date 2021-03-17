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
 * This entity class maps to the contact_gene table.
 */
public class ContactGene {
    private int pk;
    private int contactPk;
    private int genePk;
    private Date createdAt;
    private Date updatedAt;

    public String buildContactGeneKey() {
        return Integer.toString(contactPk) + "_" + Integer.toString(genePk);
    }

    public static String buildContactGeneKey(int contactPk, int genePk) {
        return Integer.toString(contactPk) + "_" + Integer.toString(genePk);
    }

    public int getPk() {
        return pk;
    }

    public void setPk(int pk) {
        this.pk = pk;
    }

    public int getContactPk() {
        return contactPk;
    }

    public void setContactPk(int contactPk) {
        this.contactPk = contactPk;
    }

    public int getGenePk() {
        return genePk;
    }

    public void setGenePk(int genePk) {
        this.genePk = genePk;
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