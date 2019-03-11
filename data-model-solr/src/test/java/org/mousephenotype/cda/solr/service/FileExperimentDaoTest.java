package org.mousephenotype.cda.solr.service;

import java.io.File;

import javax.validation.constraints.NotNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader=AnnotationConfigContextLoader.class)
@TestPropertySource("file:${user.home}/configfiles/${profile:dev}/test.properties")
public class FileExperimentDaoTest {

	private final Logger logger = LoggerFactory.getLogger(FileExperimentDaoTest.class);

	// Sring Configuration class
	// Only wire up the observation service for this test suite
	@Configuration
	@ComponentScan(
		basePackages = {"org.mousephenotype.cda"},
		useDefaultFilters = false,
		includeFilters = { @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = {FileExperimentDao.class})
		})
	static class ContextConfiguration {

		@NotNull
		@Value("${root_stats_directory}")
		private String rootStatsDirectory;
		@NotNull
		@Value("${original_stats_directory}")
		private String originalStatsDirectory;

		@Bean(name = "fileExperimentDao")
		FileExperimentDao getFileExperimentDao() {
			return new FileExperimentDao(rootStatsDirectory, originalStatsDirectory);
		}

		@Bean
		public static PropertySourcesPlaceholderConfigurer propertyConfigurer() {
			return new PropertySourcesPlaceholderConfigurer();
		}

	}

	@Autowired
	FileExperimentDao fileExperimentDao;


	@Test
	public void getFileByCenterProcedureParameterAndColonyId(){
///nfs/nobackup/spot/mouseinformatics/HAMED_HA/DR9.2/jobs/Results_9.2_V1/MARC/IMPC_HEM/IMPC_HEM_038_001/1110018G07Rik_HEPD0633_2_C09_1/homozygote/08aa37a898ab923b9ffdbd01c0077040/output_Successful.tsv
		String center="MARC";
		String procedure="IMPC_HEM";
		String parameter="IMPC_HEM_038_001";
		String colonyId="1110018G07Rik_HEPD0633_2_C09_1";
		String zygosity="homozygote";
		String metadata="08aa37a898ab923b9ffdbd01c0077040";
		
		File file=fileExperimentDao.getFileByCenterProcedureParameterAndColonyId(center, procedure, parameter, colonyId, zygosity, metadata);
		assert(file.isFile());
		
	}
	
	@Test
	public void readIndexFileTest() {
		File indexFile=fileExperimentDao.readIndexFile();
		assert(indexFile.isFile());
	}

//	@Test
//	public void getGrossPathObservationByProcedureNameAndGene(){
//
//		String procedureName="Gross Pathology and Tissue Collection";
//		String geneAccession="MGI:2449119";
//		try {
//			List<ObservationDTO> result = fileExperimentDao.getObservationsByProcedureNameAndGene(procedureName, geneAccession);
//			assertTrue(result.size()>0);
//
//		} catch (SolrServerException | IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}

	
	


}
