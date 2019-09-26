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

import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author mrelac
 * 
 * This class encapsulates the code and data necessary to represent a run status.
 * Use this class to accumulate error and warning messages and optionally to track a success count.
 * 
 */
public class RunStatus {
    private final Set<String>   errorMessages;
    private final Set<String>   warningMessages;
    public        int          successCount;

    public RunStatus() {
        errorMessages = new HashSet<>();
        warningMessages = new HashSet<>();
        successCount = 0;
    }

    public Set<String> getErrorMessages() {
        return errorMessages;
    }

    public Set<String> getWarningMessages() {
        return warningMessages;
    }
    
    public void add(RunStatus runStatus) {
        errorMessages.addAll(runStatus.errorMessages);
        warningMessages.addAll(runStatus.warningMessages);
        successCount += runStatus.successCount;
    }

    public void addError(String errorMessage) {
        this.errorMessages.add(errorMessage);
    }
    
    public void addError(Set<String> errorMessages) {
        this.errorMessages.addAll(errorMessages);
    }

    public void addWarning(String warningMessage) {
        this.warningMessages.add(warningMessage);
    }
    
    public void addWarning(Set<String> warningMessage) {
        this.warningMessages.addAll(warningMessage);
    }
    
    public boolean hasErrors() {
        return (errorMessages.size() > 0);
    }
    
    public boolean hasWarnings() {
        return (warningMessages.size() > 0);
    }
    
    @Override
    public String toString() {
        return "RunStatus {" + "errors=" + errorMessages.size() + ", warnings=" + warningMessages.size() + ", successCount=" + successCount + "}";
    }

    /**
     * @return  All of the error messages in a String, each message terminated
     * with a newline, suitable for display. Returns an empty string if there
     * are no error messages.
     */
    public String toStringErrorMessages() {
        StringBuilder sb = new StringBuilder();
        for (String s : errorMessages) {
            sb.append(s);
            sb.append("\n");
        }

        return sb.toString();
    }

    /**
     * @return  All of the warning messages in a String, each message terminated
     * with a newline, suitable for display. Returns an empty string if there
     * are no warning messages.
     */
    public String toStringWarningMessages() {
        StringBuilder sb = new StringBuilder();
        for (String s : warningMessages) {
            sb.append(s);
            sb.append("\n");
        }

        return sb.toString();
    }
}