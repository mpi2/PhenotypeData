package org.mousephenotype.cda.indexers;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mousephenotype.cda.indexers.utils.EmbryoRestData;
import org.mousephenotype.cda.indexers.utils.EmbryoRestGetter;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {TestConfigIndexers.class} )
@TestPropertySource(locations = {"file:${user.home}/configfiles/${profile}/test.properties"})
@Transactional
public class EmbryoRestGetterTest {

	@Test
	public void getEmbryDataTest(){
		System.out.println("in getEmbryoDataTest blah");
		EmbryoRestGetter embryoRest=new EmbryoRestGetter("http://dev.mousephenotype.org/EmbryoViewerWebApp/rest/ready");
		EmbryoRestData embryoDataSet = embryoRest.getEmbryoRestData();
		System.out.println(embryoDataSet);
	}
}
