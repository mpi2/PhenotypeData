/*
 * Copyright 2012 - 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.mousephenotype.cda.solr.repositories.image;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mousephenotype.cda.solr.TestConfigSolr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.repository.config.EnableSolrRepositories;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * @author Christoph Strobl
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={TestConfigSolr.class})
@EnableSolrRepositories(basePackages = { "org.mousephenotype.cda.solr.repositories.image" }, multicoreSupport=true)
public class ImageServiceTest{

    @Autowired
    private ImageServiceImpl imageService;



    @Test
    public void findByIdTest() {
        String id="22654363";
        System.out.println("ImageService= " + imageService);
        System.out.println("querying for one image with " + id);

        Image image = imageService.findById(id);
        System.out.println("one image found id="+ image.getId()+" maId="+ image.getMaId());

    }

    @Test
    public void findByMaIdTest() {
        String maId="MA:0000191";
        System.out.println("imageService="+imageService);
        List<Image> imageList=imageService.findByMaId(maId);
        assertTrue(imageList!=null);
        assertTrue(imageList.size()>0);

    }

    @Test
    public void findByMarkerAccessionTest(){
        String markerAccession="MGI:1342278";
        List<Image> images=imageService.findByMarkerAccession(markerAccession);
        assertTrue(images.size()>0);
    }





}
