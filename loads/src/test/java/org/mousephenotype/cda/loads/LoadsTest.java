/**
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
/**
 * Copyright © 2014 EMBL - European Bioinformatics Institute
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * This test class is intended to run healthchecks against the observation table.
 */

package org.mousephenotype.cda.loads;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mousephenotype.cda.db.utilities.SqlUtils;
import org.mousephenotype.cda.loads.config.TestConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@TestPropertySource("file:${user.home}/configfiles/${profile:dev}/test.properties")
@SpringBootTest(classes = TestConfig.class)
public class LoadsTest {

    @Autowired
    private JdbcTemplate jdbc1;

    @Autowired
    private JdbcTemplate jdbc2;


    // Test identical results. No difference is expected.
@Ignore
    @Test
    public void testQueryDiffNoDiffsIdentical() throws Exception{
        SqlUtils sqlUtils = new SqlUtils();

        System.out.println("Testing testQueryDiffNoDiffsIdentical");

        List<String[]> actualResults = sqlUtils.queryDiff(jdbc1, jdbc1, "SELECT message FROM test");

        assert(actualResults.isEmpty());
    }


    // Test more results in jdbc2 than jdbc1. No difference is expected.
@Ignore
    @Test
    public void testQueryDiffNoDiffsMoreResultsInJdbc2() throws Exception{
        SqlUtils sqlUtils = new SqlUtils();

        List<String[]> expectedResults = new ArrayList<>();

        System.out.println("Testing testQueryDiffNoDiffsMoreResultsInJdbc2");

        List<String[]> actualResults = sqlUtils.queryDiff(jdbc1, jdbc2, "SELECT message FROM test");

        assert(actualResults.isEmpty());
    }

//    @Test
//    public void testQueryDiffTwoDiffs() throws Exception{
//        SqlUtils sqlUtils = new SqlUtils();
//
//        List<String[]> expectedResults = new ArrayList<>();
//        expectedResults.add(new String[] { "MESSAGE" } );
//        expectedResults.add(new String[] { "dcc line 6" } );
//        expectedResults.add(new String[] { "dcc line 7" } );
//
//        System.out.println("Testing testQueryDiffTwoDiffs");
//
//        List<String[]> actualResults = sqlUtils.queryDiff(jdbc2, jdbc1, "SELECT message FROM test");
//
//        assert(actualResults.size() == 3);
//        for (int i = 0; i < expectedResults.size(); i++) {
//            Assert.assertArrayEquals(expectedResults.get(i), actualResults.get(i));
//        }
//    }







    // Test fewer results in jdbc2 than jdbc1. The extra rows in jdbc1 should be returned.
    @Test
    public void testQueryDiffTwoDiffs() throws Exception{
        SqlUtils sqlUtils = new SqlUtils();

        List<String[]> expectedResults = new ArrayList<>();
        expectedResults.add(new String[] { "MESSAGE" } );
        expectedResults.add(new String[] { "dcc line 6" } );
        expectedResults.add(new String[] { "dcc line 7" } );

        System.out.println("Testing testQueryDiffTwoDiffs");


//        Server server = null;
//        try {
//            server = Server.createTcpServer("-tcpAllowOthers").start();
//            System.out.println("h2 server port = " + server.getPort());
////            server = Server.createTcpServer("-tcpPort", "18082", "-tcpAllowOthers").start();
//            Class.forName("org.h2.Driver");
//            Connection conn = DriverManager.getConnection("jdbc:h2:tcp://localhost/~/dcc1", "sa", "");
//            System.out.println("Connection Established: "
//                                       + conn.getMetaData().getDatabaseProductName() + "/" + conn.getCatalog());
//            String url = server.getURL();
//            url += ";DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=false";
//Server.openBrowser(url);
//            Statement cs = conn.createStatement();
//            cs.execute("SET AUTOCOMMIT OFF ");
//            cs.execute("SET SCHEMA dcc1");
//            ResultSet rs = cs.executeQuery("SELECT * FROM test");
//
//            System.out.println();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//
//
//
//
//
//
//
//
//
//
//
////org.h2.tools.Server.startWebServer(jdbc2.getDataSource().getConnection());
//
//        Connection c = jdbc1.getDataSource().getConnection();
//        System.out.println("catalog:" + c.getCatalog());
//        System.out.println("schema:" + c.getSchema());
//        Properties p = c.getClientInfo();
//
//
//
//        Class.forName("org.h2.Driver");
//        Connection con = DriverManager.getConnection("jdbc:h2:tcp://localhost:8082/~/dcc1");
//        Statement  cs  = con.createStatement();
//        ResultSet  rs  = cs.executeQuery("SELECT * FROM test");
//        DataSource ds  = new SimpleDriverDataSource();


        try {

//            jdbc1.execute("SET TRACE_LEVEL_SYSTEM_OUT 3");
            List<Map<String, Object>> rs = jdbc1.queryForList("DATABASE_NAME()");
            jdbc1.execute("SET SCHEMA dcc1");

            List<Map<String, Object>> rs2 = jdbc1.queryForList("SELECT DATABASE()");
            jdbc1.update("SET SCHEMA dcc1");

            List<Map<String, Object>> rs3 = jdbc1.queryForList("SELECT DATABASE()");



            jdbc1.execute("SELECT message FROM test;");
            System.out.println("OK");
        } catch (Exception e) {
            System.out.println("oops");
        }





//org.h2.tools.Server.startWebServer(jdbc1.getDataSource().getConnection());
//      List<Map<String, Object>> thang =  jdbc2.queryForList("SELECT message FROM dcc2test.test;");

        SqlRowSet rs1 = jdbc1.queryForRowSet("SELECT * FROM test;");


        List<String[]> actualResults = sqlUtils.queryDiff(jdbc2, jdbc1, "SELECT message FROM test");

        assert(actualResults.size() == 3);
        for (int i = 0; i < expectedResults.size(); i++) {
            Assert.assertArrayEquals(expectedResults.get(i), actualResults.get(i));
        }
    }
}
