package uk.ac.ebi.phenotype.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import javax.xml.bind.JAXBException;
import java.io.IOException;


/**
 * @since 2015/10/08
 * @author ilinca
 *
 */

@RunWith(SpringJUnit4ClassRunner.class)
@TestPropertySource("file:${user.home}/configfiles/${profile:dev}/test.properties")
@ContextConfiguration(loader=AnnotationConfigContextLoader.class)
public class UniprotServiceTest {

	// Only wire up the Uniprot service for this test suite
	@Configuration
	@ComponentScan(
		basePackages = {"uk.ac.ebi.phenotype.service"},
		useDefaultFilters = false,
		includeFilters = { @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = {UniprotService.class})
		})
	static class ContextConfiguration {

		@Bean
		public static PropertySourcesPlaceholderConfigurer propertyConfigurer() {
			return new PropertySourcesPlaceholderConfigurer();
		}

	}


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
