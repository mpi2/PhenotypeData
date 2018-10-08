/*******************************************************************************
 * Copyright © 2018 EMBL - European Bioinformatics Institute
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

package uk.ac.ebi.phenotype.generic.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SecurityUtils {

    private static final Logger logger = LoggerFactory.getLogger(SecurityUtils.class);

    public static final String ANONYMOUS_USER = "anonymous";

    public SecurityUtils() {
    }

    public static String getPrincipal() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return getPrincipal(authentication);
    }

    public static String getPrincipal(Authentication authentication) {
        if (authentication == null) {
            return ANONYMOUS_USER;
        } else {
            Object principal = authentication.getPrincipal();
            String userName;
            if (principal instanceof UserDetails) {
                userName = ((UserDetails)principal).getUsername();
            } else {
                userName = principal.toString();
            }

            return userName;
        }
    }

    public static boolean isLoggedIn() {

        Authentication         authentication = SecurityContextHolder.getContext().getAuthentication();
        List<GrantedAuthority> authorities    = new ArrayList<>(authentication.getAuthorities());

        boolean isLoggedIn = false;

logger.info("principal = {}", authentication.getPrincipal().toString());
logger.info("authorities = {}", authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()));
        for (GrantedAuthority authority : authorities) {
            if (authority.getAuthority().equals("ROLE_USER")) {
                isLoggedIn = true;
                break;
            }
        }

        return isLoggedIn;
    }
}