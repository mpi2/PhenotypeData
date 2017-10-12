/*******************************************************************************
 * Copyright © 2015 EMBL - European Bioinformatics Institute
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

package org.mousephenotype.cda.threei.create;

import org.slf4j.LoggerFactory;

import java.util.*;
import java.text.DecimalFormat;

import java.sql.Date;

/**
 * Created by kolab on 04/10/2017 - base class for writing ANA data to XML based on ExtractDccSpecimens.
 * <p/>
 * This class is a base class that encapsulates the generic code and data 
 * necessary to convert an XLS (Excel) report provided by Sanger/KCL to the XML
 * format required by ExtractDccSpecimens and ExtractDccExperiments. It is
 * based on loads/ExtractDccSpecimens by Mike Relac.
 * This class is meant to be an executable jar
 */
public abstract class CreateAnaXmls {

    protected static final Map<String, Integer> monthToNumber;
    static
    {
        monthToNumber = new HashMap<String, Integer>();
        monthToNumber.put("Jan", 0);
        monthToNumber.put("Feb", 1);
        monthToNumber.put("Mar", 2);
        monthToNumber.put("Apr", 3);
        monthToNumber.put("May", 4);
        monthToNumber.put("Jun", 5);
        monthToNumber.put("Jul", 6);
        monthToNumber.put("Aug", 7);
        monthToNumber.put("Sep", 8);
        monthToNumber.put("Oct", 9);
        monthToNumber.put("Nov", 10);
        monthToNumber.put("Dec", 11);
    }
    protected final org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());

    // Required by the Harwell DCC export utilities
    public static final  String CONTEXT_PATH = "org.mousephenotype.dcc.exportlibrary.datastructure.core.common:org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure:org.mousephenotype.dcc.exportlibrary.datastructure.core.specimen:org.mousephenotype.dcc.exportlibrary.datastructure.tracker.submission:org.mousephenotype.dcc.exportlibrary.datastructure.tracker.validation";

    // Convert a string representing a double to a string representing an
    // integer
    protected String integerFormat(String strNumber) {
        try{
            DecimalFormat formatter = new DecimalFormat("#");
            double dblNumber = Double.parseDouble(strNumber);
            return "" + formatter.format(dblNumber);
        } catch (Exception e) {
            logger.warn("Problem creating integer format: " + e.getMessage());
            return "";
        }
    }
    
    // Convert a string representing a date in the format 
    // to a GregorianCalendar object
    protected GregorianCalendar gcDate(String date) {

        String[] arrDate = date.split(" ");
        if (arrDate.length != 6) {
            return null;
        }
        try {
            int year = Integer.parseInt(arrDate[5]);
            int month = monthToNumber.get(arrDate[1]);
            int day = Integer.parseInt(arrDate[2]);
            return new GregorianCalendar(year, month, day);
        } catch (Exception e) {
            logger.warn("Problem creating GregorianCalendar date: " + e.getMessage());
            return null;
        }
    }
}
