package org.mousephenotype.cda.reports;

import java.beans.Introspector;
import java.io.IOException;
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
import org.mousephenotype.cda.reports.support.ReportException;
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
	
	private Map<String, MaDTO> maMap = new HashMap<>();
	
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
	        Map<String, Mapping> geneMaLaczCombiantions = new HashMap<>();
	        Map<String, Mapping> geneMaFromPhenotypeCombiantions = new HashMap<>();
	        Set<String> maFromLacz = new HashSet<>();
	        Set<String> maFromMp = new HashSet<>();
	        Map<String, Set<String>> geneMpAssocMap = new HashMap<>();
	        
	        // Get gene-MA associations
	        for (ImageDTO doc : impcLacz){
	        	List<String> maIds = doc.getMaTermId();
	        	for (String maId : maIds){
	        		geneMaLaczCombiantions.put(doc.getGeneAccession() + "_" + maId, new Mapping(doc.getGeneAccession(), maId));
	        		maFromLacz.add(maId);
	        	}
	        }
	        Iterator<SolrDocument> i = wtsiImageService.getImagesForLacZ().iterator();
	        while (i.hasNext()){
	        	SolrDocument doc = i.next();
	        	for (Object maId : doc.getFieldValues("ma_id")){
	        		geneMaLaczCombiantions.put(doc.getFieldValue("accession") + "_" + maId, new Mapping(doc.getFieldValue("accession").toString(), maId.toString()));
	        		maFromLacz.add(maId.toString());
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
	        			geneMaFromPhenotypeCombiantions.put(geneDto.getMarkerAccessionId() + "_" + ma, new Mapping(geneDto.getMarkerAccessionId(), ma));
	        			maFromMp.add(ma);
	        		}
	        	}
	        }
	        
	        System.out.println("------ geneMaFromPhenotypeCombiantions: " + geneMaFromPhenotypeCombiantions.size());
	        
	        Set<String> common = new HashSet<>(geneMaFromPhenotypeCombiantions.keySet());
	        common.retainAll(geneMaLaczCombiantions.keySet());   

	        System.out.println("------ IN COMMON : " + common.size());
	        
	        List<String[]> result = new ArrayList<>();
        	result.add(new String[] { "Gene Id", "Gene Symbol", "Ma Id", "Ma Term" });
	        for (String s: common){
	        	String maId = s.split("_")[1];
	        	String geneId = s.split("_")[0];
	        	String maTerm = getMa(maId).getMaTerm();
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
	        
	        for (Mapping m1: geneMaLaczCombiantions.values()){
	        	for (Mapping m2: geneMaFromPhenotypeCombiantions.values()){
	        		if (m1.getMgiAccession().equalsIgnoreCase(m2.getMgiAccession())){
	        			// System.out.println("--- " + m1.getMaId() + " " + m2.getMaId() + " ---");
	        			Integer res = getCommonClosestAncestor(getMa(m1.getMaId()), getMa(m2.getMaId()), 0); 
	        			if ( res != null && res>= 0){
	        				System.out.println(m1.getMgiAccession());
	        			};
	        		}
	        	}
	        }
	    }
	
	
	public Integer getCommonClosestAncestor(MaDTO ma1, MaDTO ma2, Integer level) 
	throws SolrServerException{
		
//		System.out.println("+++++ " + level + " " + ma1.getMaTerm() +  "     " + ma2.getMaTerm());
		
		if (ma1.getMaId().equalsIgnoreCase(ma2.getMaId())){
			System.out.println("Found common ancestor at level " + level + "(" + ma1.getMaTerm() + ")"  );
			return level;
		}
		
		if (level > 7){return -2;}
		
		List<String> parents1 = ma1.getParentMaId();
		List<String> parents2 = ma2.getParentMaId();
		level++;
		if (parents1 == null){
			parents1 = new ArrayList<>();
		}
		if (parents2 == null){
			parents2 = new ArrayList<>();
		}
		if (parents1.contains(ma2.getMaId())){
			System.out.println("Found common ancestor at level " + level + "(" + ma2.getMaTerm() + ")"  );
			return level;
		}
		if (parents2.contains(ma1.getMaId())){
			System.out.println("Found common ancestor at level " + level + "(" + ma1.getMaTerm()+ ")"  );
			return level;
		}
		if( inCommon(parents1, parents2).size() > 0){
			System.out.println("Found common ancestor at level " + level + "(" + getMa(inCommon(parents1, parents2).get(0)).getMaTerm() + ")"  ); // Empirically I observed there was always just one class in the intersection for our dataset. This might change though.
			return level;
		}
		for (String m1: parents1){
			MaDTO ma1Dto = getMa(m1);
			Integer l = getCommonClosestAncestor(ma1Dto, ma2, level);
			if (l >= 0 || l != -2){ // We found something OR we already checked too many levels
				return l;
			}
			for (String m2: parents2){
				MaDTO ma2Dto = getMa(m2);
				l = getCommonClosestAncestor(ma1Dto, ma2Dto, level);
				if (l >= 0 || l != -2){
					return l;
				}
			}
		}
		for (String m2: parents2){
			MaDTO ma2Dto = getMa(m2);
			Integer l = getCommonClosestAncestor(ma2Dto, ma1, level);
			if (l >= 0 || l != -2){
				return l;
			}
		}
		return -1;
	}

	public List<String>  inCommon (List<String> a, List<String>b){
		
		List<String> newList = new ArrayList<>(a);
		newList.retainAll(b);
		return newList;
	}
	
	public MaDTO getMa(String maId) 
	throws SolrServerException{
		if (!maMap.containsKey(maId)){
			maMap.put(maId, maService.getMaTerm(maId));
		}
		return maMap.get(maId);
	}
	
	
	private class Mapping{
		
		String mgiAccession;
		String maId;
		
		public Mapping(String mgiAccession, String maId){			
			this.mgiAccession = mgiAccession;
			this.maId = maId;
		}
		
		public String getMgiAccession() {
			return mgiAccession;
		}
		public void setMgiAccession(String mgiAccession) {
			this.mgiAccession = mgiAccession;
		}
		public String getMaId() {
			return maId;
		}
		public void setMaId(String maId) {
			this.maId = maId;
		}
		
	}
	
}
