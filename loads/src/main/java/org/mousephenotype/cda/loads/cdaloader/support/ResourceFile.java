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

import org.mousephenotype.cda.loads.cdaloader.exceptions.CdaLoaderException;
import org.mousephenotype.cda.loads.cdaloader.steps.tasklets.Downloader;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Autowired;

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

    private String filename;
    private String sourceUrl;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    /**
     * Return an <code>ItemReader</code> instance of the specified type <code>T</code>.
     *
     * @return an <code>ItemReader</code> instance of the specified type <code>T</code>.
     */
    public abstract Step getLoadStep() throws CdaLoaderException;

    public Step getDownloadStep() {
        Downloader downloader = new Downloader();
        downloader.initialise(this.sourceUrl, this.filename);
        return stepBuilderFactory.get("downloadStep")
                .tasklet(downloader)
                .build();
    }
    /**
     * Initialise a new <code>ResourceFile</code> instance
     * @param sourceUrl the resource source url
     * @param filename the fully qualified filename where the resource will be downloaded to and from which the <code>
     *                 ItemReader</code> will read
     */
    protected void initialise(String sourceUrl, String filename) {
        this.sourceUrl = sourceUrl;
        this.filename = filename;
    }
}
