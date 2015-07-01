package org.mousephenotype.cda.repositories.solr.parameter;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mousephenotype.cda.repositories.solr.DataModelSolrTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.data.solr.repository.config.EnableSolrRepositories;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = DataModelSolrTest.class)
@EnableSolrRepositories(basePackages = { "org.mousephenotype.cda.repositories.solr.parameter" }, multicoreSupport=true)
public class ParameterServiceImplTest {
	
	@Autowired
	ParameterServiceImpl parameterService;
	@Test
	public void testFindByStableId() {
		List<Parameter> parameters = parameterService.findByStableId("IMPC_BWT_004_001");
		assertTrue(parameters.size()>0);
	}

}
