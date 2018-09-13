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

package org.mousephenotype.cda.ri.core.utils;


import org.mousephenotype.cda.ri.core.exceptions.InterestException;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by mrelac on 22/05/2017.
 */
public class DateUtils {

    public ParseUtils parseUtils = new ParseUtils();

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

    public Date convertToDate(String dateString) throws InterestException {
        Date date = null;

        SimpleDateFormat sdf = new SimpleDateFormat("y-M-d H:m:s.S");
        if ((dateString != null) && ( ! dateString.trim().isEmpty())) {
            date = parseUtils.tryParseDate(sdf, dateString.trim());
            if (date == null) {
                throw new InterestException("Invalid date: '" + dateString.trim() + "'");
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
}