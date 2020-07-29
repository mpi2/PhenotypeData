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
import org.mousephenotype.cda.solr.service.ImpressService;
import org.mousephenotype.cda.solr.service.dto.ImageDTO;
import org.mousephenotype.cda.solr.service.dto.ImpressBaseDTO;
import org.mousephenotype.cda.solr.service.dto.ParameterDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class PhisService {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	ImpressService impressService;

	HttpSolrClient phisSolr = new HttpSolrClient.Builder("http://ves-ebi-d2.ebi.ac.uk:8140/mi/phis/v1.0.4/images/").build();
    HttpSolrClient phisChannelSolr = new HttpSolrClient.Builder("http://ves-ebi-d2.ebi.ac.uk:8140/mi/phis/v1.0.4/channels/").build();

	public List<ImageDTO> getPhenoImageShareImageDTOs(Map<String, Set<String>> primaryGenesProcedures, ImpressService impressService) throws SolrServerException, IOException {

		this.impressService = impressService;

		List<PImageDTO> phisImages = new ArrayList<>();
		SolrQuery q = new SolrQuery();

		q.setQuery(PImageDTO.HOST_NAME + ":WTSI").setRows(Integer.MAX_VALUE); // WTSI -> brain histopath images
		phisImages.addAll(phisSolr.query(q).getBeans(PImageDTO.class));

		//q.setQuery(PImageDTO.HOST_NAME + ":\"IMPC Portal\"");
//		q.setFilterQueries("-" + PImageDTO.PROCEDURE + ":IMPC_XRY_001"); // WTSI already exported XRay images through Harwell. There are more images in PhIS but we need a strategy to avoid duplication
//		q.addFilterQuery("-" + PImageDTO.PROCEDURE + ":IMPC_EYE_001"); // They also exported IMPC_EYE_001, same as above

		//phisImages.addAll(phisSolr.query(q).getBeans(PImageDTO.class)); // IMPC Portal -> Sanger old images
		logger.info("PhenoImageshare images loaded: " + phisImages.size());

		return this.convertPhisImageToImages(phisImages, primaryGenesProcedures);

	}

	public List<PChannelDTO> getPhenoImageShareChannelDTOs(String imageId) throws IOException, SolrServerException {

	    SolrQuery query = new SolrQuery(PChannelDTO.ASSOCIATED_IMAGE + ":" + imageId);
        return phisChannelSolr.query(query).getBeans(PChannelDTO.class);

	}

	private List<ImageDTO> convertPhisImageToImages(List<PImageDTO> phisImages, Map<String, Set<String>> primaryGenesProcedures) throws IOException, SolrServerException {

		List<ImageDTO> images = new ArrayList<>();

		for (PImageDTO pImage : phisImages) {


			ImageDTO image = new ImageDTO();

			image.setObservationType(ObservationType.image_record.name());

            //TODO get gene symbol from allele2
			if(pImage.getGeneIds() != null && pImage.getGeneIds().size() >= 1){
				image.setGeneAccession(pImage.getGeneIds().get(0));
                if(pImage.getGeneIds().size() != 1){
                    logger.warn("MGI accession in Phis is not 1 for image.");
                }
			}

			if(pImage.getGeneSymbols() != null && pImage.getGeneSymbols().size() >= 1){
				image.setGeneSymbol(pImage.getGeneSymbols().get(0));
                if(pImage.getGeneSymbols().size() != 1){
                    logger.warn("Symbol in Phis is not 1 for image.");
                }
			}
			// Genes in expression images are stored in a different field. Need to check the expressed bag too
			// Allele and gene ids are stored in the same field as this is only used for querying. You can get them from the associated roi.
			if (pImage.getExpressedGfIdBag() != null && pImage.getExpressedGfIdBag().size() >= 1) {

				List<PChannelDTO> channelDTOs = getPhenoImageShareChannelDTOs(pImage.getId());
				if (channelDTOs.size() == 0) {
					logger.warn("No Channel doc found." + pImage.getId());
				} else {
					PChannelDTO channel = channelDTOs.get(0); // Sanger images have one channel only.AlleleIndex

					if (channel.getGeneId() != null) {
						image.setGeneAccession(channel.getGeneId());
					} else {
						logger.warn("No gene accession " + channel.getId());
					}
					//TODO get gene symbol from allele2
					if (channel.getGeneSymbol() != null) {
						image.setGeneSymbol(channel.getGeneSymbol());
					}

					if (channel.getGeneticFeatureId() != null) {
						image.setAlleleAccession(channel.getGeneticFeatureId());
					} else {
						logger.warn("No allele accession " + channel.getId());
					}
					//TODO get allele symbol from allele2
					if (channel.getGeneticFeatureSymbol() != null) {
						image.setAlleleSymbol(channel.getGeneticFeatureSymbol());
					}
				}
			}

			if(pImage.getGeneticFeatureIds() != null && pImage.getGeneticFeatureIds().size() >= 1){
				image.setAlleleAccession(pImage.getGeneticFeatureIds().get(0));
                if(pImage.getGeneticFeatureIds().size() != 1){
                    logger.warn("getGeneticFeatureIds (allele accession) in Phis is not 1 for one image.");
                }
			}

            //TODO get allele symbol from allele2
            if (pImage.getGeneticFeatureSymbols() != null && pImage.getGeneticFeatureSymbols().size() >= 1){
                image.setAlleleSymbol(pImage.getGeneticFeatureSymbols().get(0));
                if(pImage.getGeneticFeatureSymbols().size() != 1){
                    logger.warn("getGeneticFeatureSymbols (allele symbol) in Phis is not 1 for one image.");
                }
            }

			if(pImage.getPipeline() != null){
				image.setPipelineStableId(pImage.getPipeline());
			}

			ImpressBaseDTO procedure = getImpressLikeProcedureIds(pImage.getProcedure(), pImage.getObservations());
			image.setProcedureStableId(procedure.getStableId());
			image.setProcedureName(procedure.getName());

			ImpressBaseDTO parameter = getImpressLikeParameterIds(pImage.getParameter(), procedure);
			image.setParameterStableId(parameter.getStableId());
			image.setParameterName(parameter.getName());

			// Now that we have the gene and procedure information check if we want to load this image. If we have images from the same procedure  for the same gene from Harwell, we want to keep those.
			if (primaryGenesProcedures.containsKey(image.getGeneSymbol())&& primaryGenesProcedures.get(image.getGeneSymbol()).contains(image.getProcedureName().toLowerCase().replaceAll("[ _-]", ""))){

				continue;
			}

			image.setFullResolutionFilePath(pImage.getImageUrl());
			image.setJpegUrl(pImage.getImageUrl());
			image.setDownloadUrl(pImage.getImageUrl());
			image.setThumbnailUrl(pImage.getThumbnailUrl());

            if(pImage.getImageGeneratedBy() !=null){
                image.setPhenotypingCenter(pImage.getImageGeneratedBy().get(0));//getting the first in the list. will we have a problem with centers that are not in our list/db as centers e.g. for brain histopath - ?
            }

			// Add MP annotations
			if (pImage.getPhenotypeIdBag() != null) {
				image.setMpId(pImage.getPhenotypeIdBag());
			}
            if (pImage.getPhenotypeLabelBag() != null) {
                image.setMpTerm(pImage.getPhenotypeLabelBag());
            }

            // Anatomy terms, MA or EMAP for mouse
            // PhIS stores them separately because anatomy annotations can have different meanings
            if (pImage.getExpressionInIdBag() != null) {
                image.addAnatomyId(pImage.getExpressionInIdBag());
            }
            if(pImage.getExpressionInLabelBag() !=null){
                image.addAnatomyTerm(pImage.getExpressionInLabelBag());
            }

            // Anatomy terms, MA or EMAP for mouse
            if (pImage.getAbnormalAnatomyIdBag() != null) {
                image.addAnatomyId(pImage.getAbnormalAnatomyIdBag());
            }
            if(pImage.getAbnormalAnatomyTermBag() !=null){
                image.addAnatomyTerm(pImage.getAbnormalAnatomyTermBag());
            }

            // Anatomy terms, MA or EMAP for mouse
            if (pImage.getDepictedAnatomyIdBag() != null) {
                image.addAnatomyId(pImage.getDepictedAnatomyIdBag());
            }
            if(pImage.getDepictedAnatomyTermBag() !=null) {
                image.addAnatomyTerm(pImage.getDepictedAnatomyTermBag());
            }

			//what about mpath, emap and cmpo? no ids in the list of 163?
			if(image.getGeneAccession() != null) { // We have a described mutation
                image.setGroup(BiologicalSampleType.experimental.toString());
            }else{
				image.setGroup(BiologicalSampleType.control.toString());
			}

			if(pImage.getSex()!=null){
				String sex=pImage.getSex();
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
			}

			if(pImage.getExperimentGroup() != null){
				image.setExperimentId(pImage.getExperimentGroup());
			}

			if(pImage.getZygosity() != null && pImage.getZygosity().size() >= 1){
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
			} else {
				image.setZygosity(null);
			}

//			if(pImage.getStage() != null){
//				image.setStage(pImage.getStage());
//			}

			images.add(image);

		}

		return images;
	}


	private ImpressBaseDTO getImpressLikeParameterIds(String parameter, ImpressBaseDTO procedure){

		ImpressBaseDTO res =  new ImpressBaseDTO();
		if (parameter != null){
			if (parameter.contains("_")) {
				res.setStableId(parameter);
				try {
					ParameterDTO param = impressService.getParameterByStableId(parameter);
					if (param != null){
						res.setName(param.getName());
					}
				} catch (SolrServerException | IOException e) {
					logger.warn("No entry found for IMPRESS id " + parameter);
					e.printStackTrace();
				}
			} else {
				res.setStableId(procedure.getStableId() + "_param");
				res.setName(parameter);
			}
		} else {
			// We need a parameter id so we'll put the procedure - closest infor to a parameter we have.
			res.setStableId(procedure.getStableId() + "_param");
			res.setName(procedure.getName());
		}
		return res;
	}


	private ImpressBaseDTO getImpressLikeProcedureIds(String procedure, List<String> observation){
		// PhIS has only 1 field for procedure. If there is an IMPRESS procedure that maps well, it will contain the stable id, otherwise it has the name from the original source.
		// The observation field is free text and can contain a procedure name "Procedure: ______"
		ImpressBaseDTO res = new ImpressBaseDTO();
		if (procedure.contains("_")){ // IMPRESS stable id
			res.setStableId(procedure.trim());
		} else {
			// we need to fabricate a PHIS-style id.
			String stableId = "PhIS_";
			if (procedure.trim().equalsIgnoreCase("Wholemount Expression")){
				stableId += "ALZ_";
				res.setName("Adult LacZ"); // we need the same name so they group together in the UI
			} else if (procedure.trim().equalsIgnoreCase("Xray")){
				stableId += "XRY_";
				res.setName("X-ray");
			} else if (procedure.trim().equalsIgnoreCase("Histology Slide")){ // Checked with Nat, we want to collapse this with histopathology
				stableId += "HIS_";
				res.setName("Histopathology");
			}else if (procedure.trim().equalsIgnoreCase("Dysmorphology")){
				stableId += "CSD_";
				res.setName("Combined SHIRPA and Dysmorphology");
			}
			stableId += procedure.trim().replaceAll(" ", "_");
			res.setStableId(stableId);
		}

		if (res.getName() == null){
			if (!procedure.contains("_")){
				res.setName(procedure.trim());
			} else {
				if(observation != null){
					observation.stream().filter(item -> { return !item.contains("Procedure name: ");});
					if (observation.size() > 0) {
						res.setName(observation.get(0).trim().replace("Procedure name: ", ""));
					}
				} else res.setName(procedure);
			}
		}
		return res;
	}

}
