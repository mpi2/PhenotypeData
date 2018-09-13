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

package org.mousephenotype.cda.ri.core.utils;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.security.SecureRandom;

public class SecurityUtils {

    public String getPrincipal() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        return getPrincipal(authentication);
    }

    public String getPrincipal(Authentication authentication) {
        String userName;

        if (authentication == null) {
            return "anonymous";
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof UserDetails) {
            userName = ((UserDetails) principal).getUsername();
        } else {
            userName = principal.toString();
        }
        return userName;
    }

    public static String generateSecureRandomPassword() {
        final Integer PASSWORD_LENGTH = 12;
        SecureRandom  secureRandom    = new SecureRandom();
        byte          bytes[]         = new byte[PASSWORD_LENGTH];

        secureRandom.nextBytes(bytes);
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encryptedPassword = passwordEncoder.encode(bytes.toString());

        return encryptedPassword;
    }

    /**
     * @return cookie string as <i>cookieName=cookieValue</i>
     */
    public static String getCookieNameValuePair(HttpServletRequest request, String cookieName) {

        Cookie cookie = getCookie(request, cookieName);

        if (cookie != null) {
            return cookie.getName() + "=" + cookie.getValue();
        }

        return "";
    }

    /**
     * @return {@code cookieName} cookie
     */
    public static Cookie getCookie(HttpServletRequest request, String cookieName) {

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(cookieName)) {
                    return cookie;
                }
            }
        }

        return null;
    }

    public static String getTokenFromQueryString(String queryString) {

        if ((queryString == null) || (queryString.isEmpty())) {
            return "";
        }

        String[] pieces = StringUtils.split(queryString, "=");
        if ((pieces.length != 2) && (!pieces[0].equals("token"))) {
            return "";
        }

        return pieces[1];
    }

    public static HttpHeaders buildHeadersFromJsessionId(HttpServletRequest request) {
        String      cookieNameValuePair  = getCookieNameValuePair(request, "JSESSIONID");
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cookie", cookieNameValuePair);

        return headers;
    }
}