package org.mousephenotype.cda.db.repositories;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mousephenotype.cda.db.pojo.MetaHistory;
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
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {RepositoriesTestConfig.class})
public class MetaHistoryRepositoryTest {

    @Autowired
    private ApplicationContext context;

    @Autowired
    private DataSource komp2DataSource;

    @Autowired
    MetaHistoryRepository metaHistoryRepository;

    @Before
    public void setUp() throws Exception {

        List<String> resources = Arrays.asList(
                "h2/schema.sql",
                "h2/repository/MetaHistoryRepositoryTest-data.sql"
        );

        for (String resource : resources) {
            Resource r = context.getResource(resource);
            ScriptUtils.executeSqlScript(komp2DataSource.getConnection(), r);
        }
    }

    @Test
    public void getAllDataReleaseVersionsCastAsc() {
        List<String> actual = metaHistoryRepository.getAllDataReleaseVersionsCastAsc();
        assertNotNull(actual);
        assertEquals(5, actual.size());
        assertEquals("1.0", actual.get(0));
        assertEquals("1.1", actual.get(1));
        assertEquals("2.0", actual.get(2));
        assertEquals("3.4", actual.get(3));
        assertEquals("10.0", actual.get(4));
    }

    @Test
    public void getAllDataReleaseVersionsCastDesc() {
        List<String> actual = metaHistoryRepository.getAllDataReleaseVersionsCastDesc();
        assertNotNull(actual);
        assertEquals(5, actual.size());
        assertEquals("10.0", actual.get(0));
        assertEquals("3.4", actual.get(1));
        assertEquals("2.0", actual.get(2));
        assertEquals("1.1", actual.get(3));
        assertEquals("1.0", actual.get(4));
    }

    @Test
    public void getAllDataReleaseVersionsBeforeSpecified() {
        List<String> actual = metaHistoryRepository.getAllDataReleaseVersionsBeforeSpecified("2.0");
        assertNotNull(actual);
        assertEquals(2, actual.size());
        assertEquals("1.1", actual.get(0));
        assertEquals("1.0", actual.get(1));

        actual = metaHistoryRepository.getAllDataReleaseVersionsBeforeSpecified("10.0");
        assertNotNull(actual);
        assertEquals(4, actual.size());
        assertEquals("3.4", actual.get(0));
        assertEquals("2.0", actual.get(1));
        assertEquals("1.1", actual.get(2));
        assertEquals("1.0", actual.get(3));

        actual = metaHistoryRepository.getAllDataReleaseVersionsBeforeSpecified("1.0");
        assertNotNull(actual);
        assertEquals(0, actual.size());
    }

    @Test
    public void getAllByPropertyKeyCastAsc() {
        List<MetaHistory> expected = Arrays.asList(
                new MetaHistory(640L,  "data_release_version", "1.0", "1.0"),
                new MetaHistory(1403L, "data_release_version", "3.4", "3.4"),
                new MetaHistory(2893L, "data_release_version", "10.1", "10.0")
        );
        List<MetaHistory> actual = metaHistoryRepository.getAllByPropertyKeyCastAsc("data_release_version");
        assertNotNull(actual);
        assertEquals(3, actual.size());
        for (int i = 0; i < actual.size(); i++) {
            assertEquals(expected.get(i), actual.get(i));
        }
    }
}