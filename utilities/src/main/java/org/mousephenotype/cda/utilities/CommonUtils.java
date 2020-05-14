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

import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLClassLoader;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

/**
 * Created by mrelac on 13/07/2015.
 * <p>
 * NOTE: Please do not add any methods here that require being wired in to Spring. Keep this file spring-free, as it
 * is used in many places that are not spring-dependent.
 */
public class CommonUtils {

    private static final org.slf4j.Logger logger   = LoggerFactory.getLogger(CommonUtils.class);
    private final static double           EPSILON  = 0.000000001;
    private              Date             zeroDate = new Date(0);

    /**
     * Validates a date according to load rules: date must be on or after minDate, and on or before maxDate. Null
     * dates,  0 dates, and dates outside of minDate or maxDate are NOT considered valid.
     *
     * @param date    The date to validate
     * @param minDate The minimum date (inclusive)
     * @param maxDate The maximum date (inclusive)
     * @return
     */
    public boolean isDateValid(Date date, Date minDate, Date maxDate) {

        if ((date == null) || (date == zeroDate) || (date.before(minDate) || (date.after(maxDate)))) {
            return false;
        }

        return true;
    }

    /**
     * Returns the classpath, prefaced by the string 'Classpath:\n'. Each
     * file is separated by a newline.
     *
     * @return the classpath, prefaced by the string 'Classpath:\n'. Each
     * file is separated by a newline.
     */
    public String toStringClasspath() {
        StringBuilder sb   = new StringBuilder("Classpath:\n");
        ClassLoader   cl   = ClassLoader.getSystemClassLoader();
        URL[]         urls = ((URLClassLoader) cl).getURLs();

        for (URL url : urls) {
            sb.append(url.getFile()).append("\n");
        }

        return sb.toString();
    }

    /**
     * Walks <code>data</code>, replacing each value in <code>expandColumnDefinitions</code> that contains a <code>delimeter</code>
     * symbol into separate columns, adding each new column after the original. Returns the new data set.
     *
     * @param data                    The data against which to perform the translation
     * @param expandColumnDefinitions A list of column indexes against which the translation will be performed
     * @param delimeter               the unescaped component separator (e.g. "|" or "/")
     * @return the transformed (expanded) list
     */
    public List<List<String>> expandCompoundColumns(List<List<String>> data, List<Integer> expandColumnDefinitions, String delimeter) {
        List<List<String>> retVal = new ArrayList<>();

        for (List<String> sourceRow : data) {
            List<String> targetRow = new ArrayList<>();
            retVal.add(targetRow);
            for (int colIndex = 0; colIndex < sourceRow.size(); colIndex++) {
                String value = sourceRow.get(colIndex);
                if (expandColumnDefinitions.contains(colIndex)) {
                    String[] parts = value.split(Pattern.quote(delimeter));
                    for (String s : parts) {
                        targetRow.add(s.trim());
                    }
                } else {
                    targetRow.add(value);
                }
            }
        }

        return retVal;
    }

    /**
     * Returns the first int found, if the int is wrapped in a set of parentheses; returns null otherwise
     * Example: "Females: (32), Males: (47)" returns the int value 32.
     *
     * @param inputString The input string
     * @return the first int found, if the int is wrapped in a set of parentheses; null otherwise
     */
    public int extractIntFromParens(String inputString) {
        Integer retVal = null;

        int openParen = inputString.indexOf("(");
        if (openParen >= 0) {
            int closeParen = inputString.indexOf(")");
            if ((closeParen >= 0) && (openParen < closeParen)) {
                retVal = tryParseInt(inputString.substring(openParen + 1, closeParen));
            }
        }

        return retVal;
    }


    public Map<String, Integer> getGoCodeRank() {

        //GO evidence code ranking mapping
        final Map<String, Integer> codeRank = new HashMap<>();

        // experimental
        codeRank.put("EXP", 5);
        codeRank.put("IDA", 5);
        codeRank.put("IPI", 5);
        codeRank.put("IMP", 5);
        codeRank.put("IGI", 5);
        codeRank.put("IEP", 5);

        // curated computational
        codeRank.put("ISS", 4);
        codeRank.put("ISO", 4);
        codeRank.put("ISA", 4);
        codeRank.put("ISM", 4);
        codeRank.put("IGC", 4);
        codeRank.put("IBA", 4);
        codeRank.put("IBD", 4);
        codeRank.put("IKR", 4);
        codeRank.put("IRD", 4);
        codeRank.put("RCA", 4);

        // automated electronic
        codeRank.put("IEA", 3);

        // other
        codeRank.put("TAS", 2);
        codeRank.put("NAS", 2);
        codeRank.put("IC", 2);

        // no biological data available
        codeRank.put("ND", 1);

        return codeRank;
    }

    public String getMysqlFullpath() {
        try {
            Process p = Runtime.getRuntime().exec("which mysql");
            p.waitFor();
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String         line   = reader.readLine();
            reader.close();

            return line;
        } catch (Exception e1) {
        }

        return "";
    }

    /**
     * This method is a best-effort to parse procedure/parameter raw status into a status and a statusValue.
     * rawStatus values usually look like "[xxx[dyyy]]", where xxx (optional) is the status, and dyyy (optional)
     * is a single-character delimiter (d) and yyy is the statusValue. However, sometimes the delimiter is ":",
     * other times it is "?", and in some cases the entire rawStatus is meaningless (e.g. _PNM-NIS_). Since
     * status indicates missing data, proper parsing of the rawStatus is not crucial. rawStatus is most useful if it
     * contains a recognised impress status code in the format described above (xxx or xxxdyyy).
     * <p>
     * The returned array is guaranteed to always have exactly two elements.
     *
     * @param rawStatus the status to parse
     * @return the status in element[0], the status message in element[1]. Either/all elements may be null.
     */
    public String[] parseImpressStatus(String rawStatus) {
        String[] retVal = new String[]{null, null};

        if ((rawStatus == null) || rawStatus.trim().isEmpty()) {
            return retVal;
        }

        int idx = rawStatus.indexOf(":");
        if (idx == -1) {
            idx = rawStatus.indexOf("?");
        }

        if (idx != -1) {
            retVal[0] = rawStatus.substring(0, idx);
            retVal[1] = rawStatus.substring(idx + 1);
        } else {
            retVal[0] = rawStatus;
            retVal[1] = null;
        }

        for (int i = 0; i < retVal.length; i++) {
            if (retVal[i] != null) {
                retVal[i] = retVal[i].trim();
                if (retVal[i].isEmpty()) {
                    retVal[i] = null;
                }
            }
        }

        return retVal;
    }

    /**
     * Print the jvm memory configuration.
     */
    public static void printJvmMemoryConfiguration() {
        final int     mb        = 1024 * 1024;
        Runtime       runtime   = Runtime.getRuntime();
        DecimalFormat formatter = new DecimalFormat("#,###");
        logger.info("  Used memory:  {}", (formatter.format(runtime.totalMemory() - runtime.freeMemory() / mb)));
        logger.info("  Free memory : {}", formatter.format(runtime.freeMemory()));
        logger.info("  Total memory: {}", formatter.format(runtime.totalMemory()));
        logger.info("  Max memory:   {}\n", formatter.format(runtime.maxMemory()));
    }

    /**
     * Returns a {@code String} with the specified map's contents (key/value pairs), prefaced by its name
     *
     * @param map     the map to query
     * @param mapName the name of the map (used to identify the map)
     * @return a {@code String} with the specified map's contents (key/value pairs), prefaced by its name
     */
    public String mapToString(Map<String, Object> map, String mapName) {
        final StringBuilder retVal = new StringBuilder();

        map.entrySet().stream().forEach(entry -> {
            if (retVal.toString().isEmpty())
                retVal.append("MAP " + mapName + "= [");
            else
                retVal.append("], [");
            retVal.append(StringUtils.join(entry.getKey() + ": " + (entry.getValue() == null ? "<null>" : entry.getValue().toString())));
        });

        if (!retVal.toString().isEmpty())
            retVal.append("]");

        return retVal.toString();
    }

    /**
     * Performs an approximate match between two doubles. Returns true if
     * the two values are within a difference of 0.000000001; false otherwise
     *
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
     *
     * @param a       first operand
     * @param b       second operand
     * @param epsilon the difference within which both operands are considered
     *                equal
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
     * Given two dates (in any order), returns a <code>String</code> in the
     * format "xxx days, yyy hours, zzz minutes, nnn seconds" that equals
     * the absolute value of the time difference between the two days.
     *
     * @param date1 the first operand
     * @param date2 the second operand
     * @return a <code>String</code> in the format "dd:hh:mm:ss" that equals the
     * absolute value of the time difference between the two date.
     */
    public String formatDateDifference(Date date1, Date date2) {
        long lower = Math.min(date1.getTime(), date2.getTime());
        long upper = Math.max(date1.getTime(), date2.getTime());
        long diff  = upper - lower;

        long days    = diff / (24 * 60 * 60 * 1000);
        long hours   = diff / (60 * 60 * 1000) % 24;
        long minutes = diff / (60 * 1000) % 60;
        long seconds = diff / 1000 % 60;

        return String.format("%02d:%02d:%02d:%02d", days, hours, minutes, seconds);
    }

    /**
     * Sleeps the thread for <code>thread_wait_in_ms</code> milliseconds.
     * If <code>threadWaitInMs</code> is null or 0, no sleep is executed.
     *
     * @param threadWaitInMs length of time, in milliseconds, to sleep.
     */
    public void sleep(Integer threadWaitInMs) {
        if ((threadWaitInMs != null) && (threadWaitInMs > 0))
            try {
                Thread.sleep(threadWaitInMs);
            } catch (Exception e) {
            }
    }

    /**
     * Given an <code>Object</code> that may be null or may be a float or double, this
     * method attempts to convert the value to a <code>Double</code>. If successful,
     * the <code>Double</code> value is returned; otherwise, <code>null</code> is returned.
     * NOTE: the [non-null] object is first converted to a string and is trimmed of whitespace.
     *
     * @param o the object to try to convert
     * @return the converted value, if <em>o</em> is a <code>Float or Double</code>; null otherwise
     */
    public Double tryParseDouble(Object o) {
        if (o == null)
            return null;

        Double retVal = null;
        try {
            retVal = Double.parseDouble(o.toString().trim());
        } catch (NumberFormatException nfe) {
        }

        return retVal;
    }

    /**
     * Given an <code>Object</code> that may be null or may be an Integer, this
     * method attempts to convert the value to an <code>Integer</code>. If successful,
     * the <code>Integer</code> value is returned; otherwise, <code>null</code> is returned.
     * NOTE: the [non-null] object is first converted to a string and is trimmed of whitespace.
     *
     * @param o the object to try to convert
     * @return the converted value, if <em>o</em> is an <code>Integer</code>; null otherwise
     */
    public static Integer tryParseInt(Object o) {
        if (o == null)
            return null;

        Integer retVal = null;
        try {
            retVal = Integer.parseInt(o.toString().trim().replace(",", ""));    // Remove commas. Number strings like '48,123' don't parse.
        } catch (NumberFormatException nfe) {
        }

        return retVal;
    }

    /**
     * Given an <code>Object</code> that may be null or may be a Long, this
     * method attempts to convert the value to a <code>Long</code>. If successful,
     * the <code>Long</code> value is returned; otherwise, <code>null</code> is returned.
     * NOTE: the [non-null] object is first converted to a string and is trimmed of whitespace.
     *
     * @param o the object to try to convert
     * @return the converted value, if <em>o</em> is an <code>Long</code>; null otherwise
     */
    public static Long tryParseLong(Object o) {
        if (o == null)
            return null;

        Long retVal = null;
        try {
            retVal = Long.parseLong(o.toString().trim().replace(",", ""));    // Remove commas. Number strings like '48,123' don't parse.
        } catch (NumberFormatException nfe) {
        }

        return retVal;
    }


    /**
     * @param truthValues
     * @return List of "integer" bit mask representation of the truth values.
     * Only 31 bits are set even though Doubles are returned from Math.pow as we plan to use these masks in javascript
     * which handles signed int.
     * <p>
     * This was written for the parallel coordinates tool.
     */
    public static List<Double> getBitMask(List<Boolean> truthValues) {

        List<Double> masks       = new ArrayList<>();
        Double       currentMask = new Double(0);
        int          maskIndex   = 0;

        for (int i = 0; i < truthValues.size(); i++) {
            if (i % 31 == 0 && i != 0) { // only 31 bits as they're signed
                masks.add(new Double(currentMask));
                currentMask = new Double(0);
                maskIndex++;
            }
            if (truthValues.get(i)) {
                currentMask += Math.pow(new Double(2), new Double(i - 31 * maskIndex));
            }
        }

        masks.add(currentMask);

        return masks;

    }

    /**
     * Given a collection of {@link String}, returns a single string with each value in {@code values} wrapped in
     * {@code delimeter} and separated by a comma.
     *
     * @param values
     * @param delimeter
     * @return
     */
    public String wrapInQuotes(Collection<String> values, char delimeter) {
        StringBuilder retVal = new StringBuilder();

        int i = 0;
        for (String value : values) {
            if (i++ > 0)
                retVal.append(",");
            retVal.append(delimeter + value + delimeter);
        }

        return retVal.toString();
    }
}