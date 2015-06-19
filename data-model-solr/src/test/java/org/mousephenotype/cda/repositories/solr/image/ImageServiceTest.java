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
package org.mousephenotype.cda.repositories.solr.image;

import java.util.List;
import java.util.regex.Pattern;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mousephenotype.cda.repositories.solr.DataModelSolrApplication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

/**
 * @author Christoph Strobl
 */

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = DataModelSolrApplication.class)
@WebAppConfiguration
public class ImageServiceTest{

    private static final Pattern IGNORED_CHARS_PATTERN = Pattern.compile("\\p{Punct}");
    @Autowired
    private ImageServiceImpl imageService;

    @Test
    public void findByIdTest() {
        String id="19539479";
        System.out.println("querying for one image with " + id);
        Image image = imageService.findById(id);
        System.out.println("one image found=" + image.getMaId());
        
    }
    
    @Test
    public void findByMaIdTest() {
        String maId="MA:0000191";
        List<Image> imageList=imageService.findByMaId(maId);
        assertTrue(imageList!=null);
        assertTrue(imageList.size()>0);
        
    }

    

    

}
