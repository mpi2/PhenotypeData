package org.mousephenotype.cda.indexers;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mousephenotype.cda.config.TestConfigIndexers;
import org.mousephenotype.cda.indexers.utils.EmbryoRestData;
import org.mousephenotype.cda.indexers.utils.EmbryoRestGetter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = {TestConfigIndexers.class})
public class EmbryoRestGetterTest {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Value("${embryoViewerFilename}")
	private       String embryoViewerFilename;

	@Test
	public void getEmbryoDataTest() throws JSONException {
		final int EXPECTED_STRAIN_COUNT = 265;					// As of 17-Apr-2019 there were 265 strains
		int actualStrainCount;

		EmbryoRestGetter embryoRest = new EmbryoRestGetter(embryoViewerFilename);
		EmbryoRestData embryoDataSet = embryoRest.getEmbryoRestData();
		actualStrainCount = (embryoDataSet.getStrains() != null ? embryoDataSet.getStrains().size() : 0);

		logger.debug(String.format("There are %s strain(s) in the embryo file", actualStrainCount));

		String message = "Expected at least " + EXPECTED_STRAIN_COUNT + " strains but found only " + actualStrainCount;
		Assert.assertEquals(message, EXPECTED_STRAIN_COUNT, actualStrainCount);
	}
}