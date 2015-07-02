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

import org.springframework.data.solr.repository.SolrCrudRepository;


/**
 * @author Christoph Strobl
 */
interface ImageRepository extends SolrCrudRepository<Image, String> {

//	@Highlight(prefix = "<b>", postfix = "</b>")
//	@Query(fields = {Image.DOWNLOAD_URL,
//			}, defaultOperator = Operator.AND)
//	HighlightPage<Image> findByDownloadUrlIn(Collection<String> names, Pageable page);
//	@Facet(fields = { Image.DOWNLOAD_URL})
//	FacetPage<Image> findByDownloadUrl(Collection<String> nameFragments, Pageable pagebale);
        //@Query("ma_id:?")
        List<Image> findByMaId(String maId);
        //@Query("mgi_accession_id:\"?0\"")
        public List<Image> findByMarkerAccession(String markerAccession);

}
