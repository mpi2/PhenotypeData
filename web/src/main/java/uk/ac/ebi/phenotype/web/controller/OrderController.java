package uk.ac.ebi.phenotype.web.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.solr.client.solrj.SolrServerException;
import org.mousephenotype.cda.solr.service.OrderService;
import org.mousephenotype.cda.solr.web.dto.OrderTableRow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class OrderController {
	
	@Autowired
	OrderService orderService;
	
	@RequestMapping("/orderSection/{acc}")
	public String orderSection(@PathVariable String acc, Model model, HttpServletRequest request, RedirectAttributes attributes) throws SolrServerException{
		System.out.println("orderSection being called!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		List<OrderTableRow> orderRows = orderService.getOrderTableRows(acc);
		
		model.addAttribute("orderRows", orderRows);
		return "orderSectionFrag";
	}
	
	

}
