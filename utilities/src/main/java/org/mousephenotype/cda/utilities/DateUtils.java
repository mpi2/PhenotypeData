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

package org.mousephenotype.cda.utilities;


import org.slf4j.Logger;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by mrelac on 22/05/2017.
 */
public class DateUtils {

    /**
     * Given two dates (in any order), returns a <code>String</code> in the
     * format "xxx days, yyy hours, zzz minutes, nnn seconds" that equals
     * the absolute value of the time difference between the two days.
     * @param date1 the first operand
     * @param date2 the second operand
     * @return a <code>String</code> in the format "dd:hh:mm:ss" that equals the
     * absolute value of the time difference between the two date.
     */
    public String formatDateDifference(Date date1, Date date2) {
        long lower = Math.min(date1.getTime(), date2.getTime());
        long upper = Math.max(date1.getTime(), date2.getTime());
        long diff = upper - lower;

        long days = diff / (24 * 60 * 60 * 1000);
        long hours = diff / (60 * 60 * 1000) % 24;
        long minutes = diff / (60 * 1000) % 60;
        long seconds = diff / 1000 % 60;

        return String.format("%02d:%02d:%02d:%02d", days, hours, minutes, seconds);
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

    public Date convertToDate(String dateString) {
        Date date = null;

        SimpleDateFormat sdf = new SimpleDateFormat("y-M-d H:m:s.S");
        if ((dateString != null) && ( ! dateString.trim().isEmpty())) {
            date = tryParseDate(sdf, dateString.trim());
            if (date == null) {
                throw new RuntimeException("Invalid date: '" + dateString.trim() + "'");
            }
        }

        return date;
    }

    /**
     * Given a base (starting) date and a time to live, in minutes, returns true if the current time is after the
     * baseDate time + timeToLiveInMinutes; false otherwise
     *
     * @param baseDate base (start) time
     * @param timeToLiveInMinutes the time that gets added to baseDate to create the expiry date
     * @return
     */
    public boolean isExpired(Date baseDate, int timeToLiveInMinutes) {
        Calendar now        = Calendar.getInstance();
        Calendar expiryTime = Calendar.getInstance();

        expiryTime.setTime(baseDate);
        expiryTime.add(Calendar.MINUTE, timeToLiveInMinutes);

        return now.after(expiryTime);
    }

    /**
     * Given a <code>SimpleDateFormat</code> instance that may be null or may describe a date input format string, this
     * method attempts to convert the value to a <code>Date</code>. If successful,
     * the <code>Date</code> instance is returned; otherwise, <code>null</code> is returned.
     * NOTE: the [non-null] object is first converted to a string and is trimmed of whitespace.
     * @param formatter a <code>SimpleDateFormat</code> instance describing the input string date format
     * @param value the <code>String</code> representation, matching <code>formatter></code> to try to convert
     * @return If <code>value</code> is a valid date as described by <code>formatter</code>; null otherwise
     */
    public static Date tryParseDate(SimpleDateFormat formatter, String value) {
        if (formatter == null)
            return null;

        Date retVal = null;
        try {
            retVal = formatter.parse(value.trim());
        }
        catch (ParseException pe ) { }

        return retVal;
    }

    /**
     * Method to parse various increment value date time formats
     * Supported formats:
     * 2012-12-12T12:12:12+00:00
     * 2012-12-12T12:12:12+0000
     * 2012-12-12T12:12:12Z
     * 2012-12-12 12:12:12Z
     * 2012-12-12T12:12:12
     * 2012-12-12 12:12:12
     * <p/>
     * Unsuccessful parse returns null
     *
     * @param logger
     * @param value date, or null if parse unsuccessful
     * @return
     */
    public static Date parseIncrementValue(Logger logger, String value) {
        Date                   d                = null;
        List<SimpleDateFormat> supportedFormats = new ArrayList<>();
        supportedFormats.add(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX"));
        supportedFormats.add(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXX"));
        supportedFormats.add(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX"));
        supportedFormats.add(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ"));
        supportedFormats.add(new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ssZ"));
        supportedFormats.add(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'"));
        supportedFormats.add(new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss'Z'"));
        supportedFormats.add(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"));
        supportedFormats.add(new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss"));
        supportedFormats.add(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm"));
        supportedFormats.add(new SimpleDateFormat("yyyy-MM-dd' 'HH:mm"));

        for (SimpleDateFormat format : supportedFormats) {
            try {
                logger.debug("Testing format: {}", format.toPattern());
                d = format.parse(value);
            } catch (ParseException e) {
                // Not this format, try the next one
                continue;
            }
            // If the parse is successful, stop processing the rest
            logger.debug("Parsed datestring {} using format {}: {}", value, format.toPattern(), d.toString());

            break;
        }

        return d;
    }
}