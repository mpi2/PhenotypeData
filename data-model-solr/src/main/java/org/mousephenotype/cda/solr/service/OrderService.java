package org.mousephenotype.cda.solr.service;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.mousephenotype.cda.enumerations.OrderType;
import org.mousephenotype.cda.solr.service.dto.Allele2DTO;
import org.mousephenotype.cda.solr.service.dto.ProductDTO;
import org.mousephenotype.cda.solr.web.dto.OrderTableRow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OrderService {

	@Autowired
	@Qualifier("allele2Core")
	private SolrClient allele2Core;

	@Autowired
	@Qualifier("productCore")
	private SolrClient productCore;
	
	 @Value("${imits.solr.host}")
	 private String IMITS_SOLR_CORE_URL;
	 
	 private String selectCre="/selectCre";

	public List<OrderTableRow> getOrderTableRows(String acc, Integer rows, boolean creLine) throws SolrServerException, IOException {
		List<OrderTableRow> orderTableRows = new ArrayList<>();
		List<Allele2DTO> allele2DTOs = this.getAllele2DTOs(acc, rows, creLine);
		System.out.println("size of allele2DTOS"+allele2DTOs.size());

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
			orderTableRows.add(row);

		}

		return orderTableRows;
	}

	protected List<Allele2DTO> getAllele2DTOs(String geneAcc, Integer rows, boolean creLine) throws SolrServerException, IOException {
		
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
		
		if(rows!=null){
			query.setRows(rows);
		}else{
			query.setRows(Integer.MAX_VALUE);
		}
		
		System.out.println("query for allele2DTOs=" + query);
		QueryResponse response = allele2Core.query(query);
		System.out.println("number found of allele2 docs=" + response.getResults().getNumFound());
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
		//System.out.println("query for alleles=" + query);
		QueryResponse response = allele2Core.query(query);
		System.out.println("number found of allele2 docs=" + response.getResults().getNumFound());
		List<Allele2DTO> allele2DTOs = response.getBeans(Allele2DTO.class);
		System.out.println("number of alleles should be 1 but is " + allele2DTOs.size());
		return allele2DTOs.get(0);
	}

	protected Map<String, List<ProductDTO>> getProductsForAllele(String alleleName) throws SolrServerException, IOException {
		return this.getProducts(null, alleleName, null, false);
	}

	protected Map<String, List<ProductDTO>> getProductsForGene(String geneAcc) throws SolrServerException, IOException {
		return this.getProducts(geneAcc, null, null, false);
	}

	public Map<String, List<ProductDTO>> getStoreNameToProductsMap(String geneAcc, String alleleName, OrderType productType, boolean creLine)  throws SolrServerException, IOException {
		List<ProductDTO> productList = null;
		Map<String, List<ProductDTO>> productsMap = this.getProducts(geneAcc, alleleName, productType, creLine);
		if (productsMap.keySet().size() > 1) {
			System.err.println("more than one key for products - should only be one");
		}
		for (String key : productsMap.keySet()) {//just get a list of products
			productList = productsMap.get(key);
		}

		HashMap<String, List<ProductDTO>> orderNameToProductList = new HashMap<String, List<ProductDTO>>();
		if (productList != null) {
			for (ProductDTO prod : productList) {
				for (String orderName : prod.getOrderNames()) {
					if (!orderNameToProductList.containsKey(orderName)) {
						orderNameToProductList.put(orderName, new ArrayList<ProductDTO>());
					}
					orderNameToProductList.get(orderName).add(prod);
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
			query.setRequestHandler(selectCre);
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
			query.setRequestHandler(selectCre);
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
		System.out.println("query for products=" + query);
		QueryResponse response = productCore.query(query);
		System.out.println("number found of products docs=" + response.getResults().getNumFound());
		List<ProductDTO> productDTOs = response.getBeans(ProductDTO.class);
		System.out.println("number of productDTOs is " + productDTOs.size());
		if(productDTOs.size()>1){
			System.err.println("too many products returned for qc method");
		}else{
			prod=productDTOs.get(0);
			qcData=prod.getQcData();
		}
		for(String qc:qcData){
			System.out.println("qc="+qc);
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
	 
	 
	 public HashMap<String, String> getCreData(String acc) throws SolrServerException, IOException {
		 //method to get the link at the bottom of the table if we have old cre mice available from the other core eucommProduct
		 HashMap<String, String> creStatus = new HashMap<>();// a bit lazy but have just used the same structure and logic her that peter used
	        creStatus.put("cre_exists", "false");
	        creStatus.put("product_type", "None");
	        creStatus.put("mgi_acc", "");
	        
	        
		 SolrQuery query = new SolrQuery();
			String q="mgi_accession_id:\""+acc+"\"";
			query.setQuery(q);
			
			query.addFilterQuery("(type:mouse OR type:es_cell)");
			
			query.setRows(Integer.MAX_VALUE);

		 //
		 // TODO: Update for displaying the CRELINE products at the bottom of the gene page
		 //
			
			System.out.println("query for cre  products=" + query);
			QueryResponse response = productCore.query(query);
			System.out.println("number found of products docs=" + response.getResults().getNumFound());
			List<ProductDTO> productDTOs = response.getBeans(ProductDTO.class);
			for(ProductDTO prod:productDTOs){
				 String creType = prod.getType();
	               if (creType.equals("mouse")) {
	                   creStatus.put("cre_exists", "true");
	                   creStatus.put("product_type", "Mice");
	                   creStatus.put("mgi_acc", prod.getMgiAccessionId());
	               }
	               if (creType.equals("es_cell")) {
	                   if (!creStatus.get("product_type").equals("Mice") ){//only do this if no mice found already as mice more important I guess.
	                       creStatus.put("cre_exists", "true");
	                       creStatus.put("product_type", "ES Cell");
	                       creStatus.put("mgi_acc", prod.getMgiAccessionId());
	                   }
	               }
			}
			return creStatus;
	 }

}
