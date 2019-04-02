package org.mousephenotype.cda.file.stats;

import static org.junit.Assert.assertFalse;

import java.io.File;
import java.util.List;

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
public class FileStatsDaoTest {

	private final Logger logger = LoggerFactory.getLogger(FileStatsDaoTest.class);
//chart for this set here: http://www.mousephenotype.org/data/charts?accession=MGI:1915747&parameter_stable_id=IMPC_HEM_038_001
	String center="MARC";
	String procedure="IMPC_HEM";
	String parameter="IMPC_HEM_038_001";
	String colonyId="1110018G07Rik_HEPD0633_2_C09_1";
	String zygosity="homozygote";
	String metadata="08aa37a898ab923b9ffdbd01c0077040";
	
	// String Configuration class
	// Only wire up the observation service for this test suite
	@Configuration
	@ComponentScan(
		basePackages = {"org.mousephenotype.cda"},
		useDefaultFilters = false,
		includeFilters = { @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = {FileStatsDao.class})
		})
	static class ContextConfiguration {

		@NotNull
		@Value("${root_stats_directory}")
		private String rootStatsDirectory;
		@NotNull
		@Value("${original_stats_directory}")
		private String originalStatsDirectory;

		@Bean(name = "fileExperimentDao")
		FileStatsDao getFileExperimentDao() {
			return new FileStatsDao(rootStatsDirectory, originalStatsDirectory);
		}

		@Bean
		public static PropertySourcesPlaceholderConfigurer propertyConfigurer() {
			return new PropertySourcesPlaceholderConfigurer();
		}

	}

	@Autowired
	FileStatsDao fileExperimentDao;

	@Test
	public void testGetStatsSummary() {
		Statistics result = fileExperimentDao.getStatsSummary(center, procedure, parameter, colonyId, zygosity, metadata);
	System.out.println("result = "+result);
	}

//	@Test
//	public void getFilePath(){
//		String filePath=fileExperimentDao.getFilePathFromIndex(center, procedure, parameter, colonyId, zygosity, metadata);
//		assertFalse(filePath.isEmpty());
//		
//		
//		
//	}
//	
//	@Test
//	public void getFileWhenNotInIndex() {
//		String filePath=fileExperimentDao.getFilePathFromIndex("blah", procedure, parameter, colonyId, zygosity, metadata);
//		assert(filePath.isEmpty());
//	}
	
	@Test
	public void readIndexFileTest() {
		File indexFile=fileExperimentDao.readIndexFile();
		assert(indexFile.isFile());
	}

	
	@Test
	public void getParameterOptionsForRequest() {
		List<String> filePaths = fileExperimentDao.getParameterOptionsForRequest(center, parameter, metadata);
		assert(filePaths.size()>0);
		filePaths.forEach(blah -> System.out.println(blah));
		System.out.println("filepaths size="+filePaths.size());
		
	}
	
	


}
