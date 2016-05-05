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

package org.mousephenotype.cda.loads.cdaloader.step;

import org.apache.commons.lang3.StringUtils;
import org.mousephenotype.cda.loads.cdaloader.exception.CdaLoaderException;
import org.mousephenotype.cda.utilities.CommonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.step.tasklet.SystemCommandTasklet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.Date;

/**
 * Created by mrelac on 13/04/2016.
 */
@Component
public class RecreateAndLoadDbTables extends SystemCommandTasklet {

    @Value("${cdaload.workspace}")
    private String cdaWorkspace;

    CommonUtils commonUtils = new CommonUtils();
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    DataSource komp2loads;

    @Value("${cdaload.dbname}")
    private String dbname;

    @Value("${cdaload.username}")
    private String dbusername;

    @Value("${cdaload.password}")
    private String dbpassword;

    @Value("${cdaload.mysql}")
    private String mysql;

    @Value("${cdaload.dbhostname}")
    private String dbhostname;

    @Value("${cdaload.dbport}")
    private String dbport;

    public final long TASKLET_TIMEOUT = 10000;                                  // Timeout in milliseconds


    public SystemCommandTasklet recreateAndLoadDbTables() throws CdaLoaderException {
        SystemCommandTasklet downloadReportsTasklet;

        long startStep = new Date().getTime();

        downloadReportsTasklet = new SystemCommandTasklet();

        ClassLoader classloader = getClass().getClassLoader();
        String filename = classloader.getResource("scripts/schema.sql").getPath();

        String[] commands = new String[] { "/bin/sh", "-c", mysql + " --host=" + dbhostname + " --port=" + dbport + " --user=" + dbusername + " --password=" + dbpassword + " " + dbname + " < " + filename };

        try {
            System.out.println("cmd = " + StringUtils.join(commands, " "));
            Process p = Runtime.getRuntime().exec(commands);
            int exitVal = p.waitFor();
            System.out.println("exitVal = " + exitVal);
        }

        catch(IOException | InterruptedException e) {
            System.out.println("FAIL: " + e.getLocalizedMessage());
        }

        // A SystemCommandTasklet needs something to execute or it throws an exception. This is a do-nothing command to satisfy that requirement.
        String command = "ls";
        downloadReportsTasklet.setCommand(command);
        downloadReportsTasklet.setTimeout(TASKLET_TIMEOUT);
        downloadReportsTasklet.setWorkingDirectory(cdaWorkspace);

        logger.info("Total step elapsed time: " + commonUtils.msToHms(new Date().getTime() - startStep));

        return downloadReportsTasklet;
    }
}