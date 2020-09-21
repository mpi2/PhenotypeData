package org.mousephenotype.cda.db.pojo; /*******************************************************************************
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

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "meta_history")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MetaHistory {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long   id;

    @Column(name = "property_key")
    private String propertyKey;

    @Column(name = "property_value")
    private String propertyValue;

    @Column(name = "data_release_version")
    private String dataReleaseVersion;

    public MetaHistory(MetaInfo metaInfo, String dataReleaseVersion) {
        this.propertyKey = metaInfo.getPropertyKey();
        this.propertyValue = metaInfo.getPropertyValue();
        this.dataReleaseVersion = dataReleaseVersion;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MetaHistory that = (MetaHistory) o;
        return id.equals(that.id) &&
                propertyKey.equals(that.propertyKey) &&
                propertyValue.equals(that.propertyValue) &&
                dataReleaseVersion.equals(that.dataReleaseVersion);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, propertyKey, propertyValue, dataReleaseVersion);
    }
}