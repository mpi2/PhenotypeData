package org.mousephenotype.cda.ri.services;

import org.junit.jupiter.api.BeforeEach;
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
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ContextConfiguration(classes = {org.mousephenotype.cda.ri.services.BaseTestConfig.class})
public abstract class BaseTest {

    @Autowired
    protected ApplicationContext context;

    @Autowired
    protected DataSource riDataSource;

    @BeforeEach
    public void setup() throws Exception {
        Resource r = context.getResource("classpath:sql/h2/schema.sql");
        ScriptUtils.executeSqlScript(riDataSource.getConnection(), r);
        r = context.getResource("classpath:sql/h2/base-test-data.sql");
        ScriptUtils.executeSqlScript(riDataSource.getConnection(), r);
    }
}