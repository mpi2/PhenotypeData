package org.mousephenotype.cda.reports;

import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrDocument;
import org.mousephenotype.cda.reports.support.ReportException;
import org.mousephenotype.cda.solr.repositories.image.ImagesSolrDao;
import org.mousephenotype.cda.solr.service.*;
import org.mousephenotype.cda.solr.service.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.beans.Introspector;
import java.io.IOException;
import java.util.*;

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

	        // Get gene-MA associations
	        for (ImageDTO doc : impcLacz){
	        	List<String> maIds = doc.getMaId();
	        	for (String maId : maIds){
	        		geneMaLaczCombiantions.put(doc.getGeneAccession() + "_" + maId, new Mapping(doc.getGeneAccession(), maId, null));
	        		maFromLacz.add(maId);
	        	}
	        }
	        Iterator<SolrDocument> i = wtsiImageService.getImagesForLacZ().iterator();
	        while (i.hasNext()){
	        	SolrDocument doc = i.next();
	        	for (Object maId : doc.getFieldValues("ma_id")){
	        		geneMaLaczCombiantions.put(doc.getFieldValue("accession") + "_" + maId, new Mapping(doc.getFieldValue("accession").toString(), maId.toString(), null));
	        		maFromLacz.add(maId.toString());
	        	}
	        }

	        // Get gene-MP associations
	        List<GenotypePhenotypeDTO> mpCalls = postqcService.getAllGenotypePhenotypes(null); // Duplicate gene-mp paris.

	        // Get MP-MA mappings
	        Map<String, List<String>> mpMaMap = new HashMap<>();
	        for (MpDTO doc :  mpService.getAllMpWithMaMapping()){
	        	mpMaMap.put(doc.getMpId(), doc.getInferredMaTermId());
	        }

	        // Convert gene-MP mappings to gene-MA
	        for (GenotypePhenotypeDTO geneDto: mpCalls){
	        	if (mpMaMap.containsKey(geneDto.getMpTermId())){
	        		for (String ma : mpMaMap.get(geneDto.getMpTermId())){
	        			geneMaFromPhenotypeCombiantions.put(geneDto.getMarkerAccessionId() + "_" + ma, new Mapping(geneDto.getMarkerAccessionId(), ma, geneDto.getMpTermId()));
	        			maFromMp.add(ma);
	        		}
	        	}
	        }

	        // Get common ancestors for geneX-ma1 and geneX-ma2 associations
	        List<String[]> result = new ArrayList<>();
	        result.add(new String[] { "Gene Id", "Gene Symbol", "Expression MA Id", "Expression MA", "MP Id", "MP Term", "Pthenotype MA Id",
	        							"Phenotype MA Term", "Common MA Id", "Common MA Term", "Level" });
	        for (Mapping m1: geneMaLaczCombiantions.values()){
	        	for (Mapping m2: geneMaFromPhenotypeCombiantions.values()){
	        		if (m1.getMgiAccession().equalsIgnoreCase(m2.getMgiAccession())){
	        			CommonAncestor inCommon = getCommonClosestAncestor(getMa(m1.getMaId()), getMa(m2.getMaId()), 0);
	        			if ( inCommon != null && inCommon.getLevel() >= 0){
	        				for (MaDTO ancestor: inCommon.getAncestors()){
	        		        	String geneSymbol = geneService.getGeneById(m1.getMgiAccession(), GeneDTO.MARKER_SYMBOL).getMarkerSymbol();
	        					result.add(new String[] { m1.getMgiAccession(), geneSymbol, m1.maId, getMa(m1.maId).getMaTerm(),
	        							(m2.mpId != null) ? m2.mpId : "", (m2.mpId != null) ? mpService.getPhenotype(m2.mpId).getMpTerm() : "",
	        							m2.maId, getMa(m2.getMaId()).getMaTerm(), ancestor.getMaId(), ancestor.getMaTerm(), "" + inCommon.getLevel() });
	        				}
	        			}
	        		}
	        	}
	        }

	        csvWriter.writeAll(result);
	        try {
	            csvWriter.close();
	        } catch (IOException e) {
	            throw new ReportException("Exception closing csvWriter: " + e.getLocalizedMessage());
	        }

	        logger.info("Expression overlap report took " + (System.currentTimeMillis() - start) + "ms to complete.");

	}


	public CommonAncestor getCommonClosestAncestor(MaDTO ma1, MaDTO ma2, Integer level)
	throws SolrServerException{

		if (ma1.getMaId().equalsIgnoreCase(ma2.getMaId())){
			return new CommonAncestor(level, ma1);
		}

		if (level > 7){return new CommonAncestor(-2, new MaDTO());}

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
			return new CommonAncestor(level, ma2);
		}
		if (parents2.contains(ma1.getMaId())){
			return new CommonAncestor(level, ma1);
		}
		if( inCommon(parents1, parents2).size() > 0){
			return new CommonAncestor(level, inCommon(parents1, parents2));
		}
		for (String m1: parents1){
			MaDTO ma1Dto = getMa(m1);
			CommonAncestor ancestor = getCommonClosestAncestor(ma1Dto, ma2, level);
			if (ancestor.getLevel() >= 0 || ancestor.getLevel() != -2){ // We found something OR we already checked too many levels
				return ancestor;
			}
			for (String m2: parents2){
				MaDTO ma2Dto = getMa(m2);
				ancestor = getCommonClosestAncestor(ma1Dto, ma2Dto, level);
				if (ancestor.getLevel() >= 0 || ancestor.getLevel() != -2){
					return ancestor;
				}
			}
		}
		for (String m2: parents2){
			MaDTO ma2Dto = getMa(m2);
			CommonAncestor ancestor = getCommonClosestAncestor(ma2Dto, ma1, level);
			if (ancestor.getLevel() >= 0 || ancestor.getLevel() != -2){
				return ancestor;
			}
		}
		return new CommonAncestor(-1, new MaDTO());
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
		String mpId;

		public Mapping(String mgiAccession, String maId, String mpId){
			this.mgiAccession = mgiAccession;
			this.maId = maId;
			this.mpId = mpId;
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
		public String getMpId(){
			return mpId;
		}
	}

	private class CommonAncestor{

		int level;
		List<MaDTO> ancestors;

		public CommonAncestor(int level, String ancestorId)
		throws SolrServerException{
			this.level = level;
			this.ancestors = new ArrayList<>();
			this.ancestors.add(maService.getMaTerm(ancestorId));
		}

		public CommonAncestor(int level, MaDTO ancestor) {
			this.level = level;
			this.ancestors = new ArrayList<>();
			this.ancestors.add(ancestor);
		}

		public CommonAncestor(int level, List<String> ancestorIds)
		throws SolrServerException {
			this.level = level;
			this.ancestors = new ArrayList<>();
			for (String a : ancestorIds){
				this.ancestors.add(maService.getMaTerm(a));
			}
		}

		public int getLevel() {
			return level;
		}

		public void setLevel(int level) {
			this.level = level;
		}

		public List<MaDTO>  getAncestors() {
			return ancestors;
		}

		public void setAncestors(List<MaDTO>  ancestorId) {
			this.ancestors = ancestors;
		}
	}

}
