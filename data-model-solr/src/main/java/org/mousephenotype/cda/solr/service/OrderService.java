package org.mousephenotype.cda.solr.service;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.mousephenotype.cda.enumerations.OrderType;
import org.mousephenotype.cda.solr.service.dto.Allele2DTO;
import org.mousephenotype.cda.solr.service.dto.ProductDTO;
import org.mousephenotype.cda.solr.web.dto.OrderTableRow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OrderService {

	private final Logger logger    = LoggerFactory.getLogger(this.getClass());
	public static String selectCre = "/selectCre";
	public static String crePredicate = "allele_design_project:Cre";

	private SolrClient allele2Core;
	private SolrClient productCore;


	@Inject
	public OrderService(SolrClient allele2Core, SolrClient productCore) {
		this.allele2Core = allele2Core;
		this.productCore = productCore;
	}

	public OrderService() {

	}


	public List<OrderTableRow> getOrderTableRows(String acc, Integer rows, boolean creLine) throws SolrServerException, IOException {
		List<OrderTableRow> orderTableRows = new ArrayList<>();
		List<Allele2DTO> allele2DTOs = this.getAllele2DTOs(acc, rows, creLine);

		for (Allele2DTO allele : allele2DTOs) {
			OrderTableRow row = new OrderTableRow();	
			String alleleName = allele.getAlleleName();
			row.setAlleleName(alleleName);
			row.setAlleleDescription(allele.getAlleleDescription());
			row.setTargetingVectorAvailable(allele.getTargetingVectorAvailable());
			row.setEsCellAvailable(allele.getEsCellAvailable());
			row.setMouseAvailable(allele.getMouseAvailable());
			row.setMarkerSymbol(allele.getMarkerSymbol());
			row.setType(allele.getType());
			row.setVectorMapLink(allele.getVectorAlleleImage());
			row.setVectorGenbankLink(allele.getVectorGenbankLink());
			row.setGeneMapLink(allele.getAlleleSimpleImage());
			row.setGeneGenbankLink(allele.getGenbankFile());
			row.setMgiAccessionId(allele.getMgiAccessionId());
			
			// Tissue inquiries
			row.setTissuesAvailable(allele.getTissuesAvailable());
			row.setTissueTypes(allele.getTissueTypes());
			row.setTissueEnquiryLinks(allele.getTissueEnquiryLinks());

			orderTableRows.add(row);
		}

		return orderTableRows;
	}

	public List<Allele2DTO> getAllele2DTOs(String geneAcc, Integer rows, boolean creLine) throws SolrServerException, IOException {
		
		String q = "*:*";// default if no gene specified
		if (geneAcc != null) {
			q = "mgi_accession_id:\"" + geneAcc + "\"";// &start=0&rows=100&hl=true&wt=json";
		}
		SolrQuery query = new SolrQuery();
		if(creLine){
			query.setRequestHandler(selectCre);
		}
		query.setQuery(q);
		query.addFilterQuery("type:Allele");
		query.addFilterQuery("("+Allele2DTO.ES_CELL_AVAILABLE+":true OR "+Allele2DTO.TARGETING_VECTOR_AVAILABLE+":true OR "+Allele2DTO.MOUSE_AVAILABLE+":true)" );
		query.set("sort", "marker_symbol asc");
		if(rows!=null){
			query.setRows(rows);
		}else{
			query.setRows(Integer.MAX_VALUE);
		}
		
		QueryResponse response = allele2Core.query(query);
		List<Allele2DTO> allele2DTOs = response.getBeans(Allele2DTO.class);

		return allele2DTOs;

	}
	
	public boolean crelineAvailable(String geneAccession) throws SolrServerException, IOException{
		boolean creLineAvailable=false;
		boolean searchCreline=true;
		List<Allele2DTO> rows=this.getAllele2DTOs(geneAccession, 1, searchCreline);
		if(rows.size()>0){
			creLineAvailable= true;
		}
		return creLineAvailable;
	}

	public Allele2DTO getAlleForGeneAndAllele(String acc, String allele, boolean creline) throws SolrServerException, IOException {
		String q = "*:*";// default if no gene specified
		if (acc != null) {
			q = "mgi_accession_id:\"" + acc + "\"";// &start=0&rows=100&hl=true&wt=json";
		}
		SolrQuery query = new SolrQuery();
		if(creline){
			query.setRequestHandler(selectCre);
		}
		query.setQuery(q);
		query.addFilterQuery("type:Allele");
		query.addFilterQuery("allele_name:\"" + allele + "\"");
		query.setRows(1);
		//logger.info("query for alleles=" + query);
		QueryResponse response = allele2Core.query(query);
		logger.info("number found of allele2 docs=" + response.getResults().getNumFound());
		List<Allele2DTO> allele2DTOs = response.getBeans(Allele2DTO.class);
		logger.info("number of alleles should be 1 but is " + allele2DTOs.size());
		System.out.println("request = "+query);
		return allele2DTOs.get(0);
	}

	protected Map<String, List<ProductDTO>> getProductsForAllele(String alleleName) throws SolrServerException, IOException {
		return this.getProducts(null, alleleName, null, false);
	}

	public Map<String, List<ProductDTO>> getProductsForGene(String geneAcc) throws SolrServerException, IOException {
		return this.getProducts(geneAcc, null, null, false);
	}

	public Map<String, List<ProductDTO>> getStoreNameToProductsMap(String geneAcc, String alleleName,
			OrderType productType, boolean creLine) throws SolrServerException, IOException {
		List<ProductDTO> productList = null;
		Map<String, List<ProductDTO>> productsMap = this.getProducts(geneAcc, alleleName, productType, creLine);
		if (productsMap.keySet().size() > 1) {
			System.err.println("more than one key for products - should only be one");
		}
		for (String key : productsMap.keySet()) {// just get a list of products
			productList = productsMap.get(key);
		}

		HashMap<String, List<ProductDTO>> orderNameToProductList = new HashMap<String, List<ProductDTO>>();
		if (productList != null) {
			for (ProductDTO prod : productList) {
				if (prod.getOrderNames() != null) {
					for (String orderName : prod.getOrderNames()) {
						if (!orderNameToProductList.containsKey(orderName)) {
							orderNameToProductList.put(orderName, new ArrayList<ProductDTO>());
						}
						orderNameToProductList.get(orderName).add(prod);
					}
				}else{
					System.err.println("No order names were found for this product "+prod.getAlleleName()+" this is unusual and should not happen");
				}
			}
		}
		return orderNameToProductList;
	}

	protected Map<String, List<ProductDTO>> getProducts(String geneAcc, String alleleName, OrderType productType, boolean creLine)
			throws SolrServerException, IOException {
		Map<String, List<ProductDTO>> alleleNameToProductsMap = new HashMap<>();
		String q = "*:*";
		if (geneAcc != null) {
			q = "mgi_accession_id:\"" + geneAcc + "\"";
		}

		SolrQuery query = new SolrQuery();
		if(creLine){
			query.addFilterQuery(crePredicate);
		}
		query.setQuery(q);
		if (alleleName != null) {
			query.addFilterQuery("allele_name:\"" + alleleName + "\"");
		}
		query.setRows(Integer.MAX_VALUE);
		if (productType != null) {
			query.addFilterQuery("type:" + productType);
		}
		
		query.addFilterQuery("production_completed:true");

		QueryResponse response = productCore.query(query);
		List<ProductDTO> productDTOs = response.getBeans(ProductDTO.class);

		for (ProductDTO prod : productDTOs) {
			if (!alleleNameToProductsMap.containsKey(prod.getAlleleName())) {
				alleleNameToProductsMap.put(prod.getAlleleName(), new ArrayList<>());
			}
			alleleNameToProductsMap.get(prod.getAlleleName()).add(prod);
		}

		return alleleNameToProductsMap;

	}

	/**
	 * 
	 * @param type es_cell or mouse etc
	 * @param productName e.g. EPD0386_3_A05
	 * @param alleleName 
	 * @param creLine TODO
	 * @return 
	 * @throws SolrServerException 
	 */
	public HashMap<String, HashMap<String, List<String>>> getProductQc(OrderType type, String productName, String alleleName, boolean creLine) throws SolrServerException, IOException {
		ProductDTO prod=null;
		List<String>qcData=null;
		SolrQuery query = new SolrQuery();
		if(creLine){
			query.addFilterQuery(crePredicate);
		}
		String q="name:"+productName;
		query.setQuery(q);
		if (type != null) {
			query.addFilterQuery("type:" + type);
		}
		if (alleleName != null) {
			query.addFilterQuery(ProductDTO.ALLELE_NAME+":\"" + alleleName+"\"");
		}
		query.setRows(Integer.MAX_VALUE);
		logger.info("query for products=" + query);
		QueryResponse response = productCore.query(query);
		logger.info("number found of products docs=" + response.getResults().getNumFound());
		List<ProductDTO> productDTOs = response.getBeans(ProductDTO.class);
		logger.info("number of productDTOs is " + productDTOs.size());
		if(productDTOs.size()>1){
			System.err.println("too many products returned for qc method");
		}else{
			prod=productDTOs.get(0);
			qcData=prod.getQcData();
		}
		for(String qc:qcData){
			logger.info("qc="+qc);
		}
		
		HashMap<String, HashMap<String, List<String>>> qcMap = extractQcData(qcData);
		return qcMap;
		
	}
	
	/**
	 * method copied from Peters code
	 * @return
	 */
	 private HashMap<String, HashMap<String, List<String>>> extractQcData(List<String> qcStrings) {
	        HashMap<String, HashMap<String, List<String>>> deep = new HashMap<>();

	        
	        for (int j = 0; j < qcStrings.size(); j++) {
	            String[] qc = qcStrings.get(j).split(":");

	            String qc_group = qc != null && qc.length > 0 ? qc[0] : "";
	            String qc_type = qc != null && qc.length > 1 ? qc[1] : "";
	            String qc_result = qc != null && qc.length > 2 ? qc[2] : "";

	            if (!deep.containsKey(qc_group)) {
	                deep.put(qc_group, new HashMap<String, List<String>>());
	                deep.get(qc_group).put("fieldNames", new ArrayList<>());
	                deep.get(qc_group).put("values", new ArrayList<>());
	            }
	            deep.get(qc_group).get("fieldNames").add(qc_type.replace("_", " "));
	            deep.get(qc_group).get("values").add(qc_result);
	        }
	        return deep;
	    }
	 

}