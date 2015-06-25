package demo;

import static org.junit.Assert.assertTrue;
import org.mousephenotype.cda.repositories.solr.DataModelSolrApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
@ComponentScan
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = DataModelSolrApplication.class)
@WebAppConfiguration
public class DemoApplicationTests {

	@Test
	public void contextLoads() {
            assertTrue(true);
	}

}
