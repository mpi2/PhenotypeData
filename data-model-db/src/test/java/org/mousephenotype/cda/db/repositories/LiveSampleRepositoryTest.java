package org.mousephenotype.cda.db.repositories;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mousephenotype.cda.db.pojo.LiveSample;
import org.mousephenotype.cda.db.pojo.OntologyTerm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import javax.sql.DataSource;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {RepositoriesTestConfig.class})
public class LiveSampleRepositoryTest {

    @Autowired
    private ApplicationContext context;

    @Autowired
    private DataSource komp2DataSource;

    @Autowired
    LiveSampleRepository liveSampleRepository;

    @Before
    public void setUp() throws Exception {

        List<String> resources = Arrays.asList(
                "sql/h2/schema.sql",
                "sql/h2/repositories/LiveSampleRepositoryTest-data.sql"
        );

        for (String resource : resources) {
            Resource r = context.getResource(resource);
            ScriptUtils.executeSqlScript(komp2DataSource.getConnection(), r);
        }
    }

    @Test
    public void findAll() throws Exception {

        List<LiveSample> expectedList = Arrays.asList(
                createLiveSample(3L,      "baseline", "EFO:0002948", 15, "male",   "homozygote", "2007-04-24", "unknown" ),
                createLiveSample(1L,      "baseline", "EFO:0002948", 15, "female", "homozygote", "2007-04-26", "unknown"),
                createLiveSample(117523L, "",         "EFO:0002948", 15, "male",   "homozygote", "2015-04-08", "" ),
                createLiveSample(117552L, "",         "EFO:0002948", 15, "male",   "homozygote", "2015-05-25", "" ),
                createLiveSample(32703L,  "#4",       "EFO:0002948", 15, "male",   "homozygote", "2009-09-01", "unknown" )
        );

        Map<Long, LiveSample> expectedMap =
                expectedList.stream()
                        .collect(Collectors.toMap(LiveSample::getId, Function.identity()));

        StreamSupport
                .stream(liveSampleRepository.findAll().spliterator(), false)
                .map(actual -> {
                    LiveSample expected = expectedMap.get(actual.getId());
                    assertEquals(expected.getId(), actual.getId());
                    assertEquals(expected.getColonyID(), actual.getColonyID());
                    assertEquals(expected.getDatasource(), actual.getDatasource());
                    assertEquals(expected.getSex(), actual.getSex());
                    assertEquals(expected.getZygosity(), actual.getZygosity());
                    assertEquals(expected.getDateOfBirth().getTime(), actual.getDateOfBirth().getTime());
                    assertEquals(expected.getLitterId(), actual.getLitterId());

                    return actual;
                });
    }

    private LiveSample createLiveSample(Long id, String colonyId, String acc, Integer dbid, String sex, String zygosity, String dateOfBirth, String litterId) throws Exception {

        OntologyTerm developmentalStage = new OntologyTerm(acc, dbid.longValue());
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        
        LiveSample liveSample = new LiveSample();

        liveSample.setId(id);
        liveSample.setColonyID(colonyId);
        liveSample.setDevelopmentalStage(developmentalStage);
        liveSample.setSex(sex);
        liveSample.setZygosity(zygosity);
        liveSample.setDateOfBirth(formatter.parse(dateOfBirth));
        liveSample.setLitterId(litterId);
        
        return liveSample;
    }
}