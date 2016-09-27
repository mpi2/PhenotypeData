/*******************************************************************************
 * Copyright © 2015 EMBL - European Bioinformatics Institute
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

package org.mousephenotype.cda.reports.support;

/**
 * This class extends LoadsQuery, adding a delta, which is the ratio of the query result from the current database
 * divided by the query result from the previous database. Any values below this threshold will cause a warning to be
 * generated.
 *
 * Created by mrelac on 22/09/16.
 */
public class LoadsQueryDelta extends LoadsQuery {
    private Double delta;

    public LoadsQueryDelta() {
        super();
    }

    public LoadsQueryDelta(String name, Double delta, String query) {
        super(name, query);
        this.delta = delta;
    }

    public Double getDelta() {
        return delta;
    }

    public void setDelta(Double delta) {
        this.delta = delta;
    }
}
