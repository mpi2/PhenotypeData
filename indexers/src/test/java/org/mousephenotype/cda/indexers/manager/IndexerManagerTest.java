/**
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 * /**
 * Copyright © 2014 EMBL - European Bioinformatics Institute
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.mousephenotype.cda.indexers.manager;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mousephenotype.cda.indexers.IndexerManager;
import org.mousephenotype.cda.indexers.exceptions.IndexerException;
import org.mousephenotype.cda.indexers.exceptions.InvalidCoreNameException;
import org.mousephenotype.cda.indexers.exceptions.MissingRequiredArgumentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.fail;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {IndexerManagerTestConfig.class})
public class IndexerManagerTest {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    IndexerManager indexerManager;

    /***********************************************************************************/
    /*    THE FOLLOWING TESTS GENERATE EXPECTED EXCEPTIONS AND THUS DO NOT BUILD       */
    /*    ANY CORES. THEY ARE INTENDED TO TEST THE SPECIFIED COMMAND-LINE PARAMETERS   */
    /*    FOR INVALID COMMAND-LINE OPTIONS.                                            */
    /***********************************************************************************/


    /**
     * Test invoking static main with no arguments.
     *
     * Expected results: STATUS_NO_ARGUMENT.
     */
    @Test
    public void testStaticNoArgs() throws Exception {
        String testName = "testStaticNoArgs";
        System.out.println("-------------------" + testName + "-------------------");
        System.out.println("Command line = ");
        int retVal = indexerManager.mainReturnsStatus(new String[]{});

        switch (retVal) {
            case IndexerManager.STATUS_NO_ARGUMENT:
                break;

            default:
                fail("Expected STATUS_NO_ARGUMENT");
                break;
        }
    }

    /**
     * Test invoking IndexerManagerInstance with no arguments.
     *
     * Expected results: MissingRequiredArgumentException.
     */
    @Test
    public void testInstanceNoArgs() {
        String testName = "testInstanceNoArgs";
        System.out.println("-------------------" + testName + "-------------------");
        String[] args = new String[]{};
        System.out.println("Command line = " + StringUtils.join(args, ","));

        // Determine which cores to build.
        try {
            indexerManager.initialise(args);
        } catch (IndexerException ie) {
            if (ie.getCause() instanceof MissingRequiredArgumentException) {
                // Do nothing. This is what we expect.
                return;
            }
        }

        fail("Expected MissingRequiredArgumentException");
    }


    /**
     * Test invoking static main with invalid nodeps
     *
     * Expected results: STATUS_NO_ARGUMENT.
     */
    @Test
    public void testStaticNoCoresNodeps() throws Exception {
        String testName = "testStaticNoCoresNodeps";
        System.out.println("-------------------" + testName + "-------------------");
        String args[] = {"--nodeps"};
        System.out.println("Command line = " + StringUtils.join(args, ","));
        int retVal = indexerManager.mainReturnsStatus(args);

        switch (retVal) {
            case IndexerManager.STATUS_NO_ARGUMENT:
                break;

            default:
                fail("Expected STATUS_NO_ARGUMENT");
        }
    }

    /**
     * Test invoking IndexerManager instance with invalid nodeps argument specified
     *
     * Expected results: MissingRequiredArgumentException.
     */
    @Test
    public void testInstanceNoCoresNodeps() {
        String testName = "testInstanceNoCoresNodeps";
        System.out.println("-------------------" + testName + "-------------------");
        String args[] = {"--nodeps"};
        System.out.println("Command line = " + StringUtils.join(args, ","));

        // Determine which cores to build.
        try {
            indexerManager.initialise(args);
        } catch (IndexerException ie) {
            if (ie.getCause() instanceof MissingRequiredArgumentException) {
                // Do nothing. This is the expected exception.
            } else {
                fail("Expected MissingRequiredArgumentException");
            }
        } catch (Exception e) {
            fail("Expected MissingRequiredArgumentException");
        }
    }

    /**
     * Test invoking static main with invalid core name.
     *
     * Expected results: STATUS_INVALID_CORE_NAME.
     */
    @Test
    public void testStaticInvalidCoreName() throws Exception {
        String testName = "testStaticInvalidCoreName";
        System.out.println("-------------------" + testName + "-------------------");
        String args[] = {"--cores=junk"};
        System.out.println("Command line = " + StringUtils.join(args, ","));
        int retVal = indexerManager.mainReturnsStatus(args);

        switch (retVal) {
            case IndexerManager.STATUS_INVALID_CORE_NAME:
                break;

            default:
                fail("Expected STATUS_INVALID_CORE_NAME");
        }
    }

    /**
     * Test invoking static main with invalid core name.
     *
     * Expected results: InvalidCoreNameException.
     */
    @Test
    public void testInstanceInvalidCoreName() {
        String testName = "testInstanceInvalidCoreName";
        System.out.println("-------------------" + testName + "-------------------");
        String args[] = {"--cores=junk"};
        System.out.println("Command line = " + StringUtils.join(args, ","));

        // Determine which cores to build.
        try {
            indexerManager.initialise(args);
        } catch (IndexerException ie) {
            if (ie.getCause() instanceof InvalidCoreNameException) {
                // Do nothing. This is the expected exception.
            } else {
                fail("Expected InvalidCoreNameException");
            }
        } catch (Exception e) {
            fail("Expected InvalidCoreNameException");
        }
    }

    /**
     * Test invoking IndexerManager instance with no 'cores=' argument.
     *
     * Expected results: MissingRequiredArgumentException.
     */
    @Test
    public void testInstanceNoCores() {
        String testName = "testInstanceNoCores";
        System.out.println("-------------------" + testName + "-------------------");
        String[] args = {};
        System.out.println("Command line = " + StringUtils.join(args, ","));

        // Determine which cores to build.
        try {
            indexerManager.initialise(args);
        } catch (IndexerException ie) {
            if (ie.getCause() instanceof MissingRequiredArgumentException) {
                // Do nothing. This is the expected exception.
            } else {
                fail("Expected MissingRequiredArgumentException");
            }
        } catch (Exception e) {
            fail("Expected MissingRequiredArgumentException");
        }
    }

    /**
     * Test invoking IndexerManager instance with empty 'cores=' argument.
     *
     * Expected results: MissingRequiredArgumentException.
     */
    @Test
    public void testInstanceEmptyCoresNoEquals() {
        String testName = "testInstanceEmptyCoresNoEquals";
        System.out.println("-------------------" + testName + "-------------------");
        String[] args = new String[]{"--cores"};
        System.out.println("Command line = " + StringUtils.join(args, ","));

        // Determine which cores to build.
        try {
            indexerManager.initialise(args);
        } catch (IndexerException ie) {
            if (ie.getCause() instanceof MissingRequiredArgumentException) {
                // Do nothing. This is the expected exception.
            } else {
                fail("Expected MissingRequiredArgumentException");
            }
        } catch (Exception e) {
            fail("Expected MissingRequiredArgumentException");
        }
    }

    /**
     * Test invoking IndexerManager instance with empty 'cores=' argument.
     *
     * Expected results: MissingRequiredArgumentException.
     */
    @Test
    public void testInstanceEmptyCores() {
        String testName = "testInstanceEmptyCores";
        System.out.println("-------------------" + testName + "-------------------");
        String[] args = new String[]{"--cores="};
        System.out.println("Command line = " + StringUtils.join(args, ","));

        // Determine which cores to build.
        try {
            indexerManager.initialise(args);
        } catch (IndexerException ie) {
            if (ie.getCause() instanceof MissingRequiredArgumentException) {
                // Do nothing. This is the expected exception.
            } else {
                fail("Expected MissingRequiredArgumentException");
            }
        } catch (Exception e) {
            fail("Expected MissingRequiredArgumentException");
        }
    }

    /**
     * Test invoking IndexerManager instance with empty 'cores=' argument, --nodeps BEFORE --cores.
     *
     * Expected results: MissingRequiredArgumentException.
     */
    @Test
    public void testInstanceEmptyCoresNoEqualsNodepsBeforeCores() {
        String testName = "testInstanceEmptyCoresNoEqualsNodepsBeforeCores";
        System.out.println("-------------------" + testName + "-------------------");
        String[] args = new String[]{"--nodeps", "--cores"};
        System.out.println("Command line = " + StringUtils.join(args, ","));

        // Determine which cores to build.
        try {
            indexerManager.initialise(args);
        } catch (IndexerException ie) {
            if (ie.getCause() instanceof MissingRequiredArgumentException) {
                // Do nothing. This is the expected exception.
            } else {
                fail(ie.getLocalizedMessage());
            }
        } catch (Exception e) {
            fail(e.getLocalizedMessage());
        }
    }

    /**
     * Test invoking IndexerManager instance with empty 'cores=' argument, --nodeps BEFORE --cores.
     *
     * Expected results: MissingRequiredArgumentException.
     */
    @Test
    public void testInstanceEmptyCoresNodepsBeforeCores() {
        String testName = "testInstanceEmptyCoresNodepsBeforeCores";
        System.out.println("-------------------" + testName + "-------------------");
        String[] args = new String[]{"--nodeps", "--cores="};
        System.out.println("Command line = " + StringUtils.join(args, ","));

        // Determine which cores to build.
        try {
            indexerManager.initialise(args);
        } catch (IndexerException ie) {
            if (ie.getCause() instanceof MissingRequiredArgumentException) {
                // Do nothing. This is the expected exception.
            } else {
                fail("Expected MissingRequiredArgumentException");
            }
        } catch (Exception e) {
            fail("Expected MissingRequiredArgumentException");
        }
    }

    /**
     * Test invoking static main with --all and --cores=ma
     *
     * Expected results: STATUS_VALIDATION_ERROR.
     */
    @Test
    public void testStaticAllAndCores() throws Exception {
        String testName = "testStaticAllAndCores";
        System.out.println("-------------------" + testName + "-------------------");
        String[] args = {"--all", "--cores=ma"};
        System.out.println("Command line = " + StringUtils.join(args, ","));
        int retVal = indexerManager.mainReturnsStatus(args);

        switch (retVal) {
            case IndexerManager.STATUS_VALIDATION_ERROR:
                break;

            default:
                fail("Expected STATUS_VALIDATION_ERROR");
                break;
        }
    }

    /**
     * Test invoking static main with --all and --nodeps
     *
     * Expected results: STATUS_VALIDATION_ERROR.
     */
    @Test
    public void testStaticAllAndNodeps() throws Exception {
        String testName = "testStaticAllAndNodeps";
        System.out.println("-------------------" + testName + "-------------------");
        String[] args = {"--all", "--nodeps"};
        System.out.println("Command line = " + StringUtils.join(args, ","));
        int retVal = indexerManager.mainReturnsStatus(args);

        switch (retVal) {
            case IndexerManager.STATUS_VALIDATION_ERROR:
                break;

            default:
                fail("Expected STATUS_VALIDATION_ERROR");
                break;
        }
    }

    /**
     * Test invoking static main with --all and --nodeps
     *
     * Expected results: STATUS_VALIDATION_ERROR.
     */
    @Test
    public void testStaticDailyAndNodeps() throws Exception {
        String testName = "testStaticDailyAndNodeps";
        System.out.println("-------------------" + testName + "-------------------");
        String[] args = {"--daily", "--nodeps"};
        System.out.println("Command line = " + StringUtils.join(args, ","));
        int retVal = indexerManager.mainReturnsStatus(args);

        switch (retVal) {
            case IndexerManager.STATUS_VALIDATION_ERROR:
                break;

            default:
                fail("Expected STATUS_VALIDATION_ERROR");
                break;
        }
    }

    /**
     * Test invoking static main with --all and --cores=ma
     *
     * Expected results: STATUS_VALIDATION_ERROR.
     */
    @Test
    public void testStaticDailyAndCores() throws Exception {
        String testName = "testStaticDailyAndCores";
        System.out.println("-------------------" + testName + "-------------------");
        String[] args = {"--daily", "--cores=ma"};
        System.out.println("Command line = " + StringUtils.join(args, ","));
        int retVal = indexerManager.mainReturnsStatus(args);

        switch (retVal) {
            case IndexerManager.STATUS_VALIDATION_ERROR:
                break;

            default:
                fail("Expected STATUS_VALIDATION_ERROR");
                break;
        }
    }

    /**
     * Test invoking static main with --all and --cores=ma
     *
     * Expected results: STATUS_VALIDATION_ERROR.
     */
    @Test
    public void testStaticAllAndDaily() throws Exception {
        String testName = "testStaticAllAndDaily";
        System.out.println("-------------------" + testName + "-------------------");
        String[] args = {"--all", "--daily"};
        System.out.println("Command line = " + StringUtils.join(args, ","));
        int retVal = indexerManager.mainReturnsStatus(args);

        switch (retVal) {
            case IndexerManager.STATUS_VALIDATION_ERROR:
                break;

            default:
                fail("Expected STATUS_VALIDATION_ERROR");
                break;
        }
    }

    /**
     * Test invoking static main with --all and --nodeps
     *
     * Expected results: STATUS_VALIDATION_ERROR.
     */
    @Test
    public void testStaticAllAndDailyAndNodeps() throws Exception {
        String testName = "testStaticAllAndDailyAndNodeps";
        System.out.println("-------------------" + testName + "-------------------");
        String[] args = {"--all", "--daily", "--nodeps"};
        System.out.println("Command line = " + StringUtils.join(args, ","));
        int retVal = indexerManager.mainReturnsStatus(args);

        switch (retVal) {
            case IndexerManager.STATUS_VALIDATION_ERROR:
                break;

            default:
                fail("Expected STATUS_VALIDATION_ERROR");
                break;
        }
    }

    /************************************************************************************************/
    /*    THE FOLLOWING TESTS ARE NOT EXPECTED TO GENERATE EXCEPTIONS; THUS THEY CAN                */
    /*    BUILD CORES. SINCE IT IS NOT THE JOB OF THE TESTS TO BUILD THE CORES, ONLY                */
    /*    THE initialise() METHOD IS RUN; THE run() METHOD THAT ACTUALLY BUILDS THE CORES           */
    /*    IS NOT RUN. THESE THEY ARE INTENDED TO TEST THE SPECIFIED COMMAND-LINE PARAMETERS         */
    /*    FOR COMMAND-LINE OPTIONS AND TO TEST THAT ONLY THE EXPECTED CORES WOULD BE BUILT.         */
    /*    testStaticXxx VERSIONS OF THESE TESTS CANNOT BE RUN BECAUSE THE run() METHOD IS ALWAYS    */
    /*    CALLED AUTOMATICALLY, THERE BEING NO WAY TO SUPPRESS IT.                                  */
    /************************************************************************************************/


    /**
     * Test invoking IndexerManager instance starting at the first core (the
     * observation core).
     *
     * Expected results: cores observation to autosuggest ready to run.
     */
    @Test
    public void testInstanceFirstAllCore() {
        String testName = "testInstanceFirstCore";
        System.out.println("-------------------" + testName + "-------------------");
        String[] args = new String[]{"--cores=pipeline"};
        System.out.println("Command line = " + StringUtils.join(args, ","));

        // Determine which cores to build.
        try {
            indexerManager.initialise(args);
        } catch (IndexerException ie) {
            fail(ie.getLocalizedMessage());
        }

        String[] actualCores = indexerManager.getCores().toArray(new String[0]);
        assertArrayEquals(IndexerManager.allCoresArray, actualCores);
    }

    /**
     * Test invoking IndexerManager instance starting at the first core (the
     * observation core), using the nodeps option.
     *
     * Expected results: the single observation core, ready to run.
     */
    @Test
    public void testInstanceFirstCoreNodeps() {
        String testName = "testInstanceFirstCoreNodeps";
        System.out.println("-------------------" + testName + "-------------------");
        String[] args = new String[]{"--cores=experiment", "--nodeps"};
        System.out.println("Command line = " + StringUtils.join(args, ","));

        // Determine which cores to build.
        try {
            indexerManager.initialise(args);
        } catch (IndexerException ie) {
            fail(ie.getLocalizedMessage());
        }

        String[] actualCores   = indexerManager.getCores().toArray(new String[0]);
        String[] expectedCores = new String[]{IndexerManager.OBSERVATION_CORE};
        assertArrayEquals(expectedCores, actualCores);
    }

    /**
     * Test invoking IndexerManager instance starting at the first daily core
     * (the preqc core).
     *
     * Expected results: All of the cores from preqc to autosuggest, ready to run.
     */
    @Test
    public void testInstanceFirstDailyCore() {
        String testName = "testInstanceFirstDailyCore";
        System.out.println("-------------------" + testName + "-------------------");

        String[] args = new String[]{"--cores=allele2"};
        System.out.println("Command line = " + StringUtils.join(args, ","));

        // Determine which cores to build.
        try {
            indexerManager.initialise(args);
        } catch (IndexerException ie) {
            fail(ie.getLocalizedMessage());
        }

        String[] actualCores = indexerManager.getCores().toArray(new String[0]);
        assertArrayEquals(IndexerManager.dailyCoresArray, actualCores);
    }

    /**
     * Test invoking IndexerManager instance starting at the first daily core
     * (the preqc core), using the nodeps option.
     *
     * Expected results: the single preqc core, ready to run.
     */
    @Test
    public void testInstanceFirstDailyCoreNodeps() {
        String testName = "testInstanceFirstDailyCoreNodeps";
        System.out.println("-------------------" + testName + "-------------------");
        String[] args = new String[]{"--cores=allele2", "--nodeps"};
        System.out.println("Command line = " + StringUtils.join(args, ","));

        // Determine which cores to build.
        try {
            indexerManager.initialise(args);
        } catch (IndexerException ie) {
            fail(ie.getLocalizedMessage());
        }

        String[] actualCores   = indexerManager.getCores().toArray(new String[0]);
        String[] expectedCores = new String[]{IndexerManager.ALLELE2_CORE};
        assertArrayEquals(expectedCores, actualCores);
    }

    /**
     * Test invoking IndexerManager instance starting at the last core (the
     * autosuggest core).
     *
     * Expected results: the single autosuggest core, ready to run.
     */
    @Test
    public void testInstanceLastCore() {
        String testName = "testInstanceLastCore";
        System.out.println("-------------------" + testName + "-------------------");
        String[] args = new String[]{"--cores=autosuggest"};
        System.out.println("Command line = " + StringUtils.join(args, ","));

        // Determine which cores to build.
        try {
            indexerManager.initialise(args);
        } catch (IndexerException ie) {
            fail(ie.getLocalizedMessage());
        }

        String[] actualCores   = indexerManager.getCores().toArray(new String[0]);
        String[] expectedCores = new String[]{IndexerManager.AUTOSUGGEST_CORE};
        assertArrayEquals(expectedCores, actualCores);
    }

    /**
     * Test invoking IndexerManager instance starting at the last core (the
     * autosuggest core), using the nodeps option.
     *
     * Expected results: the single autosuggest core, ready to run.
     */
    @Test
    public void testInstanceLastCoreNodeps() {
        String testName = "testInstanceLastCoreNodeps";
        System.out.println("-------------------" + testName + "-------------------");
        String[] args = new String[]{"--cores=autosuggest", "--nodeps"};
        System.out.println("Command line = " + StringUtils.join(args, ","));

        // Determine which cores to build.
        try {
            indexerManager.initialise(args);
        } catch (IndexerException ie) {
            fail(ie.getLocalizedMessage());
        }

        String[] actualCores   = indexerManager.getCores().toArray(new String[0]);
        String[] expectedCores = new String[]{IndexerManager.AUTOSUGGEST_CORE};
        assertArrayEquals(expectedCores, actualCores);
    }

    /**
     * Test invoking IndexerManager instance specifying specific cores
     *
     * Expected results: the specified cores, ready to run.
     */
    @Test
    public void testInstanceMultipleCores() {
        String testName = "testInstanceMultipleCores";
        System.out.println("-------------------" + testName + "-------------------");
        String[] args = new String[]{"--cores=pipeline,allele,impc_images,anatomy,mp"};
        System.out.println("Command line = " + StringUtils.join(args, ","));

        // Determine which cores to build.
        try {
            indexerManager.initialise(args);
        } catch (IndexerException ie) {
            fail(ie.getLocalizedMessage());
        }

        String[] actualCores = indexerManager.getCores().toArray(new String[0]);
        String[] expectedCores = new String[]{
                IndexerManager.PIPELINE_CORE
                , IndexerManager.ALLELE_CORE
                , IndexerManager.IMPC_IMAGES_CORE
                , IndexerManager.ANATOMY_CORE
                , IndexerManager.MP_CORE
        };
        assertArrayEquals(expectedCores, actualCores);
    }

    /**
     * Test invoking IndexerManager instance specifying specific cores), using
     * the nodeps option.
     *
     * Expected results: the specified cores, ready to run.
     */
    @Test
    public void testInstanceMultipleCoresNodeps() {
        String testName = "testInstanceMultipleCoresNodeps";
        System.out.println("-------------------" + testName + "-------------------");
        String[] args = new String[]{"--cores=pipeline,allele,impc_images,anatomy,mp", "--nodeps"};
        System.out.println("Command line = " + StringUtils.join(args, ","));

        // Determine which cores to build.
        try {
            indexerManager.initialise(args);
        } catch (IndexerException ie) {
            fail(ie.getLocalizedMessage());
        }

        String[] actualCores = indexerManager.getCores().toArray(new String[0]);
        String[] expectedCores = new String[]{
                IndexerManager.PIPELINE_CORE
                , IndexerManager.ALLELE_CORE
                , IndexerManager.IMPC_IMAGES_CORE
                , IndexerManager.ANATOMY_CORE
                , IndexerManager.MP_CORE
        };
        assertArrayEquals(expectedCores, actualCores);
    }

    /**
     * Test invoking IndexerManager instance  using the --all argument.
     *
     * Expected results: cores observation to autosuggest ready to run.
     */
    @Test
    public void testInstanceAll() {
        String testName = "testInstanceAll";
        System.out.println("-------------------" + testName + "-------------------");
        String[] args = new String[]{"--all"};
        System.out.println("Command line = " + StringUtils.join(args, ","));

        // Determine which cores to build.
        try {
            indexerManager.initialise(args);
        } catch (IndexerException ie) {
            fail(ie.getLocalizedMessage());
        }

        String[] actualCores = indexerManager.getCores().toArray(new String[0]);
        assertArrayEquals(IndexerManager.allCoresArray, actualCores);
    }

    /**
     * Test invoking IndexerManager instance  using the --daily argument.
     *
     * Expected results: cores preqc to autosuggest ready to run.
     */
    @Test
    public void testInstanceDaily() {
        String testName = "testInstanceDaily";
        System.out.println("-------------------" + testName + "-------------------");
        String[] args = new String[]{"--daily"};
        System.out.println("Command line = " + StringUtils.join(args, ","));

        // Determine which cores to build.
        try {
            indexerManager.initialise(args);
        } catch (IndexerException ie) {
            fail(ie.getLocalizedMessage());
        }

        String[] actualCores = indexerManager.getCores().toArray(new String[0]);
        assertArrayEquals(IndexerManager.dailyCoresArray, actualCores);
    }
}