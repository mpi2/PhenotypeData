package uk.ac.ebi.phenotype.service;

import java.io.IOException;

import javax.xml.bind.JAXBException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import uk.ac.ebi.phenotype.web.TestConfig;


/**
 * @since 2015/10/08
 * @author ilinca
 *
 */

@RunWith(SpringJUnit4ClassRunner.class)
@TestPropertySource("file:${user.home}/configfiles/${profile}/test.properties")
@SpringApplicationConfiguration(classes = TestConfig.class)
@Transactional
public class UniprotServiceTest {

	
    @Autowired
    UniprotService uniprotService;

    @Test
    @Ignore
    public void testCheckTypeParameterString() {
       
        UniprotDTO dto = new UniprotDTO();
		try {
			
			dto = uniprotService.readXml("http://www.uniprot.org/uniprot/Q6ZNJ1.xml" , dto);
		
	        assert(dto.getFunction() != null);
	        assert(dto.getGoCell() != null && dto.getGoCell().size() >= 2);
	        assert(dto.getGoMolecularFunction() != null && dto.getGoMolecularFunction().size() >= 1);
	        assert(dto.getGoProcess() != null && dto.getGoProcess().size() >= 2);
	        
		} catch (JAXBException | IOException e) {
			e.printStackTrace();
		}
    }
}
