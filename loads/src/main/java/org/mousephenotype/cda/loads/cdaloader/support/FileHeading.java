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

/**
 * Created by mrelac on 09/06/16.
 */
@Deprecated
public class FileHeading {
    public final int offset;
    public final String heading;

    /**
     * Creates an instance of an offset -> heading describing the mapping between the heading name and its column offset
     * in the data stream
     *
     * @param offset the 0-relative offset of the heading in the file stream
     * @param heading the heading name in the file
     */
    @Deprecated
    public FileHeading(int offset, String heading) {
        this.offset = offset;
        this.heading = heading;
    }

    @Override
    public String toString() {
        return "FileHeading{" +
                "offset=" + offset +
                ", heading='" + heading + '\'' +
                '}';
    }
}
