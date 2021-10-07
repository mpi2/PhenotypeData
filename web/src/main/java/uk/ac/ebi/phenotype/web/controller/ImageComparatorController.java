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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
        filteredMutants = filteredMutants.stream().map(this::resetAge).collect(Collectors.toList());

        ImageDTO imgDoc = null;
        if (!filteredMutants.isEmpty()) {
            imgDoc = filteredMutants.get(0);
        }

        List<ImageDTO> controls = imageService.getControlsBySexAndOthersForComparisonViewer(imgDoc, numberOfControlsPerSex, sexType, parameterStableId, anatomyId, parameterAssociationValue);
        controls = controls.stream().map(this::resetAge).collect(Collectors.toList());

        Map<String, Set<String>> specimenExpression = new HashMap<>();
        for (ImageDTO image : Stream.of(filteredMutants, controls).flatMap(Collection::stream).collect(Collectors.toList())) {
            String imageKey = image.getId();
            specimenExpression.computeIfAbsent(imageKey, k -> new LinkedHashSet<>());
            if(image.getParameterAssociationName() != null) {
                for (int i = 0; i < image.getParameterAssociationName().size(); i++) {
                    specimenExpression.get(imageKey).add(image.getParameterAssociationName().get(i) + ": " + image.getParameterAssociationValue().get(i));
                }
            }
        }
        Map<String, String> specimenExpressionStrings = new HashMap<>();
        for (String k : specimenExpression.keySet()) {
            specimenExpressionStrings.computeIfAbsent(k, v -> String.join(",<br />", specimenExpression.get(k)));
        }



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
        model.addAttribute("specimenExpression", specimenExpressionStrings);
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

    private ImageDTO resetAge(ImageDTO image) {
        if (image.getMetadata().stream().anyMatch(x -> x.toLowerCase(Locale.ROOT).contains("date of sacrifice"))) {
            List<String> metadataMatches = image.getMetadata().stream().filter(x -> x.toLowerCase(Locale.ROOT).contains("date of sacrifice")).collect(Collectors.toList());
            if (metadataMatches.size() > 0) {
                // Use first date of sacrifice as the end age
                final List<String> metadataPieces = Arrays.asList(metadataMatches.get(0).split(" = "));
                final String dateOfSacrificeString = metadataPieces.get(1);

                final Date dateOfSacrifice = parseDateString(dateOfSacrificeString);
                if (dateOfSacrifice == null) return image;
                final Date dateOfBirth = image.getDateOfBirth();

                final long l = dateOfSacrifice.getTime() - dateOfBirth.getTime();
                final int days = (int) TimeUnit.DAYS.convert(l, TimeUnit.MILLISECONDS);
                image.setAgeInDays(days);
                image.setAgeInWeeks((int) Math.round((days / 7.0)));
            }
        }
        return image;
    }


    /**
     * Method to parse various increment value date time formats
     * Supported formats:
     * 2012-12-12T12:12:12+00:00
     * 2012-12-12T12:12:12+0000
     * 2012-12-12T12:12:12Z
     * 2012-12-12 12:12:12Z
     * 2012-12-12T12:12:12
     * 2012-12-12 12:12:12
     * <p/>
     * Unsuccessful parse returns null
     *
     * @param value string value of a date in ISO 8601 format
     * @return date, or null if parse unsuccessful
     */
    public static Date parseDateString(String value) {
        Date d = null;
        List<SimpleDateFormat> supportedFormats = new ArrayList<>();
        supportedFormats.add(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX"));
        supportedFormats.add(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXX"));
        supportedFormats.add(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX"));
        supportedFormats.add(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ"));
        supportedFormats.add(new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ssZ"));
        supportedFormats.add(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'"));
        supportedFormats.add(new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss'Z'"));
        supportedFormats.add(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"));
        supportedFormats.add(new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss"));
        supportedFormats.add(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm"));
        supportedFormats.add(new SimpleDateFormat("yyyy-MM-dd' 'HH:mm"));

        for (SimpleDateFormat format : supportedFormats) {
            try {
                d = format.parse(value);
            } catch (ParseException e) {
                // Not this format, try the next one
                continue;
            }
            // If the parse is successful, stop processing the rest
            break;
        }

        return d;
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
