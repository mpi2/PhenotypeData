package org.mousephenotype.cda.solr.repositories.parameter;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mousephenotype.cda.solr.TestConfigSolr;
import org.mousephenotype.cda.solr.repositories.GenotypePhenotypeRepository;
import org.mousephenotype.cda.solr.service.dto.GenotypePhenotypeDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes={TestConfigSolr.class})
@TestPropertySource(locations = {"file:${user.home}/configfiles/${profile:dev}/test.properties"})
public class GenotypePhenotypeRepositoryTest {

	@Autowired
	GenotypePhenotypeRepository genotypePhenotypeRepository;

	@Test
	public void testFindByStableId() {
		List<GenotypePhenotypeDTO> genoPhenoDTOs = genotypePhenotypeRepository.findByParameterStableId("IMPC_CSD_003_001");
		for(GenotypePhenotypeDTO genoPhenoDTO : genoPhenoDTOs){
			System.out.println("genoPhenoDTO id ="+genoPhenoDTO.getId());
		}
		assert(genoPhenoDTOs.size()>0);
	}
}