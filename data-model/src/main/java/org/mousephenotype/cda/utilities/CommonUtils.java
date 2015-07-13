/*******************************************************************************
 *  Copyright © 2013 - 2015 EMBL - European Bioinformatics Institute
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

package org.mousephenotype.cda.utilities;

import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * Created by mrelac on 13/07/2015.
 */
@Component
public class CommonUtils {

    /**
     * Return the string representation of the specified <code>milliseconds</code>
     * in hh:mm:ss format. NOTE: year, month, and day do not participate in the
     * computation. If milliseconds is longer than 24 hours, incorrect results
     * will be returned.
     *
     * @param milliseconds
     * @return
     */
    public static String msToHms(Long milliseconds) {
        String result = String.format("%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(milliseconds),
                TimeUnit.MILLISECONDS.toMinutes(milliseconds) -
                        TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(milliseconds)),
                TimeUnit.MILLISECONDS.toSeconds(milliseconds) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliseconds)));

        return result;
    }

    /**
     * Given an <code>Object</code> that may be null or may be an Integer, this
     * method attempts to convert the value to an <code>Integer</code>. If successful,
     * the <code>Integer</code> value is returned; otherwise, <code>null</code> is returned.
     * NOTE: the [non-null] object is first converted to a string and is trimmed of whitespace.
     * @param o the object to try to convert
     * @return the converted value, if <em>o</em> is an <code>Integer</code>; null otherwise
     */
    public Integer tryParseInt(Object o) {
        if (o == null)
            return null;

        Integer retVal = null;
        try {
            retVal = Integer.parseInt(o.toString().trim().replace(",", ""));    // Remove commas. Number strings like '48,123' don't parse.
        }
        catch (NumberFormatException nfe ) { }

        return retVal;
    }
}
