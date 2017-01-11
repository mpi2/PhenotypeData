/*******************************************************************************
 * Copyright © 2015 EMBL - European Bioinformatics Institute
 * <p>
 * Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this targetFile except in compliance
 * with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 ******************************************************************************/

package org.mousephenotype.cda.loads.exceptions;

/**
 * Created by mrelac on 24/07/2015.
 */
public class DataLoadException extends Exception {

    public enum DETAIL {
        GENERAL_ERROR,
        NONEXISTENT_COLONY_ID,
        NO_GENE_FOR_ALLELE
    }

    private DETAIL detail = DETAIL.GENERAL_ERROR;

    public DataLoadException() {
        super();
    }

    public DataLoadException(String message) {
        super(message);
    }

    public DataLoadException(Exception e) {
        super(e);
    }

    public DataLoadException(String message, Throwable cause) {
        super(message, cause);
    }

    public DataLoadException(Throwable cause) {
        super(cause);
    }

    public DataLoadException(String message, Throwable cause, boolean enableSuppression, boolean writeableStackTrace) {
        super(message, cause, enableSuppression, writeableStackTrace);
    }



    public DataLoadException(DETAIL detail) {
        super();
        this.detail = detail;
    }

    public DataLoadException(String message, DETAIL detail) {
        super(message);
        this.detail = detail;
    }

    public DETAIL getDetail() {
        return detail;
    }

    public void setDetail(DETAIL detail) {
        this.detail = detail;
    }
}
