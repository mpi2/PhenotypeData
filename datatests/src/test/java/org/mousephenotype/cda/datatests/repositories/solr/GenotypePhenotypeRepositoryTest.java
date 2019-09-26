package org.mousephenotype.cda.datatests.repositories.solr;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mousephenotype.cda.solr.repositories.GenotypePhenotypeRepository;
import org.mousephenotype.cda.solr.service.dto.GenotypePhenotypeDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {RepositorySolrTestConfig.class})
public class GenotypePhenotypeRepositoryTest {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	GenotypePhenotypeRepository genotypePhenotypeRepository;

	@Test
	public void testFindByStableId() {
		List<GenotypePhenotypeDTO> genoPhenoDTOs = genotypePhenotypeRepository.findByParameterStableId("IMPC_CSD_003_001");
		for(GenotypePhenotypeDTO genoPhenoDTO : genoPhenoDTOs){
			logger.debug("genoPhenoDTO id = "+genoPhenoDTO.getId());
		}

		assertTrue("Expected at least one genotype-phenotype association but there were none", genoPhenoDTOs.size( )> 0);
	}
}