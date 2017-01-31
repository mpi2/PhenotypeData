package org.mousephenotype.cda.indexers.utils;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.mousephenotype.cda.enumerations.BiologicalSampleType;
import org.mousephenotype.cda.enumerations.ObservationType;
import org.mousephenotype.cda.enumerations.SexType;
import org.mousephenotype.cda.enumerations.ZygosityType;
import org.mousephenotype.cda.indexers.beans.PChannelDTO;
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
	
	HttpSolrClient phisSolr = new HttpSolrClient("http://ves-ebi-d2.ebi.ac.uk:8140/mi/phis/v1.0.4/images/");
    HttpSolrClient phisRoiSolr = new HttpSolrClient("http://ves-ebi-d2.ebi.ac.uk:8140/mi/phis/v1.0.4/rois/");

	public List<ImageDTO> getPhenoImageShareImageDTOs() throws SolrServerException, IOException {

		List<PImageDTO> phisImages = new ArrayList<>();
		SolrQuery q = new SolrQuery();

		q.setQuery(PImageDTO.HOST_NAME + ":WTSI").setRows(Integer.MAX_VALUE); // WTSI -> brain histopath images
		phisImages.addAll(phisSolr.query(q).getBeans(PImageDTO.class));

		q.setQuery(PImageDTO.HOST_NAME + ":\"IMPC Portal\"");
		phisImages.addAll(phisSolr.query(q).getBeans(PImageDTO.class)); // IMPC Portal -> Sanger old images
        //TODO filter out XRAy anf lacz because we already have an import from sanger. Could also import them as PhIS has more but we need to check which ones are already in.
		System.out.println("Got this many images: " + phisImages.size());

		return this.convertPhisImageToImages(phisImages);

	}

	public List<PChannelDTO> getPhenoImageShareChannelDTOs(String imageId) throws IOException, SolrServerException {

	    SolrQuery query = new SolrQuery(PChannelDTO.ASSOCIATED_IMAGE + ":" + imageId);
        return phisRoiSolr.query(query).getBeans(PChannelDTO.class);

	}

	private List<ImageDTO> convertPhisImageToImages(List<PImageDTO> phisImages) throws IOException, SolrServerException {

		List<ImageDTO> images = new ArrayList<>();

		for (PImageDTO pImage : phisImages) {

			ImageDTO image = new ImageDTO();

			image.setObservationType(ObservationType.image_record.name());

			if(pImage.getGeneIds().size() >= 1){
				image.setGeneAccession(pImage.getGeneIds().get(0));
                if(pImage.getGeneIds().size() != 1){
                    logger.warn("MGI accession in Phis is not 1 for image.");
                }
			}

			if(pImage.getGeneSymbols().size() >= 1){
				image.setGeneSymbol(pImage.getGeneSymbols().get(0));
                if(pImage.getGeneSymbols().size() != 1){
                    logger.warn("Symbol in Phis is not 1 for image.");
                }
			}

			if(pImage.getGeneticFeatureIds().size() >= 1){
				image.setAlleleAccession(pImage.getGeneticFeatureIds().get(0));
                if(pImage.getGeneticFeatureIds().size() != 1){
                    logger.warn("getGeneticFeatureIds (allele accession) in Phis is not 1 for one image.");
                }
			}

            if (pImage.getGeneticFeatureSymbols().size() >= 1){
                image.setAlleleSymbol(pImage.getGeneticFeatureSymbols().get(0));
                if(pImage.getGeneticFeatureSymbols().size() != 1){
                    logger.warn("getGeneticFeatureSymbols (allele symbol) in Phis is not 1 for one image.");
                }
            }

            // Genes in expression images are stored in a different field. Need to check the expressed bag too
            // Allele and gene ids are stored in the same field as this is only used for querying. You can get them from the associated roi.
            if (pImage.getExpressedGfIdBag().size() >= 1){
                //TODO
                List<PChannelDTO> channelDTOs = getPhenoImageShareChannelDTOs(pImage.getId());

            }
			image.setFullResolutionFilePath(pImage.getImageUrl());
			image.setJpegUrl(pImage.getImageUrl());
			image.setDownloadUrl(pImage.getImageUrl());
			image.setThumbnailUrl(pImage.getThumbnailUrl());
			
			// need to loop over and creat mp_id_terms
			if (pImage.getPhenotypeIdBag() != null) {
				image.setMpId(pImage.getPhenotypeIdBag());
			}
			if(pImage.getImageGeneratedBy() !=null){
				image.setPhenotypingCenter(pImage.getImageGeneratedBy().get(0));//getting the first in the list. will we have a problem with centers that are not in our list/db as centers e.g. for brain histopath - ?
			}
			
			//what about mpath, emap and cmpo? no ids in the list of 163?
			if(pImage.getSampleType()!=null){
				String sampleType=pImage.getSampleType();
				if(sampleType.equalsIgnoreCase("MUTANT")){
					image.setGroup(BiologicalSampleType.experimental.toString());
				}else{
					image.setGroup(BiologicalSampleType.control.toString());
				}
			}

			if(pImage.getPipeline() != null){
				image.setPipelineStableId(pImage.getPipeline());
			}
			if(pImage.getProcedure()!=null){
				if (pImage.getProcedure().contains("_")) { // is  IMPRESS id
					image.setProcedureStableId(pImage.getProcedure());
				} else { // text name of procedure; procedure not in IMPRESS
                    image.setProcedureStableId(pImage.getProcedure().trim().replaceAll(" ", "_"));
                    image.setProcedureName(pImage.getProcedure().trim());
                }
			}
			if(pImage.getParameter()!=null){
			    if (pImage.getParameter().contains("_")) {
                    image.setParameterStableId(pImage.getParameter());
                } else {
                    image.setParameterStableId(pImage.getParameter().trim().replaceAll(" ", "_"));
                    image.setParameterName(pImage.getParameter());
                }
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
			if(pImage.getExperimentGroup() != null){
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
			
//			//<arr name="stage_facet">
//			//<str>post-embryonic stage</str>
//			//</arr>
//			if(pImage.getStage()!=null){
//				image.setStage(pImage.getStage());
//			}
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
