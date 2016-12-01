package org.mousephenotype.cda.indexers;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeansException;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by jmason on 01/12/2016.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {TestConfigIndexers.class})
@TestPropertySource(locations = {"file:${user.home}/configfiles/${profile:dev}/test.properties"})
public class ImpcImagesIndexerTest implements ApplicationContextAware {

    private ApplicationContext applicationContext;
    private ImpcImagesIndexer impcImagesIndexer;


    @Before
    public void setUp() throws Exception {
        impcImagesIndexer = ImpcImagesIndexer.class.newInstance();
        applicationContext.getAutowireCapableBeanFactory().autowireBean(impcImagesIndexer);
        applicationContext.getAutowireCapableBeanFactory().initializeBean(impcImagesIndexer, "IndexBean" + impcImagesIndexer.getClass().toGenericString());
    }


    @Test
    public void getHighestObservationId() throws Exception {

        Integer largest = impcImagesIndexer.getHighestObservationId();
        System.out.println("Largest ID is " + largest);
        assert(largest > 1000000); // There are at least One MILLION documents
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

}