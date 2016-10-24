package uk.ac.ebi.phenotype.web.controller;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.type.TypeFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import uk.ac.ebi.phenotype.bean.LandingPageDTO;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Created by ilinca on 24/10/2016.
 */

@Controller
public class LandingPageController {

    @RequestMapping("/landing")
    public String getAlleles(
            Model model,
            HttpServletRequest request) throws IOException {

        BufferedReader in = new BufferedReader( new FileReader(new ClassPathResource("landingPages.json").getFile()));
        if (in != null) {
            String json = in.lines().collect(Collectors.joining(" "));
            ObjectMapper mapper = new ObjectMapper();
            LandingPageDTO[] readValue = mapper.readValue(json, TypeFactory.defaultInstance().constructArrayType(LandingPageDTO.class));
            model.addAttribute("pages", new ArrayList<LandingPageDTO>(Arrays.asList(readValue)));
        }
        return "landing";
    }


}
