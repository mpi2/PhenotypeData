package org.mousephenotype.cda.solr.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mousephenotype.cda.solr.TestConfigSolr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import javax.validation.constraints.NotNull;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = {TestConfigSolr.class})
public class ImpressServiceTest {

	@Autowired
	@NotNull
	ImpressService impressService;


	@Test
	public void getMpsForProcedures() throws Exception {

		Map<String, Set<String>> mps = impressService.getMpsForProcedures();

		assertTrue(mps.size() > 100);
	}
}