package uk.ac.ebi.phenotype.web.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.mousephenotype.cda.solr.web.dto.GeneTargetDetail;
import org.mousephenotype.cda.solr.web.dto.OrderTableRow;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class OrderController {
	
	@RequestMapping("/orderSection/{acc}")
	public String orderSection(@PathVariable String acc, Model model, HttpServletRequest request, RedirectAttributes attributes){
		System.out.println("orderSection being called!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		OrderTableRow row=new OrderTableRow();
		row.setAlleleName("Cpsf3tm1b(EUCOMM)Wtsi");
		row.setStrainOfOrigin("C57BL/6N");
		row.setAlleleType("Reporter-tagged deletion allele (post-Cre)");
		GeneTargetDetail detail=new GeneTargetDetail();
		detail.setLabel("Target Vector Map");
		detail.setLink("https://www.i-dcc.org/imits/targ_rep/alleles/4973/allele-image-cre?simple=true.jpg.jpg");
		List<GeneTargetDetail> geneTargetDetails=new ArrayList<>();
		geneTargetDetails.add(detail);
		row.setGeneTargetDetails(geneTargetDetails);

		List<OrderTableRow> orderRows=new ArrayList<>();
		orderRows.add(row);
		model.addAttribute("orderRows", orderRows);
		return "orderSectionFrag";
	}

}
