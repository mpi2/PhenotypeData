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

package org.mousephenotype.cda.loads.cdaloader;

import org.mousephenotype.cda.loads.cdaloader.exception.CdaLoaderException;
import org.springframework.batch.item.ItemReader;

/**
 * This class encapsulates the code and data necessary to represent a resource file, identified by a URL, containing
 * interfaces for a contract to:
 *   - download the resource from the specified URL
 *   - return an ItemReader of a specified type that returns a single instance of the resource, implemented in the
 *   <code>read()</code> implementation.
 *
 * Created by mrelac on 04/05/16.
 */
public abstract class ResourceFile {

    protected String sourceFile;
    protected int dbId;

    /**
     * Return an <code>ItemReader</code> instance of the specified type <code>T</code>.
     *
     * @return an <code>ItemReader</code> instance of the specified type <code>T</code>.
     */
    public abstract ItemReader<?> getItemReader() throws CdaLoaderException;

    /**
     * Download the resource file set by <code>setResourceFile</code>
     */
    public abstract void download();

    public void initialise(String sourceFile, int dbId) {
        this.sourceFile = sourceFile;
        this.dbId = dbId;
    }

    public int getDbId() {
        return dbId;
    }

    public void setDbId(int dbId) {
        this.dbId = dbId;
    }

    public String getSourceFile() {
        return sourceFile;
    }

    public void setSourceFile(String sourceFile) {
        this.sourceFile = sourceFile;
    }
}
