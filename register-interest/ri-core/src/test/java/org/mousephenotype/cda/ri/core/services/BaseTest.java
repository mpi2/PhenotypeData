package org.mousephenotype.cda.ri.core.services;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import javax.sql.DataSource;

/**
 * Base test class to aggregate the spring test annotations.
 * All test classes from all modules should extend this base class. Test classes from other modules must declare
 * a dependency on the test jar created from this class:
 *         <dependency>
 *             <groupId>org.mousephenotype.ri</groupId>
 *             <artifactId>core</artifactId>
 *             <version>1.0.0-RELEASE</version>
 *             <classifier>tests</classifier>
 *             <type>test-jar</type>
 *             <scope>test</scope>
 *         </dependency>
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ContextConfiguration(classes = {BaseTestConfig.class})
public abstract class BaseTest {

    @Autowired
    protected ApplicationContext context;

    @Autowired
    protected DataSource riDataSource;

    @Before
    public void setup() throws Exception {

        Resource r = context.getResource("classpath:sql/h2/schema.sql");
        ScriptUtils.executeSqlScript(riDataSource.getConnection(), r);
        r = context.getResource("classpath:sql/h2/base-test-data.sql");
        ScriptUtils.executeSqlScript(riDataSource.getConnection(), r);
    }
}