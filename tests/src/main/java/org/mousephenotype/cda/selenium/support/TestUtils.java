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

package org.mousephenotype.cda.selenium.support;

import org.apache.commons.lang3.StringUtils;
import org.mousephenotype.cda.common.Constants;
import org.mousephenotype.cda.utilities.CommonUtils;
import org.mousephenotype.cda.utilities.RunStatus;
import org.openqa.selenium.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

import static org.junit.Assert.fail;

/**
 * This class is intended to hold methods useful for testing but not worthy of their own class.
 *
 * NOTE: Please do not add any methods here that require being wired in to Spring. Keep this file spring-free, as it
 *       is used in places that are not spring-dependent.
 *
 * @author mrelac
 *
 */
public class TestUtils {

    private CommonUtils commonUtils = new CommonUtils();
    private Map<String, String> testIterationsHash = new HashMap<>();

    private final Logger logger = LoggerFactory.getLogger(this.getClass().getCanonicalName());

    public final static String DATE_FORMAT = "yyyy/MM/dd HH:mm:ss";
    public final int DEFAULT_COUNT = 10;
    public static final String NO_SUPPORTING_DATA = "No supporting data supplied.";


    /**
     * Returns a list of the given values, each on a separate line, in the format "\t[0]: xxx"
     * @param values the values to be formatted and returned
     * @return a list of the given values, each on a separate line, in the format "\t[0]: xxx"
     */
    public String buildIndexedList(List<String> values) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < values.size(); i++) {
            sb.append("\t[").append(i).append("]: ").append(values.get(i)).append("\n");
        }

        return sb.toString();
    }
    /**
     * Counts and returns the number of sex icons in <code>table</code>
     * @param table the data store
     * @param sexColumnIndex the zero-relative sex column index in the data store
     * @param graphColumnIndex if not null, the zero-relative graph column index
     *                         which will be used to filter out non preqc-rows.
     * @return the number of sex icons in <code>table</code>: for each row,
     * if the sex = "male" or "female", add 1. If the sex = "both", add 2.
     */
    public int getSexIconCount(GridMap table, int sexColumnIndex, Integer graphColumnIndex) {
        int retVal = 0;

        for (String[] sA : table.getBody()) {
            // If this is a preqc row, skip it.
            if (graphColumnIndex != null) {
                if (sA[graphColumnIndex].contains("/phenoview/")) {
                    continue;
                }
            }

            if (sA[sexColumnIndex].equalsIgnoreCase("female"))
                retVal++;
            else if (sA[sexColumnIndex].equalsIgnoreCase("male"))
                retVal++;
            else if (sA[sexColumnIndex].equalsIgnoreCase("both"))
                retVal += 2;
        }

        return retVal;
    }

    /**
     * Return target count prioritized as follows:
     * <p><b>NOTE: If the returned target size is less than the collection size,
     * the collection is shuffled (i.e. randomized)</b></p>
     * <ul>
     * <li>if there is a system property matching <i>testMethodName</i>, that value is used</li>
     * <li>else if <i>testMethodName</i> appears in any test properties file, that value is used</li>
     * <li>else if <i>defaultCount</i> is not null, it is used</li>
     * <li>else the value defined by <i>DEFAULT_COUNT</i> is used</li>
     * </ul>
     * @param env the env hash of environment variables that could contain testMethodName.
     * @param testMethodName the method to which the target count applies
     * @param collection the collection to be tested (used for maximum size when target count of -1 is specified)
     * @param defaultCount if not null, the value to use if it was not specified as a -D parameter on the command line
     *                     and no match was found for <i>testMethodName</i> in <code>testIterations.properties</code>
     * @return target count
     */
    public int getTargetCount(Environment env, String testMethodName, List collection, Integer defaultCount) {
        Integer targetCount = null;

        if (tryParseInt(System.getProperty(testMethodName)) != null) {
            targetCount = tryParseInt(System.getProperty(testMethodName));
        } else if (tryParseInt(env.getProperty(testMethodName)) != null) {
            targetCount = tryParseInt(env.getProperty(testMethodName));
        } else if (defaultCount != null) {
            targetCount = defaultCount;
        } else {
            targetCount = DEFAULT_COUNT;
        }

        if (targetCount == -1)
            targetCount = collection.size();

        // If targetCount is less than the collection, randomize the collection.
        if (targetCount < collection.size()) {
            Collections.shuffle(collection);
            System.out.println("Randomizing collection.");
        }

        return Math.min(targetCount, collection.size());
    }

    /**
     * Returns true if <code>webElement</code> has a class attribute and its value contains <code>cssclass</code>;
     *         false otherwise.
     *
     * @param webElement the <code>WebElement to interrogate</code>
     * @param cssclass the class name sought
     *
     * @return true if <code>webElement</code> has a class attribute and its value contains <code>cssclass</code>;
     *         false otherwise.
     */
    public boolean hasCssclass(WebElement webElement, String cssclass) {
        String attribute = webElement.getAttribute("class");
        return ((attribute != null) && (attribute.contains(cssclass)));
    }

    // FIXME FIXME FIXME - Clean this up.


//    /**
//     * Return target count prioritized as follows:
//     * <p><b>NOTE: If the returned target size is less than the collection size,
//     * the collection is shuffled (i.e. randomized)</b></p>
//     * <ul>
//     * <li>if there is a system property matching <i>testMethodName</i>, that value is used</li>
//     * <li>else if <i>testMethodName</i> appears in the <code>testIterations.properties</code> file, that value is used</li>
//     * <li>else if <i>defaultCount</i> is not null, it is used</li>
//     * <li>else the value defined by <i>DEFAULT_COUNT</i> is used</li>
//     * </ul>
//     * @param testMethodName the method to which the target count applies
//     * @param defaultCount if not null, the value to use if it was not specified as a -D parameter on the command line
//     *                     and no match was found for <i>testMethodName</i> in <code>testIterations.properties</code>
//     * @return target count
//     */
//    public int getTargetCount(String testMethodName, Integer defaultCount) {
//        Integer targetCount = null;
//
//        if (defaultCount != null)
//            targetCount = defaultCount;
//
//        if (testIterationsHash.containsKey(testMethodName)) {
//            if (Utils.tryParseInt(testIterationsHash.get(testMethodName)) != null) {
//                targetCount = Utils.tryParseInt(testIterationsHash.get(testMethodName));
//            }
//        }
//        if (Utils.tryParseInt(System.getProperty(testMethodName)) != null) {
//            targetCount = Utils.tryParseInt(System.getProperty(testMethodName));
//        }
//
//        if (targetCount == null) {
//            targetCount = DEFAULT_COUNT;
//        }
//
//        return targetCount;
//    }
//
//    /**
//     * Compares two strings, each delimited by <code>delimiter</code> by count
//     * and value: if each has the same number of delimited segments, and each
//     * delimited segment's value is equal, returns true. If <code>
//     * ignoreNoInfoAvailable</code> is true, any string values equaling
//     * 'No information available' are first removed from each string before the
//     * comparison is performed.
//     * NOTE: null/empty strings are treated equally and, if both are either null
//     * or empty, return true.
//     *
//     * @param string1 the first string operand
//     * @param string2 the second string operand
//     * @param delimiter string segment delimiter
//     * @param ignoreNoInfoAvailable if true, the string 'No information available'
//     *        is first removed from both strings.
//     * @return true if each string's segments are equal; false otherwise
//     */
//    public static boolean isEqual(String string1, String string2, String delimiter, boolean ignoreNoInfoAvailable) {
//        boolean retVal;
//        Set<String> string1Set = new HashSet();
//        Set<String> string2Set = new HashSet();
//
//        if (string1 == null)
//            string1 = "";
//        if (string2 == null)
//            string2 = "";
//
//        String[] parts = string1.split(delimiter);
//        for (String part : parts) {
//            if ((ignoreNoInfoAvailable) && (part.equals(SearchFacetTable.Constants.NO_INFORMATION_AVAILABLE))) {
//                // Do nothing. This is the 'No information available' string, which the caller has asked us not to include.
//            } else {
//                string1Set.add(part);
//            }
//        }
//        parts = string2.split(delimiter);
//        for (String part : parts) {
//            if ((ignoreNoInfoAvailable) && (part.equals(SearchFacetTable.Constants.NO_INFORMATION_AVAILABLE))) {
//                // Do nothing. This is the 'No information available' string, which the caller has asked us not to include.
//            } else {
//                string2Set.add(part);
//            }
//        }
//
//        if (string1Set.size() == string2Set.size()) {
//            string1Set.removeAll(string2Set);
//            retVal = string1Set.isEmpty();
//        } else {
//            retVal = false;
//        }
//
//        return retVal;
//    }

    /**
     * Searches <code>list</code> for <code>searchToken</code>
     * @param list the list to search
     * @param searchToken the token to search for
     * @return true if <code>searchToken</code> was found in one of the strings
     * in <code>list</code>; false otherwise
     */
    public static boolean contains(List<WebElement> list, String searchToken) {
        if ((list == null) || (list.isEmpty()))
            return false;

        for (WebElement e : list) {
            if (e.getText().contains(searchToken))
                return true;
        }

        return false;
    }

    public static GridMap convertZygosityToShortName(GridMap downloadData, int zygosityColumnIndex) {
        GridMap gridMap = new GridMap(downloadData.getData(), downloadData.getTarget());

        for (String[] row : gridMap.getData()) {
            row[zygosityColumnIndex] = row[zygosityColumnIndex].substring(0, 3);
        }

        return gridMap;
    }

    /**
     * Searches <code>list</code> for <code>searchToken</code>, returning the
     * number of times <code>searchToken</code> appears in <code>list</code>.
     *
     * @param list the list to search
     * @param searchToken the token to search for
     * @return the number of times <code>searchToken</code> appears in <code>list</code>.
     */
    public static int count(List<String> list, String searchToken) {
        int retVal = 0;

        if ((list == null) || (list.isEmpty()))
            return retVal;

        for (String s : list) {
            if (s.contains(searchToken))
                retVal++;
        }

        return retVal;
    }

    /**
     * Clones an existing set.
     * @param input set to be cloned
     * @return a new deep-copy instance of input
     */
    public Set cloneStringSet(Set<String> input) {
        return cloneStringSet(input, false);
    }

    /**
     * Clones an existing set, lowercasing each string if directed.
     * @param input set to be cloned
     * @param setToLowercase if true, each string is set to lowercase; otherwise
     *        each string is left untouched.
     * @return a new deep-copy instance of input
     */
    public Set cloneStringSet(Set<String> input, boolean setToLowercase) {
        HashSet resultSet = new HashSet();

        for (String s : input) {
            if (setToLowercase) {
               resultSet.add(s.toLowerCase());
            } else {
               resultSet.add(s);
            }
        }

        return resultSet;
    }

    /**
     * Returns the closest match to <code>stringToMatch</code> in
     * <code>set</code>
     *
     * @param set the set to search
     *
     * @param stringToMatch the string to match
     *
     * @return the closest match to <code>stringToMatch</code> in <code>set</code>
     */
    public String closestMatch(Set<String> set, String stringToMatch) {
        String matchedString = "";
        Integer matchedScore = null;
        if ((set == null) || (stringToMatch == null))
            return matchedString;

        Iterator<String> it = set.iterator();
        while (it.hasNext()) {
            String candidate = it.next();
            int candidateScore = StringUtils.getLevenshteinDistance(candidate, stringToMatch);
            if (matchedString.isEmpty()) {                                      // First time through, populate matchedXxx.
                matchedString = candidate;
                matchedScore = candidateScore;
            } else {
                if ((candidateScore >= 0) && (candidateScore < matchedScore)) {
                    matchedScore = candidateScore;
                    matchedString = candidate;
                }
            }
        }

        return matchedString;
    }

    /**
     * Creates a set from <code>input</code> using <code>colIndexes</code>, using
     * the underscore character as a column delimiter. Each value is first trimmed,
     * then lowercased. Resulting strings containing multiple adjacent spaces are collapsed into a single space
     * so that comparison against web page strings (which automatically collapse multiple spaces into a single space)
     * won't fail simply because of multiple spaces.
     *
     * Example: input.body[][] = "a", "b", "c", "d", "e"
     *                           "f", "g", "h", "i", "j"
     *
     * colIndexes = 1, 3, 4
     *
     * produces a set that looks like:  "b_d_e_"
     *                                  "g_i_j_"
     * @param input Input object
     * @param colIndexes indexes of columns to be copied
     * @return a set containing the concatenated values.
     */
    public Set<String> createSet(GridMap input, Integer[] colIndexes) {
        HashSet resultSet = new HashSet();

        String[][] body = input.getBody();
        for (int rowIndex = 0; rowIndex < body.length; rowIndex++) {
            String[] row = body[rowIndex];
            String resultString = "";
            for (int colIndex : colIndexes) {
                String cell = "";

                try {
                    cell = row[colIndex].trim().toLowerCase() + "_";
                    cell = cell.replaceAll("\\s+", " ");
                } catch (Exception e) { }
                resultString += cell;
            }
            resultSet.add(resultString);
        }

        return resultSet;
    }

    /**
     * Dump <code>set</code> using logger ('info' level)
     *
     * @param name the set name (for display purposes)
     * @param set the set to be dumped
     * @param sort if true, sort the set before displaying it.
     */
    public void dumpSet(String name, Set<String> set, boolean sort) {
        System.out.println("\nDumping set '" + name + "'. Contains " + set.size() + " records:");
        if (set.size() <= 0)
            return;

        String[] data = set.toArray(new String[0]);
        if (sort) {
            Arrays.sort(data);
        }
        for (int i = 0; i < set.size(); i++) {
            System.out.println("[" + i + "]: " + data[i]);
        }
        System.out.println();
    }

    public String webElementToString(WebElement webElement) {
        String retVal = "";
        retVal += "class='" + webElement.getAttribute("class") + "'";
        retVal += ",id='" + webElement.getAttribute("id") + "'";
        retVal += ",tag name='" + webElement.getTagName() + "'";
        retVal += ",text='" + webElement.getText() + "'\n";

        return retVal;
    }

    public String webElementListToString(List<WebElement> webElementList) {
        String retVal = "";

        int i = 0;
        for (WebElement webElement : webElementList) {
            retVal += "[" + i + "]: " + webElementToString(webElement);
        }

        return retVal;
    }

    /**
     * Dump <code>set</code> as a string.
     *
     * @param set the set to be dumped
     *
     * @return the set, as a <code>String</code> with embedded newlines.
     */
    public String dumpSet(Set<String> set) {
        StringBuilder retVal = new StringBuilder();
        if (set.size() <= 0)
            return retVal.toString();

        String[] data = set.toArray(new String[0]);
        for (Integer i = 0; i < set.size(); i++) {
            retVal.append("[")
                  .append(i.toString())
                  .append("]: '")
                  .append(data[i])
                  .append("'\n");
        }

        return retVal.toString();
    }

    /**
     * Creates a set from <code>input</code> using <code>colIndexes</code>, using
     * the underscore character as a column delimiter. Each value is first trimmed,
     * then lowercased.
     *
     * Example: input.body[][] = "a", "b", "c", "d", "e"
     *                           "f", "g", "h", "i", "j"
     *
     * colIndexes = 1, 3, 4
     *
     * produces a set that looks like:  "b_d_e_"
     *                                  "g_i_j_"
     * @param input Input object
     * @param colIndexes indexes of columns to be copied
     * @return a set containing the concatenated values.
     */
    public Set<String> createSet(GridMap input, List<Integer> colIndexes) {
        HashSet resultSet = new HashSet();

        String[][] body = input.getBody();
        for (int rowIndex = 0; rowIndex < body.length; rowIndex++) {
            String[] row = body[rowIndex];
            String resultString = "";
            for (int colIndex : colIndexes) {
                resultString += row[colIndex].trim().toLowerCase() + "_";
            }
            resultSet.add(resultString);
        }

        return resultSet;
    }

    /**
     * Given a source array of 'array of String' and a starting index in that
     * array, copies <code>count</code> 'array of String' elements into a new
     * array returned to the caller.
     * @param src the source array
     * @param startIndex the source array starting index
     * @param count the number of elements to copy
     * @return the requested elements
     */
    public String[][] copy(String[][] src, int startIndex, int count) {
        if (src == null)
            return null;
        if ((src.length == 0) || (src[0].length == 0))
            return new String[0][0];

        String[][] retVal = new String[src.length - 1][src[0].length];
        for (int i = 0; i < count; i++) {
            retVal[i] = src[i + startIndex];
        }

        return retVal;
    }



    /**
     * Converts a <code>List&lt;List&lt;String&gt;&gt;&gt;</code> to a two-
     * dimensional array of strings.
     *
     * @param list the list to be converted
     *
     * @return a <code>List&lt;List&lt;String&gt;&gt;&gt;</code> to a two-
     * dimensional array of strings.
     */
    public String[][] listToArray(List<List<String>> list) {
        String[][] retVal = new String[list.size()][];

        for (int rowIndex = 0; rowIndex < list.size(); rowIndex++) {
            List<String> row = list.get(rowIndex);
            retVal[rowIndex] = new String[row.size()];
            for (int colIndex = 0; colIndex < row.size(); colIndex++) {
                retVal[rowIndex][colIndex] = row.get(colIndex);
            }
        }

        return retVal;
    }

    /**
     * Given a variable list of strings, returns a single string with each
     * original string separated by an underscore ("_"). Null strings are
     * replaced with an empty string.
     *
     * NOTE: values are trimmed and set to lowercase.
     *
     * @param values the values used to create the resulting string
     *
     * @return a single string with each
     * original string separated by an underscore ("_"). Null strings are
     * replaced with an empty string.
     */
    public String makeKey(String... values) {
        String retVal = "";

        for (String name : values) {
            if ( ! retVal.isEmpty())
                retVal += "_";
            retVal += (name == null ? "" : name.trim().toLowerCase());
        }

        return retVal;
    }

    /**
     * Given a list of ints describing 0-relative offsets into a full set of
     * row values, returns a single string with each value separated by an
     * underscore("_"). Null strings are replaced with an empty string.
     *
     * NOTE: values are trimmed and set to lowercase.
     *
     * @param columnIndexes The 0-relative column indexes for <code>columns
     * </code>.
     *
     * @param values the column data. Null strings are replaced with an empty\
     *                string.
     *
     * @return a single string with each input string separated by
     * an underscore("_"). Null strings are replaced with an empty string.
     *
     * @throws IndexOutOfBoundsException if columnIndexes is out of bounds.
     */
    public String makeKey(int[] columnIndexes, List<String> values) throws IndexOutOfBoundsException {
        String[] retVal = new String[columnIndexes.length];

        for (int i = 0; i < columnIndexes.length; i++) {
            String s = values.get(columnIndexes[i]).trim().toLowerCase();
            retVal[i] = (s == null ? "" : s);
        }

        return makeKey(retVal);
    }

    /**
     * Patches any non-heading <code>input</code> string values that are empty
     * with the string 'No information available', returning a new <code>
     * GridMap</code> instance identical to the input, with empty strings replaced
     * as described.
     *
     * Replaces any empty cells in <code>input</code> with the string 'No information available'.
     *
     * @param dataIn the input collection
     *
     * @return a copy of the input collection, with empty strings replaced with 'No information available'.
     */
    public String[][] patchEmptyFields(String[][] dataIn) {
        String[][] dataOut = new String[dataIn.length][dataIn[0].length];

        for (int rowIndex = 0; rowIndex < dataIn.length; rowIndex++) {
            for (int colIndex = 0; colIndex < dataIn[rowIndex].length; colIndex++) {
                String cellIn = dataIn[rowIndex][colIndex];
                String cellOut = "";
                if (cellIn == null) {
                    cellOut = Constants.NO_INFORMATION_AVAILABLE;
                } else {
                    String[] parts = dataIn[rowIndex][colIndex].split("\\|");
                    if (parts.length == 0) {
                        cellOut = Constants.NO_INFORMATION_AVAILABLE;
                    } else {
                        for (int delimeterIndex = 0; delimeterIndex < parts.length; delimeterIndex++) {
                            if (delimeterIndex > 0)
                                cellOut += "|";
                            String part = parts[delimeterIndex];
                            if ((part == null) || part.trim().isEmpty())
                                cellOut += Constants.NO_INFORMATION_AVAILABLE;
                            else
                                cellOut += part.trim();
                        }
                    }
                }

                dataOut[rowIndex][colIndex] = cellOut;
            }
        }

        return dataOut;
    }

    /**
     * The baseUrl for testing typically looks like:
     *     "http://ves-ebi-d0:8080/mi/impc/dev/phenotype-archive".
     * Typical urls (e.g. graph urls) look like:
     *     "http://ves-ebi-d0:8080/data/charts?accession=MGI:xxx...."
     * Typical tokenMatch for graph pages looks like "/charts?". For download
     * links it looks like "/export?".
     *
     * @param baseUrl the base url
     * @param url the graph (or other page) url
     * @param tokenMatch the token matching the start of the good part of the url.
     * @return a useable url that starts with the baseUrl followed by
     * everything including and after the '/charts?' part of the url.
     */
    public String patchUrl(String baseUrl, String url, String tokenMatch) {
        int idx = url.indexOf(tokenMatch);
        return baseUrl + url.substring(idx);
    }

    /**
     * Given a test name, test start time, error list, exception list, success list,
     * and total number of expected records to be processed, writes the given
     * information to stdout.
     *
     * @param testName the test name (must not be null)
     * @param start the test start time (must not be null)
     * @param status the <code>RunStatus</code> instance
     * @param totalRecords the total number of expected records to process
     * @param totalPossible the total number of possible records to process
     */
    public void printEpilogue(String testName, Date start, RunStatus status, int totalRecords, int totalPossible) {
        DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        System.out.println(dateFormat.format(new Date()) + ": " + testName + " finished.");
        Date stop;

        if (status.hasWarnings()) {
            System.out.println(status.getWarningMessages().size() + " records had warnings:");
            System.out.println(status.toStringWarningMessages());
        }

        if (status.hasErrors()) {
            System.out.println(status.getErrorMessages().size() + " errors:");
            System.out.println(status.toStringErrorMessages());
        }

        stop = new Date();
        String warningClause = (status.hasWarnings() ? " (" + status.getWarningMessages().size() + " warning(s) " : "");
        System.out.println(dateFormat.format(stop) + ": " + status.successCount + " of " + totalRecords + " (total possible: " + totalPossible + ") records successfully processed" + warningClause + " in " + commonUtils.formatDateDifference(start, stop) + ".");
        if (status.hasErrors()) {
            fail("ERRORS: " + status.getErrorMessages().size());
        }
        System.out.println();
    }

    /**
     * Given an initialized <code>WebDriver</code> instance and a selenium URL,
     * prints the test environment for the test associated with <code>driver<code>.
     * @param logger the logger to use
     * @param testClass the test class
     * @param testName the test name
     * @param requestedRecordCount the number of test records requested
     * @param maxRecordCount the maximum number of test records available
     */
    public void logTestStartup(Logger logger, Class testClass, String testName, int requestedRecordCount, int maxRecordCount) {
        DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        String testClassAndName = testClass.getSimpleName() + "." + testName + "() started " + dateFormat.format(new Date());
        String message = "Expecting to process " + requestedRecordCount + " of a total of " + maxRecordCount + " records.";

        System.out.println("####################################################################################################");
        System.out.println("#" + StringUtils.center(testClassAndName, 98, " ") + "#");
        System.out.println("#" + StringUtils.center(message, 98, "") + "#");
        System.out.println("####################################################################################################");
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


    // PRIVATE METHODS


    /**
     * Removes the protocol and double slashes from the url string
     * @param url url string which may or may not contain a protocol
     * @return the url, without the protocol or the double slashes
     */
    public String removeProtocol(String url) {
        return (url.replace("https://", "").replace("http://", ""));
    }

    public enum HTTP_PROTOCOL {
        http
      , https
    };

    /**
     * Sets the protocol (http or https).
     * Examples with HTTP_PROTOCOL = HTTP:
     *      //ves-ebi-d0        -> http://ves-ebi-d0
     *      https://ves-ebi-d0  -> http://ves-ebi-d0
     *      http://ves-ebi-d0   -> http://ves-ebi-d0
     * @param url url string which may or may not contain a protocol
     * @param protocol one of: http or https (choose from enum)
     * @return the url, with the protocol changed, if it exists
     */
    public String setProtocol(String url, HTTP_PROTOCOL protocol) {
        return url.replace("https://", "//")
                  .replace("http://", "//")
                  .replace("//", protocol.name() + "://");
    }

    /**
     * Scrolls <code>element</code> to the top
     * @param driver <code>WebDriver</code> instance
     * @param element Element to scroll to top
     */
    public void scrollToTop(WebDriver driver, WebElement element) {
        scrollToTop(driver, element, null);
    }

    /**
     * There is a selenium bug that silently removes opening parentheses from
     * a sendkeys string. See http://stackoverflow.com/questions/19704559/selenium-sendkeys-not-working-for-open-brackets-and-harsh-keys-when-using-java
     *
     * This is the workaround.
     *
     * @param element <code>WebElement</code> against which to use the sendKeys
     * @param text the text to send (may contain open parenthesis)
     */
    public void seleniumSendKeysHack(WebElement element, String text) {
        char[] chars = text.toCharArray();
        for (char c : chars) {
            if (c == '(') {
                element.sendKeys(Keys.chord(Keys.SHIFT, "9"));
            } else {
                StringBuffer sb = new StringBuffer().append(c);
                element.sendKeys(sb);
            }
        }
    }

    /**
     * Scrolls <code>element</code> to the top
     * @param driver <code>WebDriver</code> instance
     * @param element Element to scroll to top
     * @param yOffsetInPixels An <code>Integer</code> which, if not null and not 0,
     *     first scrolls the element to the top, then further scrolls it <code>
     *     yOffsetInPixels</code> pixels down (if negative number) or up (if
     *     positive).
     */
    public void scrollToTop(WebDriver driver, WebElement element, Integer yOffsetInPixels) {
        Point p = element.getLocation();
        ((JavascriptExecutor)driver).executeScript("arguments[0].scrollIntoView(true);", element);

        if ((yOffsetInPixels != null) && (yOffsetInPixels != 0)) {
            ((JavascriptExecutor)driver).executeScript("window.scroll(" + p.getX() + "," + (p.getY() + yOffsetInPixels) + ");");
        }

        commonUtils.sleep(100);
    }

    /**
     * Sorts <code>delimitedString</code> alphabetically, case-insensitive, first splitting the
     * string into separate segments, delimited by <code>delimiter</code>.
     *
     * @param delimitedString the delimited string to sort
     * @param delimiter the delimiter
     *
     * @return the sorted string
     */
    public String sortDelimitedArray(String delimitedString, String delimiter) {
        if (( delimitedString == null) || (delimitedString.isEmpty()))
            return delimitedString;

        String[] partsArray = delimitedString.split(Pattern.quote(delimiter));
        List<String> partsList = Arrays.asList(partsArray);
        Collections.sort(partsList, String.CASE_INSENSITIVE_ORDER);

        String retVal = "";
        for (String part : partsList) {
            if ( ! retVal.isEmpty()) {
                retVal += delimiter;
            }
            retVal += part;
        }

//        logger.debug("retVal: '" + retVal + "'");

        return retVal;
    }

    /**
     * Sorts the delimited cells in the specified columns of each row in <code>delimitedArray</code> alphabetically,
     * case-insensitive.
     *
     * For example, given:
     *      [0]           [1]           [2]
     *      "abc"         "f|e|d"       "ghi"
     *      "l|k|j"       "klm"         "o|p|n"
     *
     * and a 'columns' specification of [1, 2], the resulting returned array will be:
     *      [0]           [1]           [2]
     *      "abc"         "d|e|f"       "ghi"
     *      "l|k|j"       "klm"         "n|o|p"
     *
     * @param delimitedArray the input data set
     * @param delimiter the delimiter
     * @param columns the list of columns to sort
     *
     * @return the sorted list
     */
    public String[][] sortDelimitedArray(String[][] delimitedArray, String delimiter, List<Integer> columns) {
        if ((delimitedArray == null) || (delimitedArray.length == 0))
            return delimitedArray;

        String[][] retVal = new String[delimitedArray.length][delimitedArray[0].length];
        for (int rowIndex = 0; rowIndex < delimitedArray.length; rowIndex++) {
            String[] row = delimitedArray[rowIndex];
            for (int colIndex = 0; colIndex < row.length; colIndex++) {
                String cell = row[colIndex];
                if (columns.contains(colIndex)) {
                    retVal[rowIndex][colIndex] = sortDelimitedArray(cell, delimiter);
                } else {
                    retVal[rowIndex][colIndex] = cell;
                }
            }
        }

        return retVal;
    }

}