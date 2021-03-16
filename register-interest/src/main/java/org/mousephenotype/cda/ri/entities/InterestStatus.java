/*******************************************************************************
 * Copyright © 2018 EMBL - European Bioinformatics Institute
 * <p>
 * Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
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

package org.mousephenotype.cda.ri.entities;

import org.springframework.http.HttpStatus;

public enum InterestStatus {
    EXISTS,
    INTERNAL_ERROR,
    NOT_FOUND,
    OK
    ;

    public HttpStatus toHttpStatus() {

        switch (this) {
            case EXISTS:
                return HttpStatus.BAD_REQUEST;

            case NOT_FOUND:
                return HttpStatus.NOT_FOUND;

            case INTERNAL_ERROR:
                return HttpStatus.INTERNAL_SERVER_ERROR;

            default:
                return HttpStatus.OK;
        }
    }
}