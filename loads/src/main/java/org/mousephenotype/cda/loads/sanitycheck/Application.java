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

package org.mousephenotype.cda.loads.sanitycheck;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Created by mrelac on 19/07/2016.
 *
 * Usage:   java -jar loads-1.0.0-exec.jar org.mousephenotype/cda/loads/sanitycheck/Application --profile=shanti -DskipTests
 *
 * In the 'shanti' properties file (in ~/configfiles/shanti/application.properties), specify the following token lvalues:

# dccloader1 is meant to be the old database.
datasource.dccloader1.url=jdbc:mysql://mysql-mi-dev:4356/dccimportImpc_4_3?useSSL=false&autoReconnect=true&amp;useUnicode=true&amp;connectionCollation=utf8_general_ci&amp;characterEncoding=utf8&amp;characterSetResults=utf8&amp;zeroDateTimeBehavior=convertToNull
datasource.dccloader1.username=xxxxxxxx
datasource.dccloader1.password=xxxxxxxx

# dccloader2 is meant to be the new database.
datasource.dccloader2.url=jdbc:mysql://mysql-mi-dev:4356/dccimportImpc_4_4?useSSL=false&autoReconnect=true&amp;useUnicode=true&amp;connectionCollation=utf8_general_ci&amp;characterEncoding=utf8&amp;characterSetResults=utf8&amp;zeroDateTimeBehavior=convertToNull
datasource.dccloader2.username=xxxxxxxx
datasource.dccloader2.password=xxxxxxxx

 */
@SpringBootApplication
public class Application {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(Application.class, args);
    }
}
