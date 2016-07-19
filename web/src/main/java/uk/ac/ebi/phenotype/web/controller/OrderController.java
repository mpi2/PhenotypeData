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
		System.out.println("orderSection being called!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		List<OrderTableRow> orderRows = orderService.getOrderTableRows(acc, rows);
		
		model.addAttribute("orderRows", orderRows);
		return "orderSectionFrag";
	}
	
	

}
