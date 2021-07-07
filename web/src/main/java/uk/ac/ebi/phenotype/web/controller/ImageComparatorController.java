/*******************************************************************************
 * Copyright 2015 EMBL - European Bioinformatics Institute
 *
 * Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 *******************************************************************************/
package uk.ac.ebi.phenotype.web.controller;

import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrServerException;
import org.mousephenotype.cda.enumerations.Expression;
import org.mousephenotype.cda.enumerations.SexType;
import org.mousephenotype.cda.enumerations.ZygosityType;
import org.mousephenotype.cda.solr.service.AnatomyService;
import org.mousephenotype.cda.solr.service.GeneService;
import org.mousephenotype.cda.solr.service.ImageService;
import org.mousephenotype.cda.solr.service.ImpressService;
import org.mousephenotype.cda.solr.service.dto.AnatomyDTO;
import org.mousephenotype.cda.solr.service.dto.GeneDTO;
import org.mousephenotype.cda.solr.service.dto.ImageDTO;
import org.mousephenotype.cda.solr.service.dto.ParameterDTO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class ImageComparatorController {

    private ImageService imageService;
    private GeneService geneService;
    private AnatomyService anatomyService;
    private ImpressService impressService;

    @Inject
    public ImageComparatorController(@NotNull ImageService imageService,
                                     @NotNull GeneService geneService,
                                     @NotNull ImpressService impressService,
                                     @NotNull AnatomyService anatomyService) {
        this.imageService = imageService;
        this.geneService = geneService;
        this.anatomyService = anatomyService;
        this.impressService = impressService;
    }

    @RequestMapping("/imageComparator")
    public String imageCompBrowser(@RequestParam(value = "acc", required = false) String acc,
                                   @RequestParam(value = "parameter_stable_id", required = false) String parameterStableId,
                                   @RequestParam(value = "parameter_association_value", required = false) String parameterAssociationValue,
                                   @RequestParam(value = "anatomy_id", required = false) String anatomyId,
                                   @RequestParam(value = "anatomy_term", required = false) String anatomyTerm,
                                   @RequestParam(value = "gender", required = false) String gender,
                                   @RequestParam(value = "zygosity", required = false) String zygosity,
                                   @RequestParam(value = "mediaType", required = false) String mediaType,
                                   @RequestParam(value = "colony_id", required = false) String colonyId,
                                   @RequestParam(value = "mp_id", required = false) String mpId,
                                   @RequestParam(value = "force_frames", required = false, defaultValue = "false") Boolean forceFrames,
                                   Model model)
            throws SolrServerException, IOException {

        int numberOfControlsPerSex = 50;

        SexType sexType = null;
        if (gender != null && !gender.equals("all")) {
            sexType = imageService.getSexTypesForFilter(gender);
        }

        if (mediaType != null) System.out.println("mediaType= " + mediaType);
        // get experimental images
        // we will also want to call the getControls method and display side by
        // side
        if (StringUtils.isEmpty(anatomyId) && !StringUtils.isEmpty(anatomyTerm) && !anatomyTerm.contains("Unassigned")) {
            try {
                AnatomyDTO anatomyDto = anatomyService.getTermByName(anatomyTerm);
                anatomyId = anatomyDto.getAnatomyId();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (parameterAssociationValue != null) {
            if (parameterAssociationValue.equalsIgnoreCase("all")) {
                parameterAssociationValue = null;
            }
        }

        List<ImageDTO> filteredMutants = imageService.getMutantImagesForComparisonViewer(acc, parameterStableId, parameterAssociationValue, anatomyId, zygosity, colonyId, mpId, sexType);

        ImageDTO imgDoc = null;
        if (!filteredMutants.isEmpty()) {
            imgDoc = filteredMutants.get(0);
        }

        List<ImageDTO> controls = imageService.getControlsBySexAndOthersForComparisonViewer(imgDoc, numberOfControlsPerSex, sexType, parameterStableId, anatomyId, parameterAssociationValue);

        final ParameterDTO parameter = impressService.getParameterByStableId(parameterStableId);
        final String procedures = parameter.getProcedureNames().stream().distinct().collect(Collectors.joining(", "));
        this.addGeneToPage(acc, model);
        model.addAttribute("procedure", procedures);
        model.addAttribute("parameter", parameter);
        model.addAttribute("mediaType", mediaType);
        model.addAttribute("sexTypes", SexType.values());
        model.addAttribute("zygTypes", ZygosityType.values());
        model.addAttribute("mutants", filteredMutants);
        model.addAttribute("expression", Expression.values());
        model.addAttribute("controls", controls);

        // Detect if external OMERO required
        boolean federated = this.isFederated(filteredMutants);

		// Detect if stacked images
        boolean zStacked = this.isZStacked(filteredMutants);

		// forcedFrames is a flag to override anything else and show frames view
        if (
        		forceFrames ||
				(mediaType != null && mediaType.equals("pdf") || federated || zStacked)
		) {
			//iframes required. switch to this view for the pdfs or to work with JAX federated omero housed at JAX
            return "comparatorFrames";
        }
        return "comparator";
    }

    private boolean isFederated(List<ImageDTO> filteredMutants) {
        for (ImageDTO image : filteredMutants) {
            if (image.getImageLink() != null && (image.getImageLink().contains("omero") || image.getImageLink().contains("NDPServe"))) {
                return true;
            }
        }
        return false;
    }

    private boolean isZStacked(List<ImageDTO> filteredMutants) {
        for (ImageDTO image : filteredMutants) {
            if (image.getParameterStableId().equals("MGP_EEI_114_001")) {
                return true;
            }
        }
        return false;
    }

    @RequestMapping("/overlap")
    public String overlap(@RequestParam String acc, @RequestParam String id1, @RequestParam String id2, Model model) throws SolrServerException, IOException {

        this.addGeneToPage(acc, model);
        return "overlap";
    }

    @RequestMapping("/imageComparatorTest")
    public String imageCompBrowser() {
        return "comparatorBasicTest";

    }

    private void addGeneToPage(String acc, Model model)
            throws SolrServerException, IOException {
        GeneDTO gene = geneService.getGeneById(acc, GeneDTO.MGI_ACCESSION_ID, GeneDTO.MARKER_SYMBOL);//added for breadcrumb so people can go back to the gene page
        model.addAttribute("gene", gene);
    }
}
