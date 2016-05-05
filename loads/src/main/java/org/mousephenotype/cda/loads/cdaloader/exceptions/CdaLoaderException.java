/*******************************************************************************
 *  Copyright (c) 2013 - 2015 EMBL - European Bioinformatics Institute
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



package org.mousephenotype.cda.loads.cdaloader.exceptions;

/**
 *
 * @author mrelac
 */
public class CdaLoaderException extends Exception {

    public CdaLoaderException() {
        super("");
    }

    public CdaLoaderException(String message) {
        super(message);
    }
    
    public CdaLoaderException(Exception e) {
        super(e);
    }
    
    public CdaLoaderException(String message, Exception e) {
        super(message, e);
    }
}
