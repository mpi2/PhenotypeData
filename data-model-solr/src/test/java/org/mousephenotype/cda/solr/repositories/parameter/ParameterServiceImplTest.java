package org.mousephenotype.cda.solr.repositories.parameter;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mousephenotype.cda.solr.TestConfigSolr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={TestConfigSolr.class})
@TestPropertySource(locations = {"file:${user.home}/configfiles/${profile:dev}/test.properties"})
public class ParameterServiceImplTest {

	@Autowired
	ParameterServiceImpl parameterService;

	@Test
	public void testFindByStableId() {
		List<Parameter> parameters = parameterService.findByStableId("IMPC_BWT_004_001");
		for(Parameter parameter:parameters){
			System.out.println("parameter id ="+parameter.getId());
		}
		assert(parameters.size()>0);
	}

}
