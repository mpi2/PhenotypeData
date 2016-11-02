package org.mousephenotype.cda.indexers.utils;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.mousephenotype.cda.enumerations.BiologicalSampleType;
import org.mousephenotype.cda.enumerations.SexType;
import org.mousephenotype.cda.enumerations.ZygosityType;
import org.mousephenotype.cda.indexers.beans.PImageDTO;
import org.mousephenotype.cda.solr.service.ImageService;
import org.mousephenotype.cda.solr.service.dto.ImageDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PhisService {

	
	private final Logger logger = LoggerFactory.getLogger(ImageService.class);
	
	HttpSolrClient phisSolr = new HttpSolrClient("http://ves-ebi-d2.ebi.ac.uk:8140/mi/phis/v1.0.3/images/");

	public List<ImageDTO> getPhenoImageShareImageDTOs() throws SolrServerException, IOException {
		// http://ves-ebi-d2.ebi.ac.uk:8140/mi/phis/v1.0.3/images/select?q=host_name:WTSI&rows=200
		SolrQuery q = new SolrQuery();
		q.setQuery(PImageDTO.HOST_NAME + ":WTSI").setRows(200);// Integer.MAX_VALUE);

		List<PImageDTO> phisImages = phisSolr.query(q).getBeans(PImageDTO.class);
		return this.convertPhisImageToImages(phisImages);
	}

	private List<ImageDTO> convertPhisImageToImages(List<PImageDTO> phisImages) {
		List<ImageDTO> images = new ArrayList<>();
		for (PImageDTO pImage : phisImages) {
			ImageDTO image = new ImageDTO();
			String id = pImage.getId();
			if(pImage.getGeneIds().size()>=1){
					image.setGeneAccession(pImage.getGeneIds().get(0));
			}
			if(pImage.getGeneIds().size()!=1){
				logger.warn("mgi accession in Phis is not 1 for image!!!!!!!");
			}
			
			if(pImage.getGeneSymbols().size()>=1){
				image.setGeneSymbol(pImage.getGeneSymbols().get(0));
			}
			if(pImage.getGeneSymbols().size()!=1){
				logger.warn("symbol in Phis is not 1 for image!!!!!!!");
			}
			//strain ids? genetic_feature_id
			if(pImage.getGeneticFeatureIds().size()>=1){
				image.setStrainAccessionId(pImage.getGeneticFeatureIds().get(0));
			}
			if(pImage.getGeneticFeatureIds().size()!=1){
				logger.warn("getGeneticFeatureIds/ mgi strain accession in Phis is not 1!!!!!!!");
			}
			
			//System.out.println("Phis id=" + id);
			//System.out.println("thumbnail url=" + pImage.getThumbnailUrl());
			image.setFullResolutionFilePath(pImage.getImageUrl());
			//thumbnail url what to do? probably need a new field in the ImageDTO?
			
			// need to loop over and creat mp_id_terms
			if (pImage.getPhenotypeIdBag() != null) {
				image.setMpTermIds(pImage.getPhenotypeIdBag());
				image.setMpTerm(pImage.getPhenotypeLabelBag());
				// image.setMpTermSynonym(mpTermSynonym); dont currently have mp
				List<String> mpIdTerms = new ArrayList<>();
				for (int i = 0; i < pImage.getPhenotypeIdBag().size(); i++) {
					mpIdTerms.add(pImage.getPhenotypeIdBag().get(i) + "_" + pImage.getPhenotypeLabelBag().get(i));
				}
			}
			
			//what about mpath, emap and cmpo? no ids in the list of 163?
			
			//WT vs Experimental
//			<int name="MUTANT">243085</int>
//			<int name="WILD_TYPE">117846</int>
			if(pImage.getSampleType()!=null){
				String sampleType=pImage.getSampleType();
				//System.out.println("sampleType="+sampleType);
				if(sampleType.equalsIgnoreCase("MUTANT")){
					image.setGroup(BiologicalSampleType.experimental.toString());
				}else{
					image.setGroup(BiologicalSampleType.control.toString());
				}
			}
			
//			<str name="pipeline">MGP_001</str>
//			<str name="parameter">MGP_BHP_066_001</str>
//			<str name="procedure">MGP_BHP_001</str>
			if(pImage.getPipeline()!=null){
				image.setPipelineStableId(pImage.getPipeline());
			}
			if(pImage.getProcedure()!=null){
				image.setProcedureStableId(pImage.getProcedure());
			}
			if(pImage.getParameter()!=null){
				image.setParameterStableId(pImage.getParameter());
			}
//			<int name="FEMALE">181573</int>
//			<int name="MALE">166083</int>
//			<int name="UNKNOWN">3737</int>
//			<int name="UNSEXED">678</int>
			if(pImage.getSex()!=null){
				String sex=pImage.getSex();
//				System.out.println("sex="+sex);
				SexType assignedSex=SexType.no_data;
				if(sex.equals("MALE")){
					assignedSex=SexType.male;
				}else if(sex.equals("FEMALE")){
					assignedSex=SexType.female;					
				}else if(sex.equals("UNKNOWN")){
					assignedSex=SexType.no_data;
				}else if(sex.equals("UNSEXED")){
					assignedSex=SexType.not_applicable;
				}
				image.setSex(assignedSex.toString());
//				System.out.println("assignedSex="+assignedSex.toString());
			}
			//<str name="experiment_group">495324</str> is experiment id for sanger data?
			if(pImage.getExperimentGroup()!=null){
				image.setExperimentId(Integer.parseInt(pImage.getExperimentGroup()));
			}
			//if(pImage.getEx)
			
//			<int name="HOMOZYGOUS">130105</int>
//			<int name="HETEROZYGOUS">107430</int>
//			<int name="WILD_TYPE">5501</int>
//			<int name="HEMIZYGOUS">3922</int>
//			<int name="UNSPECIFIED">7</int>
			if(pImage.getZygosity().size()>=1){
				String zyg=pImage.getZygosity().get(0);
				ZygosityType z=null;
				if(zyg.equals("HOMOZYGOUS")){
					z=ZygosityType.homozygote;
				}else if(zyg.equals("HETEROZYGOUS")){
					z=ZygosityType.heterozygote;
				}else if(zyg.equals("HEMIZYGOUS")){
					z=ZygosityType.hemizygote;
				}else if(zyg.equals("WILD_TYPE")){//some centers describe lacz genes as WT as not necessarily a knockout -we will call it null here for impc
					z=null;
				}else if(zyg.equals("UNSPECIFIED")){
					z=null;
				}
				if(z!=null){
					image.setZygosity(z.toString());
				}
				
				
			}
			
			//<arr name="stage_facet">
			//<str>post-embryonic stage</str>
			//</arr>
			if(pImage.getStage()!=null){
				image.setStage(pImage.getStage());
			}
			//observations seem to contain the procedure name - loop over these and see if they have procedure at the start and if so add them to procedure name (or get it from our impress? how generic can we be?)
			if(pImage.getObservations().size()>=1){
				for(String obs: pImage.getObservations()){
					if(obs.startsWith("Procedure name: ")){
						String procedureName=obs.substring(obs.indexOf(": ")+2, obs.length() );
						image.setProcedureName(procedureName);
//						System.out.println("procedureName="+procedureName);
					}
				}
			}
		
			

			images.add(image);

		}

		return images;

	}
}
