package org.mousephenotype.cda.db.repositories;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mousephenotype.cda.db.pojo.MetaInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;


@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {RepositoriesTestConfig.class})
public class MetaInfoRepositoryTest {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ApplicationContext context;

    @Autowired
    private DataSource komp2DataSource;

    @Autowired
    private MetaInfoRepository metaInfoRepository;


    @Before
    public void setUp() throws Exception {

        List<String> resources = Arrays.asList(
                "sql/h2/repositories/org.mousephenotype.cda.db.repositories.MetaInfoRepositoryTest-data.sql"
        );

        for (String resource : resources) {
            Resource r = context.getResource(resource);
            ScriptUtils.executeSqlScript(komp2DataSource.getConnection(), r);
        }
    }


    @Test
    public void findByPropertyKey() throws Exception {

        MetaInfo expectedMetaInfo = new MetaInfo();
        expectedMetaInfo.setId(1L);
        expectedMetaInfo.setPropertyKey("data_release_version");
        expectedMetaInfo.setPropertyValue("10.1");
        expectedMetaInfo.setDescription("Major data release 10.1, released on 03 June 2019, analysed using PhenStat version 2.7.1");

        MetaInfo metaInfo = metaInfoRepository.findByPropertyKey("data_release_version");

        assertEquals(expectedMetaInfo.getId(), metaInfo.getId());
        assertEquals(expectedMetaInfo.getPropertyKey(), metaInfo.getPropertyKey());
        assertEquals(expectedMetaInfo.getPropertyValue(), metaInfo.getPropertyValue());
        assertEquals(expectedMetaInfo.getDescription(), metaInfo.getDescription());
    }
}