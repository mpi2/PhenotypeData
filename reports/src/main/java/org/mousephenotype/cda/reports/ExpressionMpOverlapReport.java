package org.mousephenotype.cda.reports;

import java.beans.Introspector;
import java.io.IOException;
import java.lang.reflect.MalformedParameterizedTypeException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.mousephenotype.cda.reports.support.ReportException;
import org.mousephenotype.cda.solr.repositories.image.Image;
import org.mousephenotype.cda.solr.repositories.image.ImagesSolrDao;
import org.mousephenotype.cda.solr.service.GeneService;
import org.mousephenotype.cda.solr.service.ImageService;
import org.mousephenotype.cda.solr.service.MaService;
import org.mousephenotype.cda.solr.service.MpService;
import org.mousephenotype.cda.solr.service.PostQcService;
import org.mousephenotype.cda.solr.service.dto.GeneDTO;
import org.mousephenotype.cda.solr.service.dto.GenotypePhenotypeDTO;
import org.mousephenotype.cda.solr.service.dto.ImageDTO;
import org.mousephenotype.cda.solr.service.dto.MaDTO;
import org.mousephenotype.cda.solr.service.dto.MpDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 
 * @author ilinca
 * @since 2016/02/25
 */
@Component
public class ExpressionMpOverlapReport extends AbstractReport {

	protected Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private ImageService imageService;

	@Autowired
	private ImagesSolrDao wtsiImageService;

	@Autowired
	private PostQcService postqcService;
	
	@Autowired
	private MpService mpService;
	
	@Autowired
	private GeneService geneService;
	
	@Autowired
	private MaService maService;
	
	public ExpressionMpOverlapReport() {
		super();
	}

	@Override
	public String getDefaultFilename() {
		return Introspector.decapitalize(ClassUtils.getShortClassName(this.getClass()));
	}
	
	public void run(String[] args) 
	throws ReportException, SolrServerException {

	        List<String> errors = parser.validate(parser.parse(args));
	        if ( ! errors.isEmpty()) {
	            logger.error("ExpressionMpOverlapReport parser validation error: " + StringUtils.join(errors, "\n"));
	            return;
	        }
	        initialise(args);

	        long start = System.currentTimeMillis();

	        List<ImageDTO> impcLacz = imageService.getImagesForLacZ();
	        Set<String> geneMaLaczCombiantions = new HashSet<>();
	        Set<String> geneMaFromPhenotypeCombiantions = new HashSet<>();
	        Map<String, Set<String>> geneMpAssocMap = new HashMap<>();
	        
	        // Get gene-MA associations
	        for (ImageDTO doc : impcLacz){
	        	List<String> maIds = doc.getMaTermId();
	        	for (String maId : maIds){
	        		geneMaLaczCombiantions.add(doc.getGeneAccession() + "_" + maId);
	        	}
	        }
	        Iterator<SolrDocument> i = wtsiImageService.getImagesForLacZ().iterator();
	        while (i.hasNext()){
	        	SolrDocument doc = i.next();
	        	for (Object maId : doc.getFieldValues("ma_id")){
	        		geneMaLaczCombiantions.add(doc.getFieldValue("accession") + "_" + maId);
	        	}
	        }
	        System.out.println("------ geneMaCombiantions: " + geneMaLaczCombiantions.size());
	        
	        // Get gene-MP associations
	        List<GenotypePhenotypeDTO> mpCalls = postqcService.getAllGenotypePhenotypes(null); // Duplicate gene-mp paris.
	        
	        // Get MP-MA mappings
	        Map<String, List<String>> mpMaMap = new HashMap<>();
	        for (MpDTO doc :  mpService.getAllMpWithMaMapping()){
	        	mpMaMap.put(doc.getMpId(), doc.getInferredMaTermId());
	        }
	        System.out.println("MAP_MP map : " + mpMaMap);
	        
	        // Convert gene-MP mappings to gene-MA
	        for (GenotypePhenotypeDTO geneDto: mpCalls){
	        	if (mpMaMap.containsKey(geneDto.getMpTermId())){
	        		for (String ma : mpMaMap.get(geneDto.getMpTermId())){
	        			geneMaFromPhenotypeCombiantions.add(geneDto.getMarkerAccessionId() + "_" + ma);
	        		}
	        	}
	        }
	        
	        System.out.println("------ geneMaFromPhenotypeCombiantions: " + geneMaFromPhenotypeCombiantions.size());
	        
	        geneMaFromPhenotypeCombiantions.retainAll(geneMaLaczCombiantions);   

	        System.out.println("------ IN COMMON : " + geneMaFromPhenotypeCombiantions.size());
	        
	        List<String[]> result = new ArrayList<>();
        	result.add(new String[] { "Gene Id", "Gene Symbol", "Ma Id", "Ma Term" });
	        for (String s: geneMaFromPhenotypeCombiantions){
	        	String maId = s.split("_")[1];
	        	String geneId = s.split("_")[0];
	        	String maTerm = maService.getMaTerm(maId).getMaTerm();
	        	String geneSymbol = geneService.getGeneById(geneId, GeneDTO.MARKER_SYMBOL).getMarkerSymbol();
	        	result.add(new String[] { geneId, geneSymbol, maId, maTerm });
	        }
	        
	        csvWriter.writeAll(result);

	        try {
	            csvWriter.close();
	        } catch (IOException e) {
	            throw new ReportException("Exception closing csvWriter: " + e.getLocalizedMessage());
	        }

	        log.info(String.format("Finished. [%s]", commonUtils.msToHms(System.currentTimeMillis() - start)));
	    }
	
//	
//	public getCommonClosestAncestor(MaDTO ma1, MaDTO ma2, integer level){
//		if (ma1.getMaId().equalsIgnoreCase(ma2.getMaId())){
//			System.out.println("Found common acnestor at level " + level + "(" + ma1.getMaTerm() + ")"  );
//			return level;
//		}
//		
//				
//	}

}
