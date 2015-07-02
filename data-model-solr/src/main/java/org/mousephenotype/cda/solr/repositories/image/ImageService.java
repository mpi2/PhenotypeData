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

import java.util.List;


/**
 * @author Christoph Strobl
 */
public interface ImageService {

	int DEFAULT_PAGE_SIZE = 3;

	//Page<Image> findByDownloadUrl(String searchTerm, Pageable pageable);

	Image findById(String id);
        
        List<Image> findByMaId(String maId);

        public List<Image> findByMarkerAccession(String markerAccession);
	//FacetPage<Image> autocompleteNameFragment(String fragment, Pageable pageable);

}
