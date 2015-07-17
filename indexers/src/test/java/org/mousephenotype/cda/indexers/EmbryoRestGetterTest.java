package org.mousephenotype.cda.indexers;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mousephenotype.cda.indexers.utils.EmbryoRestData;
import org.mousephenotype.cda.indexers.utils.EmbryoRestGetter;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

//@RunWith(SpringJUnit4ClassRunner.class)
public class EmbryoRestGetterTest {

	@Test
	public void getEmbryDataTest(){
		System.out.println("in getEmbryoDataTest");
		EmbryoRestGetter embryoRest=new EmbryoRestGetter();
		embryoRest.setRestUrl("http://dev.mousephenotype.org/EmbryoViewerWebApp/rest/ready");
		EmbryoRestData embryoDataSet = embryoRest.getEmbryoRestData();
		System.out.println(embryoDataSet);
	}
}
