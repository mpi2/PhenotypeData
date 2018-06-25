package uk.ac.ebi.phenotype.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ThreeIController {

	@RequestMapping("/threeIAnalysis")
	public String threeIAnalysis() {
		System.out.println("calling threeIAnalysis");
		return "threeIAnalysis";
	}
}
