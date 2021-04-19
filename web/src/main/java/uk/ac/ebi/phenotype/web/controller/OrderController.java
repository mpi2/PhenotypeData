package uk.ac.ebi.phenotype.web.controller;

import org.apache.solr.client.solrj.SolrServerException;
import org.mousephenotype.cda.enumerations.OrderType;
import org.mousephenotype.cda.solr.service.OrderService;
import org.mousephenotype.cda.solr.service.dto.Allele2DTO;
import org.mousephenotype.cda.solr.service.dto.ProductDTO;
import org.mousephenotype.cda.solr.web.dto.OrderTableRow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class OrderController {
	
	@Autowired
	OrderService orderService;
		
	/**
	 * 
	 * @param allele e.g. Thpotm1(KOMP)Vlcg
	 * @param model
	 * @param request
	 * @param attributes
	 * @return
	 * @throws SolrServerException, IOException
	 */
	@RequestMapping("/order")
	public String order(@RequestParam (required = true) String acc, @RequestParam (required = true) String allele,  @RequestParam (required = true) String type, @RequestParam(required=false, defaultValue="false") boolean creLine, 
			Model model, HttpServletRequest request, RedirectAttributes attributes) throws SolrServerException, IOException {
		System.out.println("orderVector being called with acc="+acc+" allele="+allele);
		//type "targeting_vector", "es_cell", "mouse"
		OrderType orderType=OrderType.valueOf(type);
		Allele2DTO allele2DTO = orderService.getAlleForGeneAndAllele(acc, allele, creLine);
		model.addAttribute("allele", allele2DTO);
		
		
		Map<String, List<ProductDTO>> storeToProductsMap = orderService.getStoreNameToProductsMap(acc, allele, orderType, creLine);
		model.addAttribute("storeToProductsMap", storeToProductsMap);
		model.addAttribute("type", orderType);
		if(creLine){
			model.addAttribute("creLine", true);
		}
		return "order";
	}
	
	@RequestMapping("/qcData")
	public String qcData(@RequestParam (required= true) String type, @RequestParam (required=true)String productName,  @RequestParam (required=true)String alleleName, @RequestParam(required=false, defaultValue="false") boolean creLine, Model model, HttpServletRequest request, RedirectAttributes attributes) throws SolrServerException, IOException {
		//get the qc_data list
		OrderType orderType=OrderType.valueOf(type);
		HashMap<String, HashMap<String, List<String>>> qcData = orderService.getProductQc(orderType, productName, alleleName, creLine);
		model.addAttribute("qcData", qcData);
		return "qcData";
	}
	
	@RequestMapping("/order/creline")
    public String creLineAlleles(@RequestParam (required = false) String acc, Model model) throws SolrServerException, IOException{
		
		List<OrderTableRow> orderRows = orderService.getOrderTableRows(acc, null, true);
		model.addAttribute("orderRows", orderRows);
		model.addAttribute("creLine", true);
		model.addAttribute("acc", acc);
    	return "creLineAlleles";
    }

}
