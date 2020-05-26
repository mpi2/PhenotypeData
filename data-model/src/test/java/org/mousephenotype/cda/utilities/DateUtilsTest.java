/*******************************************************************************
 * Copyright © 2019 EMBL - European Bioinformatics Institute
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

package org.mousephenotype.cda.utilities;

import org.junit.Test;
import org.mousephenotype.cda.common.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class DateUtilsTest {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    String expectedZonedDateTimeString = "2012-12-12T12:12Z[UTC]";

    @Test
    public void testTimeStringParsing() {

        List<String> testDateTimeValues = Arrays.asList(
                "2012-12-12T12:12:00Z",     "2012-12-12T12:12:00-00:00", "2012-12-12T12:12:00-0000", "2012-12-12T12:12:00+00:00",
                "2012-12-12T12:12:00+0000", "2012-12-12T12:12:00Z",      "2012-12-12 12:12:00Z",     "2012-12-12T12:12:00",
                "2012-12-12 12:12:00",      "2012-12-12 12:12");

        for (String testDateTimeValue : testDateTimeValues) {

            logger.debug("Testing Date format: " + testDateTimeValue);
            Date actualDateTime = DateUtils.parseIncrementValue(logger, testDateTimeValue);

            ZonedDateTime actualZonedDateTime = ZonedDateTime.from(actualDateTime.toInstant().atZone(ZoneId.of("UTC")));
            assertEquals(expectedZonedDateTimeString, actualZonedDateTime.toString());
        }
    }

    private class DateExpectedOutput {
        String dateString;
        String expectedDate;

        public DateExpectedOutput(String dateString, String expectedDate) {
            this.dateString = dateString;
            this.expectedDate = expectedDate;
        }
    }

    @Test
    public void testZonedDateTimeParsingNoMilliseconds() {

        List<DateExpectedOutput> inputs = Arrays.asList(
                new DateExpectedOutput("2012-12-12 12:12:00", "2012-12-12T12:12Z[UTC]"),
                new DateExpectedOutput("2012-12-12T12:12:01", "2012-12-12T12:12:01Z[UTC]"),
                new DateExpectedOutput("2012-12-12 12:12:01Z", "2012-12-12T12:12:01Z[UTC]"),
                new DateExpectedOutput("2012-12-12T 12:12:01Z", "2012-12-12T12:12:01Z[UTC]")

        );

        for (DateExpectedOutput input : inputs) {

           logger.debug("Testing Date format: " + input.dateString);

            ZonedDateTime actualDate =
                    ZonedDateTime.parse(input.dateString,
                                        DateTimeFormatter.ofPattern(Constants.DATETIME_FORMAT_OPTIONAL_MILLISECONDS).withZone(ZoneId.of("UTC")));

            assertEquals(input.expectedDate, actualDate.toString());
        }
    }

    @Test
    public void testZonedDateTimeParsingWithMilliseconds() {

        List<DateExpectedOutput> inputs = Arrays.asList(
                new DateExpectedOutput("2012-12-12 12:12:00.123", "2012-12-12T12:12:00.123Z[UTC]"),
                new DateExpectedOutput("2012-12-12T12:12:01.123", "2012-12-12T12:12:01.123Z[UTC]"),
                new DateExpectedOutput("2012-12-12 12:12:01.123Z", "2012-12-12T12:12:01.123Z[UTC]"),
                new DateExpectedOutput("2012-12-12T 12:12:01.123Z", "2012-12-12T12:12:01.123Z[UTC]")
        );

        for (DateExpectedOutput input : inputs) {

            logger.debug("Testing Date format: " + input.dateString);

            ZonedDateTime actualDate =
                    ZonedDateTime.parse(input.dateString,
                                        DateTimeFormatter.ofPattern(Constants.DATETIME_FORMAT_OPTIONAL_MILLISECONDS).withZone(ZoneId.of("UTC")));

            assertEquals(input.expectedDate, actualDate.toString());
        }
    }
}
