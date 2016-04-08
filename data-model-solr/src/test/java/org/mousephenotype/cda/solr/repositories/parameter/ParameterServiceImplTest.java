package org.mousephenotype.cda.solr.repositories.parameter;

import org.junit.runner.RunWith;
import org.mousephenotype.cda.solr.TestConfigSolr;
import org.springframework.data.solr.repository.config.EnableSolrRepositories;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={TestConfigSolr.class})
@EnableSolrRepositories(basePackages = { "org.mousephenotype.cda.solr.repositories.parameter" }, multicoreSupport=true)
public class ParameterServiceImplTest {

//	@Autowired
//	ParameterServiceImpl parameterService;
//
//	@Test
//	public void testFindByStableId() {
//		List<Parameter> parameters = parameterService.findByStableId("IMPC_BWT_004_001");
//		for(Parameter parameter:parameters){
//			System.out.println("parameter id ="+parameter.getId());
//		}
//		assertTrue(parameters.size()>0);
//	}

}
