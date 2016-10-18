package org.mousephenotype.cda.indexers.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.mousephenotype.cda.indexers.beans.PImageDTO;
import org.mousephenotype.cda.solr.service.dto.ImageDTO;

public class PhisService {

	public List<ImageDTO> getPhenoImageShareImageDTOs() throws SolrServerException, IOException{
		
	List<ImageDTO> phisImageDTOs=new ArrayList<>();
	//http://ves-ebi-d2.ebi.ac.uk:8140/mi/phis/v1.0.3/images/select?q=host_name:WTSI&rows=200
	HttpSolrClient phisSolr=new HttpSolrClient("http://ves-ebi-d2.ebi.ac.uk:8140/mi/phis/v1.0.3/images/");
	SolrQuery q = new SolrQuery();
	q.setQuery(PImageDTO.HOST_NAME+":WTSI").setRows(200);//Integer.MAX_VALUE);
	
	
		List<PImageDTO> phisImages=phisSolr.query(q).getBeans(PImageDTO.class);
		return this.convertPhisImageToImages(phisImages);
		
	
	}

	private List<ImageDTO> convertPhisImageToImages(List<PImageDTO> phisImages) {
		List<ImageDTO> images=new ArrayList<>();
		for(PImageDTO pImage:phisImages){
			ImageDTO image=new ImageDTO();
			String id=pImage.getId();
			System.out.println("Phis id="+id);
			System.out.println("thumbnail url="+pImage.getThumbnailUrl());
			image.setFullResolutionFilePath(pImage.getImageUrl());
			images.add(image);
			
		}
		
		
		
		return images;
		
	}
}
