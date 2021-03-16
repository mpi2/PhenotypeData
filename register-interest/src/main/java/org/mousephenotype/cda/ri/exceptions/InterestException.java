/*******************************************************************************
 * Copyright © 2015 EMBL - European Bioinformatics Institute
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

package org.mousephenotype.cda.ri.exceptions;

import org.mousephenotype.cda.ri.entities.InterestStatus;

/**
 * Created by mrelac on 24/07/2015.
 */
public class InterestException extends Exception {

    private InterestStatus interestStatus = InterestStatus.OK;
    
    public InterestException() {
        super();
    }

    public InterestException(String message) {
        super(message);
    }
    
    public InterestException(InterestStatus interestStatus) {
        super();
        this.interestStatus = interestStatus;
    }
    
    public InterestException(String message, InterestStatus interestStatus) {
        super(message);
        this.interestStatus = interestStatus;
    }

    public InterestException(Exception e) {
        super(e);
    }

    public InterestException(String message, Throwable cause) {
        super(message, cause);
    }

    public InterestException(Throwable cause) {
        super(cause);
    }

    public InterestException(String message, Throwable cause, boolean enableSuppression, boolean writeableStackTrace) {
        super(message, cause, enableSuppression, writeableStackTrace);
    }

    public InterestStatus getInterestStatus() {
        return interestStatus;
    }

    public void setInterestStatus(InterestStatus interestStatus) {
        this.interestStatus = interestStatus;
    }

    @Override
    public String toString() {
        return "InterestException{" +
                "interestStatus=" + interestStatus +
                '}';
    }
}