package org.mousephenotype.cda.solr.service;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mousephenotype.cda.owl.OntologyParser;
import org.mousephenotype.cda.owl.OntologyParserFactory;
import org.mousephenotype.cda.owl.OntologyTermDTO;
import org.mousephenotype.cda.solr.TestConfigSolr;
import org.mousephenotype.cda.solr.service.dto.GenotypePhenotypeDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import sun.jvm.hotspot.utilities.Assert;

import javax.sql.DataSource;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes={TestConfigSolr.class})
@TestPropertySource(locations = {"file:${user.home}/configfiles/${profile:dev}/test.properties"})
@Transactional
public class PostQcServiceTest {

    @NotNull
    @Autowired
    PostQcService postQcService;

    @NotNull
    @Autowired
    @Qualifier("komp2DataSource")
    DataSource komp2DataSource;

    @NotNull
    @Value("${owlpath}")
    String owlpath;

    @Test
    public void getCountsByTopLevelMpTermAcc() throws Exception {

        // These top level MP terms should have hits in the g-p core
        List<String> TOP_LEVELS = Arrays.asList("MP:0005376", "MP:0005386", "MP:0005397", "MP:0010768", "MP:0005378", "MP:0005387", "MP:0005390", "MP:0005385", "MP:0005380", "MP:0005375", "MP:0003631", "MP:0005389", "MP:0005391", "MP:0005377", "MP:0005379", "MP:0010771", "MP:0005367", "MP:0005382", "MP:0005371", "MP:0005381", "MP:0005388", "MP:0005370", "MP:0005369", "MP:0000001");

        OntologyParserFactory f = new OntologyParserFactory(komp2DataSource, owlpath);
        OntologyParser mpParser = f.getMpParser();

        Map<String, Long> test = postQcService.getCountsByLevelMpTermAcc(GenotypePhenotypeDTO.TOP_LEVEL_MP_TERM_NAME);
        System.out.println(StringUtils.join(test.keySet(), ", "));

        for (String term : TOP_LEVELS) {
            OntologyTermDTO t = mpParser.getOntologyTerm(term);

            System.out.println("Checking term: " + t);

            Assert.that(test.containsKey(t.getName()), "test doesn't contain top level " + t);
        }
    }

}