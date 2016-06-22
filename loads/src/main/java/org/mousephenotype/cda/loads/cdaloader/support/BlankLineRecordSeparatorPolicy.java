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

package org.mousephenotype.cda.loads.cdaloader.support;

import org.springframework.batch.item.file.separator.SimpleRecordSeparatorPolicy;

/**
 * Spring batch doesn't appear to have a simple way to ignore blank lines. This is the recommended solution.
 * Created by mrelac on 22/06/16.
 */
public class BlankLineRecordSeparatorPolicy extends SimpleRecordSeparatorPolicy {

    @Override
    public boolean isEndOfRecord(String line) {
        if (line.trim().isEmpty()) {
            return false;
        }

        return super.isEndOfRecord(line);
    }

    @Override
    public String postProcess(String record) {
        if (record == null || record.trim().isEmpty()) {
            return null;
        }

        return super.postProcess(record);
    }
}
