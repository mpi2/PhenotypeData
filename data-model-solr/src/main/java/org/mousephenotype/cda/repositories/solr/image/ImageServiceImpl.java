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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

/**
 * @author Christoph Strobl
 */
@Service
class ImageServiceImpl implements ImageService {

    

    private ImageRepository imageRepository;

    @Override
    public Image findById(String id) {
        
        System.out.println("querying for one image with " + id);
        Image image = imageRepository.findOne(id);
        System.out.println("one image found=" + image);
        return image;
    }

    @Override
    public List<Image> findByMaId(String maId) {
        List<Image> imageList = imageRepository.findByMaId("\""+maId+"\"");
        for (Image image : imageList) {
            System.out.println("one image found=" + image.getMaId());
        }
        return imageList;
    }
    
    @Override
    public List<Image> findByMarkerAccession(String markerAccession1) {
        List<Image> imageList = imageRepository.findByMarkerAccession("\""+markerAccession1+"\"");
        for (Image image : imageList) {
            System.out.println("one image found id=" + image.getId());
        }
        return imageList;
    }

    @Autowired
    public void setImageRepository(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

}
