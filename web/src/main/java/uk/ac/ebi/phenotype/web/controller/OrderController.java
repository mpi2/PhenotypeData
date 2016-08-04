package uk.ac.ebi.phenotype.web.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.solr.client.solrj.SolrServerException;
import org.mousephenotype.cda.enumerations.OrderType;
import org.mousephenotype.cda.solr.service.OrderService;
import org.mousephenotype.cda.solr.service.dto.Allele2DTO;
import org.mousephenotype.cda.solr.service.dto.ProductDTO;
import org.mousephenotype.cda.solr.web.dto.OrderTableRow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class OrderController {
	
	@Autowired
	OrderService orderService;
	
	@RequestMapping("/orderSection")
	public String orderSection(@RequestParam (required = false) String acc, 
			@RequestParam (required=false, defaultValue="25") int rows,
			Model model, HttpServletRequest request, RedirectAttributes attributes) throws SolrServerException{
		System.out.println("orderSection being called blah!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		List<OrderTableRow> orderRows = orderService.getOrderTableRows(acc, rows);
		for(OrderTableRow row: orderRows){
			System.out.println("row="+ row);
		}
		
		model.addAttribute("acc",acc);
		model.addAttribute("orderRows", orderRows);
		return "orderSectionFrag";
	}
	
	/**
	 * 
	 * @param allele e.g. Thpotm1(KOMP)Vlcg
	 * @param rows
	 * @param model
	 * @param request
	 * @param attributes
	 * @return
	 * @throws SolrServerException
	 */
	@RequestMapping("/order")
	public String order(@RequestParam (required = true) String acc, @RequestParam (required = true) String allele,  @RequestParam (required = true) String type,
			Model model, HttpServletRequest request, RedirectAttributes attributes) throws SolrServerException{
		System.out.println("orderVector being called with acc="+acc+" allele="+allele);
		//type "targeting_vector", "es_cell", "mouse"
		OrderType orderType=OrderType.valueOf(type);
		Allele2DTO allele2DTO = orderService.getAlleForGeneAndAllele(acc, allele);
		model.addAttribute("allele", allele2DTO);
		System.out.println("size of allele2DTOs="+allele2DTO);
		
		Map<String, List<ProductDTO>> productsByOrderName = orderService.getProductToOrderNameMap(acc, allele, orderType);
		model.addAttribute("productsByName", productsByOrderName);
		model.addAttribute("type", orderType);
		return "order";
	}
	
	@RequestMapping("/qcData")
	public String qcData(@RequestParam (required= true) String type, @RequestParam (required=true)String productName, Model model, HttpServletRequest request, RedirectAttributes attributes) throws SolrServerException{
		System.out.println("qcData being called with type="+type+" productName="+productName);
		//get the qc_data list
		OrderType orderType=OrderType.valueOf(type);
		HashMap<String, HashMap<String, List<String>>> qcData = orderService.getProductQc(orderType, productName);
		model.addAttribute("qcData", qcData);
		return "qcData";
	}

}
