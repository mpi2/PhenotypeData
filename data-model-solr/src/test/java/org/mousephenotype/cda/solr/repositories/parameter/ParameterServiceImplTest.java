package org.mousephenotype.cda.solr.repositories.parameter;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mousephenotype.cda.solr.repositories.DataModelSolrTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.data.solr.repository.config.EnableSolrRepositories;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = DataModelSolrTest.class)
@EnableSolrRepositories(basePackages = { "org.mousephenotype.cda.solr.repositories.parameter" }, multicoreSupport=true)
public class ParameterServiceImplTest {
	
	@Autowired
	ParameterServiceImpl parameterService;
	@Test
	public void testFindByStableId() {
		List<Parameter> parameters = parameterService.findByStableId("IMPC_BWT_004_001");
		for(Parameter parameter:parameters){
			System.out.println("parameter id ="+parameter.getId());
		}
		assertTrue(parameters.size()>0);
	}

}
