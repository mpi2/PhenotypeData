/*******************************************************************************
 * Copyright © 2019 EMBL - European Bioinformatics Institute
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

package org.mousephenotype.cda.ri.entities;

public class SmtpParameters {

    private String  smtpHost;
    private Integer smtpPort;
    private String  smtpFrom;
    private String  smtpReplyto;

    public SmtpParameters(String smtpHost, Integer smtpPort, String smtpFrom, String smtpReplyto) {
        this.smtpHost = smtpHost;
        this.smtpPort = smtpPort;
        this.smtpFrom = smtpFrom;
        this.smtpReplyto = smtpReplyto;
    }

    public String getSmtpHost() {
        return smtpHost;
    }

    public void setSmtpHost(String smtpHost) {
        this.smtpHost = smtpHost;
    }

    public Integer getSmtpPort() {
        return smtpPort;
    }

    public void setSmtpPort(Integer smtpPort) {
        this.smtpPort = smtpPort;
    }

    public String getSmtpFrom() {
        return smtpFrom;
    }

    public void setSmtpFrom(String smtpFrom) {
        this.smtpFrom = smtpFrom;
    }

    public String getSmtpReplyto() {
        return smtpReplyto;
    }

    public void setSmtpReplyto(String smtpReplyto) {
        this.smtpReplyto = smtpReplyto;
    }

    @Override
    public String toString() {
        return "SmtpParameters{" +
                "smtpHost='" + smtpHost + '\'' +
                ", smtpPort=" + smtpPort +
                ", smtpFrom='" + smtpFrom + '\'' +
                ", smtpReplyto='" + smtpReplyto + '\'' +
                '}';
    }
}
