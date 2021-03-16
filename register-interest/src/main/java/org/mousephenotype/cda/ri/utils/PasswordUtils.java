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

package org.mousephenotype.cda.ri.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.security.SecureRandom;

public class PasswordUtils {

    public static String generateSecureRandomPassword() {
        Integer      PASSWORD_LENGTH = 12;
        SecureRandom secureRandom    = new SecureRandom();
        byte[]       bytes           = new byte[PASSWORD_LENGTH];

        secureRandom.nextBytes(bytes);
        PasswordEncoder passwordEncoder   = new BCryptPasswordEncoder();
        String          encryptedPassword = passwordEncoder.encode(bytes.toString());

        return encryptedPassword;
    }
}
