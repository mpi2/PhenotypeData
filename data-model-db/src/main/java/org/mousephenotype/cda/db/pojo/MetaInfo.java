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
import java.util.Objects;

@Entity
@Table(name = "meta_info")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MetaInfo {

    @Id
    private Long id;

    @Column(name = "property_key")
    private String propertyKey;

    @Column(name = "property_value")
    private String propertyValue;

    private String description;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MetaInfo)) return false;
        MetaInfo metaInfo = (MetaInfo) o;
        return id.equals(metaInfo.id) &&
                propertyKey.equals(metaInfo.propertyKey) &&
                propertyValue.equals(metaInfo.propertyValue) &&
                Objects.equals(description, metaInfo.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, propertyKey, propertyValue, description);
    }
}