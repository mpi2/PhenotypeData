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

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by mrelac on 13/07/2015.
 */
@Component
public class CommonUtils {

    private final static double EPSILON = 0.000000001;

    public Map<String,Integer> getGoCodeRank(){

    	//GO evidence code ranking mapping
        final Map<String,Integer> codeRank = new HashMap<>();

        // experimental
	    codeRank.put("EXP", 5);codeRank.put("IDA", 5);codeRank.put("IPI", 5);codeRank.put("IMP", 5);
	    codeRank.put("IGI", 5);codeRank.put("IEP", 5);

	    // curated computational
	    codeRank.put("ISS", 4);codeRank.put("ISO", 4);codeRank.put("ISA", 4);codeRank.put("ISM", 4);
	    codeRank.put("IGC", 4);codeRank.put("IBA", 4);codeRank.put("IBD", 4);codeRank.put("IKR", 4);
	    codeRank.put("IRD", 4);codeRank.put("RCA", 4);

	    // automated electronic
	    codeRank.put("IEA", 3);

	    // other
	    codeRank.put("TAS", 2);codeRank.put("NAS", 2);codeRank.put("IC", 2);

	    // no biological data available
	    codeRank.put("ND", 1);

    	return codeRank;
    }

    /**
     * Performs an approximate match between two doubles. Returns true if
     * the two values are within a difference of 0.000000001; false otherwise
     * @param a first operand
     * @param b second operand
     * @return true if  the two values are within a difference of 0.000000001;
     * false otherwise
     */
    public boolean equals(double a, double b) {
        return (a == b ? true : Math.abs(a - b) < EPSILON);
    }

    /**
     * Performs an approximate match between two doubles. Returns true if
     * the two values are within <code>epsilon</code>; false otherwise
     * @param a first operand
     * @param b second operand
     * @param epsilon the difference within which both operands are considered
     * equal
     * @return true if  the two values are within <code>epsilon</code>; false otherwise
     */
    public boolean equals(double a, double b, double epsilon) {
        return (a == b ? true : Math.abs(a - b) < epsilon);
    }

    /**
     * Return the string representation of the specified <code>milliseconds</code>
     * in hh:mm:ss format. NOTE: year, month, and day do not participate in the
     * computation. If milliseconds is longer than 24 hours, incorrect results
     * will be returned.
     *
     * @param milliseconds
     * @return
     */
    public String msToHms(Long milliseconds) {
        String result = String.format("%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(milliseconds),
                TimeUnit.MILLISECONDS.toMinutes(milliseconds) -
                        TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(milliseconds)),
                TimeUnit.MILLISECONDS.toSeconds(milliseconds) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliseconds)));

        return result;
    }

    /**
     * Sleeps the thread for <code>thread_wait_in_ms</code> milliseconds.
     * If <code>threadWaitInMs</code> is null or 0, no sleep is executed.
     *
     * @param threadWaitInMs length of time, in milliseconds, to sleep.
     */
    public void sleep(Integer threadWaitInMs) {
        if ((threadWaitInMs != null) && (threadWaitInMs > 0))
            try { Thread.sleep(threadWaitInMs); } catch (Exception e) { }
    }

    /**
     * Given an <code>Object</code> that may be null or may be a float or double, this
     * method attempts to convert the value to a <code>Double</code>. If successful,
     * the <code>Double</code> value is returned; otherwise, <code>null</code> is returned.
     * NOTE: the [non-null] object is first converted to a string and is trimmed of whitespace.
     * @param o the object to try to convert
     * @return the converted value, if <em>o</em> is a <code>Float or Double</code>; null otherwise
     */
    public Double tryParseDouble(Object o) {
        if (o == null)
            return null;

        Double retVal = null;
        try {
            retVal = Double.parseDouble(o.toString().trim());
        }
        catch (NumberFormatException nfe ) { }

        return retVal;
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
