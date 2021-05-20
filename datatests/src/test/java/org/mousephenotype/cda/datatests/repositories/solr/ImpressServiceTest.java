package org.mousephenotype.cda.datatests.repositories.solr;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mousephenotype.cda.solr.service.ImpressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.validation.constraints.NotNull;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {RepositorySolrTestConfig.class})
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