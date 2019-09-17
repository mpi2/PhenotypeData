package uk.ac.ebi.phenotype.service;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import uk.ac.ebi.phenotype.web.TestConfig;

import javax.xml.bind.JAXBException;
import java.io.IOException;


/**
 * @since 2015/10/08
 * @author ilinca
 *
 */

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = {TestConfig.class})
@Ignore
public class UniprotServiceTest {

	@Autowired
    UniprotService uniprotService;

    @Test
    public void testCheckTypeParameterString() {

        UniprotDTO dto = new UniprotDTO();
		try {

			dto = uniprotService.readXml("https://www.uniprot.org/uniprot/P26367.xml" , dto);

	        assert(dto.getFunction() != null);
	        assert(dto.getGoCell() != null && dto.getGoCell().size() >= 2);
	        assert(dto.getGoMolecularFunction() != null && dto.getGoMolecularFunction().size() >= 1);
	        assert(dto.getGoProcess() != null && dto.getGoProcess().size() >= 2);

		} catch (JAXBException | IOException e) {

			e.printStackTrace();
		}
    }
}