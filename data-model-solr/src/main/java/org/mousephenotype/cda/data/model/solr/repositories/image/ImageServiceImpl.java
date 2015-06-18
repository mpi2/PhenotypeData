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
package org.mousephenotype.cda.data.model.solr.repositories.image;

import java.util.regex.Pattern;

import org.mousephenotype.cda.data.model.solr.repositories.image.model.Image;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Christoph Strobl
 */
@Service
class ImageServiceImpl implements ImageService {

	private static final Pattern IGNORED_CHARS_PATTERN = Pattern.compile("\\p{Punct}");

	private ImageRepository imageRepository;

//	@Override
//	public Page<Image> findByDownloadUrl(String downloadUrl, Pageable pageable) {
//            System.out.println("downloadUrl search="+downloadUrl);
//		if (StringUtils.isBlank(downloadUrl)) {
//                    System.out.println("downloadUrl is blank");
//			return imageRepository.findAll(pageable);
//		}
//
//		return imageRepository.findByDownloadUrlIn(splitSearchTermAndRemoveIgnoredCharacters(downloadUrl), pageable);
//	}

	@Override
	public Image findById(String id) {
            System.out.println("querying for one image with "+id);
            Image image = imageRepository.findOne(id);
            System.out.println("one image found="+image);
		return image;
	}

//	private Collection<String> splitSearchTermAndRemoveIgnoredCharacters(String searchTerm) {
//		String[] searchTerms = StringUtils.split(searchTerm, " ");
//		List<String> result = new ArrayList<String>(searchTerms.length);
//		for (String term : searchTerms) {
//			if (StringUtils.isNotEmpty(term)) {
//				result.add(IGNORED_CHARS_PATTERN.matcher(term).replaceAll(" "));
//			}
//		}
//		return result;
//	}

	@Autowired
	public void setImageRepository(ImageRepository imageRepository) {
		this.imageRepository = imageRepository;
	}

//    @Override
//    public FacetPage<Image> autocompleteNameFragment(String fragment, Pageable pageable) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }

}
