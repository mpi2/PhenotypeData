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

package org.mousephenotype.cda.loads.common;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.Map;

/**
 * Created by mrelac on 10/08/16.
 */
public class LoadUtils {

    private NamedParameterJdbcTemplate npJdbcTemplate;

    public LoadUtils(NamedParameterJdbcTemplate npJdbcTemplate) {
        this.npJdbcTemplate = npJdbcTemplate;
    }

    /**
     * Wrapper for namedParameterJdbcTemplat.queryForObject
     * @param query the query to be executed
     * @param paramMap the parameter map
     * @return the result, if found; 0 otherwise
     */
    public long queryForPk(String query, Map<String, ?> paramMap) {
        long pk = 0L;

        try {
            pk = npJdbcTemplate.queryForObject(query, paramMap, Long.class);
        } catch (EmptyResultDataAccessException e) {

        }

        return pk;
    }

}
