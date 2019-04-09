package org.mousephenotype.cda.indexers;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mousephenotype.cda.config.TestConfigIndexers;
import org.mousephenotype.cda.indexers.utils.EmbryoRestData;
import org.mousephenotype.cda.indexers.utils.EmbryoRestGetter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import javax.validation.constraints.NotNull;

//@RunWith(SpringJUnit4ClassRunner.class)
//@SpringApplicationConfiguration(classes = {TestConfigIndexers.class} )
//@TestPropertySource(locations = {"file:${user.home}/configfiles/${profile:dev}/test.properties"})
//@Transactional



@RunWith(SpringRunner.class)
@TestPropertySource("file:${user.home}/configfiles/${profile:dev}/test.properties")
@SpringBootTest(classes = TestConfigIndexers.class)
public class EmbryoRestGetterTest {

	@NotNull
	@Value("${embryoViewerFilename}")
	private String embryoViewerFilename;

	@Test
	public void getEmbryDataTest() throws JSONException {
		System.out.println("in getEmbryoDataTest blah");
		EmbryoRestGetter embryoRest=new EmbryoRestGetter(embryoViewerFilename);
		EmbryoRestData embryoDataSet = embryoRest.getEmbryoRestData();
		System.out.println(String.format("There are %s strain(s) in the embryo file", (embryoDataSet.getStrains()!=null?embryoDataSet.getStrains().size():"*NO*")));
	}
}