/*******************************************************************************
 *  Copyright © 2017 EMBL - European Bioinformatics Institute
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

package org.mousephenotype.cda.ri.entities;

import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Date;

/**
 * Created by mrelac on 12/05/2017.
 * This entity class maps to the contact table.
 */
public class Contact {
    private int                          pk;
    private String                       emailAddress;
    private boolean                      inHtml;
    private boolean                      isAccountLocked;
    private boolean                      isPasswordExpired;
    private String                       password;
    private Collection<GrantedAuthority> roles;

    private Date createdAt;
    private Date updatedAt;

    public int getPk() {
        return pk;
    }

    public void setPk(int pk) {
        this.pk = pk;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public boolean isInHtml() {
        return inHtml;
    }

    public void setInHtml(boolean inHtml) {
        this.inHtml = inHtml;
    }

    public boolean isAccountLocked() {
        return isAccountLocked;
    }

    public void setAccountLocked(boolean accountLocked) {
        isAccountLocked = accountLocked;
    }

    public boolean isPasswordExpired() {
        return isPasswordExpired;
    }

    public void setPasswordExpired(boolean passwordExpired) {
        isPasswordExpired = passwordExpired;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Collection<GrantedAuthority> getRoles() {
        return roles;
    }

    public void setRoles(Collection<GrantedAuthority> roles) {
        this.roles = roles;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "Contact{" +
            "pk=" + pk +
            ", emailAddress='" + emailAddress + '\'' +
            ", inHtml='" + inHtml + '\'' +
            ", isAccountLocked=" + isAccountLocked +
            ", isPasswordExpired=" + isPasswordExpired +
            ", password='" + password + '\'' +
            ", roles=" + roles +
            ", createdAt=" + createdAt +
            ", updatedAt=" + updatedAt +
            '}';
    }
}