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

package org.mousephenotype.cda.datatests.repositories.solr;

import org.apache.solr.client.solrj.SolrClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mousephenotype.cda.solr.service.dto.ObservationDTOWrite;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {RepositorySolrTestConfig.class})
public class SolrBeanTests {

    @Autowired
    private SolrClient experimentCore;

    String           dateRaw;
    String           dateIso8601;
    SimpleDateFormat formatter;
    Date             expectedDateAsDate;
    ZonedDateTime    expectedDateAsZonedDateTime;

    @Test
    public void testObservationDTOWriteDateOfExperimentWithDate() throws Exception {

        initialiseWithNonNullData();

        ObservationDTOWrite my = new ObservationDTOWrite();
        my.setId("1");
        my.setDateOfExperiment(expectedDateAsDate);
        experimentCore.addBean(my);

        Date actualDateAsDate = my.getDateOfExperimentAsDate();
        assertEquals(expectedDateAsDate, actualDateAsDate);

        ZonedDateTime actualDateAsZonedTimeDate = my.getDateOfExperimentAsZonedDateTime();
        assertEquals(expectedDateAsZonedDateTime, actualDateAsZonedTimeDate);
    }

    @Test
    public void testObservationDTOWriteDateOfExperimentWithZonedTimeDate() throws Exception {

        initialiseWithNonNullData();

        ObservationDTOWrite my = new ObservationDTOWrite();
        my.setId("1");
        my.setDateOfExperiment(expectedDateAsZonedDateTime);
        experimentCore.addBean(my);

        Date actualDateAsDate = my.getDateOfExperimentAsDate();
        assertEquals(expectedDateAsDate, actualDateAsDate);

        ZonedDateTime actualDateAsZonedTimeDate = my.getDateOfExperimentAsZonedDateTime();
        assertEquals(expectedDateAsZonedDateTime, actualDateAsZonedTimeDate);
    }

    @Test
    public void testObservationDTOWriteDateOfExperimentWithNullDate() throws Exception {

        initialiseWithNullData();

        ObservationDTOWrite my = new ObservationDTOWrite();
        my.setId("1");
        my.setDateOfExperiment(expectedDateAsDate);
        experimentCore.addBean(my);

        Date actualDateAsDate = my.getDateOfExperimentAsDate();
        assertEquals(expectedDateAsDate, actualDateAsDate);

        ZonedDateTime actualDateAsZonedTimeDate = my.getDateOfExperimentAsZonedDateTime();
        assertEquals(expectedDateAsZonedDateTime, actualDateAsZonedTimeDate);
    }

    @Test
    public void testObservationDTOWriteDateOfExperimentWithNullZonedTimeDate() throws Exception {

        initialiseWithNullData();

        ObservationDTOWrite my = new ObservationDTOWrite();
        my.setId("1");
        my.setDateOfExperiment(expectedDateAsZonedDateTime);
        experimentCore.addBean(my);

        Date actualDateAsDate = my.getDateOfExperimentAsDate();
        assertEquals(expectedDateAsDate, actualDateAsDate);

        ZonedDateTime actualDateAsZonedTimeDate = my.getDateOfExperimentAsZonedDateTime();
        assertEquals(expectedDateAsZonedDateTime, actualDateAsZonedTimeDate);
    }


    private void initialiseWithNonNullData() throws ParseException {
        dateRaw                     = "2011-12-22 00:00:00";
        dateIso8601                 = "2011-12-22T00:00:00Z";
        formatter                   = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        expectedDateAsDate          = formatter.parse(dateRaw);
        expectedDateAsZonedDateTime = ZonedDateTime.parse(dateIso8601);
    }

    private void initialiseWithNullData() throws ParseException {
        dateRaw                     = null;
        dateIso8601                 = null;
        expectedDateAsDate          = null;
        expectedDateAsZonedDateTime = null;
    }
}