/*******************************************************************************
 * Copyright 2015 EMBL - European Bioinformatics Institute
 *
 * Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 *******************************************************************************/

package org.mousephenotype.cda.utilities;

import java.io.IOException;
import java.net.URL;

/**
 *
 * @author mrelac
 */
public class DataReaderFactory {

    /**
     * Given a URL, this method returns a <code>DataReader</code> of the correct
     * type to handle the stream identified by <code>url</code>.
     * @param url the url (which contains all the information necessary to
     * create the correctly typed <code>DataReader</code>)
     * @return a <code>DataReader</code> capable of correctly handling the stream
     * identified by <code>url</code>.
     * @throws IOException
     */
    public DataReader create(URL url) throws IOException {
        String query = url.getQuery();
        String[] queryArray = query.split("&");
        for (String s : queryArray) {
            if (s.startsWith("fileType=")) {
                String filetype = s.replace("fileType=", "");
                switch (filetype) {
                    case "tsv":
                        return new DataReaderTsv(url);
                        
                    case "xls":
                        return new DataReaderXls(url);
                        
                    default:
                        throw new IOException("Unknown stream type '" + filetype + "'.");
                }
            }
        }
        
        throw new IOException("Expected url query with substring 'fileType'. url query = '" + query + "'.");
    }
}
