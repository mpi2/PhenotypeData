package uk.ac.ebi.phenotype.web.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.solr.client.solrj.SolrServerException;
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
	@RequestMapping("/orderVector")
	public String orderVector(@RequestParam (required = true) String acc, @RequestParam (required = true) String allele,
			Model model, HttpServletRequest request, RedirectAttributes attributes) throws SolrServerException{
		System.out.println("orderVector being called with acc="+acc+" allele="+allele);
		Allele2DTO allele2DTO = orderService.getAlleForGeneAndAllele(acc, allele);
		System.out.println("size of allele2DTOs="+allele2DTO);
		List<ProductDTO> products = orderService.getProduct(acc, allele, "vector");
		model.addAttribute("allele", allele2DTO);
		return "orderVector";
	}
	
	@RequestMapping("/orderEsCell")
	public String orderEsCell(@RequestParam (required = false) String acc, 
			@RequestParam (required=false, defaultValue="25") int rows,
			Model model, HttpServletRequest request, RedirectAttributes attributes) throws SolrServerException{
		System.out.println("orderEsCell being called!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		
		return "orderEsCell";
	}
	
	
	@RequestMapping("/orderMouse")
	public String orderMouse(@RequestParam (required = false) String acc, 
			@RequestParam (required=false, defaultValue="25") int rows,
			Model model, HttpServletRequest request, RedirectAttributes attributes) throws SolrServerException{
		System.out.println("orderMouse being called!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		return "orderMouse";
	}

}
