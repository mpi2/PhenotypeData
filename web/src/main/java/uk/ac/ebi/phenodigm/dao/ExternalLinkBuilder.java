/*
 * Copyright © 2011-2013 EMBL - European Bioinformatics Institute
 * and Genome Research Limited
 *  
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License.  
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.ac.ebi.phenodigm.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.phenodigm.model.Allele;
import uk.ac.ebi.phenodigm.model.MouseModel;


/**
 * Creates external links (URIs) for a given object.
 *
 * @author Jules Jacobsen <jules.jacobsen@sanger.ac.uk>
 */
public class ExternalLinkBuilder {

    private static final Logger logger = LoggerFactory.getLogger(ExternalLinkBuilder.class);

    protected enum MouseHrefTags {

        MGI_URL("<a href=\"http://informatics.jax.org/accession/%s\">%s</a>"),
        IMPC_URL("<a href=\"http://www.mousephenotype.org/data/genes/%s\">%s</a>"),
        UNKNOWN_URL("<a href=%s>%s</a>");

        private final String urlPattern;

        MouseHrefTags(String urlPattern) {
            this.urlPattern = urlPattern;
        }

        static String getHrefTag(String resource) {
            //find out what source of the model is for building the URL.
            switch (resource) {
                case "MGI":
                    return MGI_URL.toString();
                case "MGP": //MGP is the legacy source name
                case "IMPC": //IMPC is the new shiny one
                    return IMPC_URL.toString();
                default:
                    return UNKNOWN_URL.toString();
            }
        }

        @Override
        public String toString() {
            return urlPattern;
        }

    }

    /**
     * Returns an HTML formatted link to the alleleSymbols of the model. Make
     * sure that the geneId, source, allelicComposition and alleleIds have been
     * set otherwise there will be empty/meaningless links.
     *
     * @param mouseModel
     * @return an HTML- formatted link to the alleleSymbols of the model
     * genotype.
     */
    public static String buildLink(MouseModel mouseModel) {
        return buildLink(mouseModel.getMgiGeneId(), mouseModel.getSource(), mouseModel.getAllelicComposition(), mouseModel.getAlleleIds());
    }

    /**
     * Non type-safe implementation factored out for ease of testing odd edge
     * cases.
     *
     * @param geneId
     * @param source
     * @param allelicComposition
     * @param alleleIds
     * @return
     */
    protected static String buildLink(String geneId, String source, String allelicComposition, String alleleIds) {

        List<String> alleleSymbols = makeAlleleSymbols(allelicComposition);

        //find out if the alleles are homozygous
        boolean homozygous = isHomozygous(alleleSymbols);

        //make the alleleIds
        List<String> alleleIdentifiers = makeAlleleIdentifiers(geneId, homozygous, alleleIds, alleleSymbols);

        //so now all the data should be in order we should be able to safely build the alleles
        //and add them to the alleleList
        List<Allele> alleleList = new ArrayList<>();
        for (int i = 0; i < alleleSymbols.size(); i++) {
            Allele allele = new Allele(source, geneId, alleleSymbols.get(i), alleleIdentifiers.get(i));
            alleleList.add(allele);
        }

        //hopefully there should be some alleles by now
        if (alleleList.size() == 1) {
            return buildAlleleLink(alleleList.get(0));
        } else if (alleleList.size() == 2) {
            //this is a heterozygote
            String alleleOne = buildAlleleLink(alleleList.get(0));
            String alleleTwo = buildAlleleLink(alleleList.get(1));
            return String.format("%s/%s", alleleOne, alleleTwo);
        }
        //hopefully we never get to this point
        return "";
    }

    private static List<String> makeAlleleSymbols(String allelicComposition) {
        //figure out the allelic composition
        //usually: Fgf9<tm1Dor>/Fgf9<tm1Dor>
        //need to take into account edge cases like:  
        // Cd247/Pou2f1<tm1Tks>/Cd247/Pou2f1<tm1Tks>
        // mt-Rnr2<m1Dwa>
        // Bstk/Bstk
        List<String> alleleSymbols = new ArrayList<>();

        String splitString = ">/"; //this is the most common type for targeted mutations
        if (!allelicComposition.contains(splitString)) {
            splitString = "/"; //spontaneous mutants e.g.  Bstk/Bstk
        }
//        logger.info("{} splitting by string: {}", allelicComposition, splitString);
        alleleSymbols.addAll(Arrays.asList(allelicComposition.split(splitString)));
        //replace the ">" of the first alleleSymbol in cases where the split string contained a ">"
        if (splitString.equals(">/")) {
            alleleSymbols.set(0, String.format("%s>", alleleSymbols.get(0)));
        }
//        logger.info("Made alleleSymbols {}", alleleSymbols);
        return alleleSymbols;
    }

    private static boolean isHomozygous(List<String> alleleSymbols) {
        return alleleSymbols.size() == 2 && alleleSymbols.get(0).equals(alleleSymbols.get(1));
    }

    private static List<String> makeAlleleIdentifiers(String geneId, boolean homozygous, String alleleIds, List<String> alleleSymbols) {

        List<String> alleleIdentifiers = new ArrayList<>();

        //split the alleleIds - there may be none, one or two 
        if (alleleIds == null || alleleIds.isEmpty()) {
            //if there is nothing, then add the same number of alleleIds as alleleSymbols
//            logger.info("{} is missing all alleleIds - replacing them with the geneId");
            for (int i = 0; i < alleleSymbols.size(); i++) {
                alleleIdentifiers.add(geneId);
            }
        } else {
            alleleIdentifiers.addAll(Arrays.asList(alleleIds.split("\\|")));
        }
//        logger.info("Made alleleIdentifiers {}", alleleIdentifiers);

        //replace the missing alleleIds with the geneId
        if (alleleIdentifiers.size() != alleleSymbols.size()) {
            int numMissingAlleleIds = Math.abs(alleleSymbols.size() - alleleIdentifiers.size());
            for (int i = 0; i < numMissingAlleleIds; i++) {
                if (homozygous) {
                    alleleIdentifiers.add(alleleIdentifiers.get(0));
                } else {
                    alleleIdentifiers.add(geneId);
                }
            }
//            logger.info("Filled in missing alleleIdentifiers {}", alleleIdentifiers);
        }
        return alleleIdentifiers;
    }

    protected static String buildAlleleLink(Allele allele) {
        String alleleId = allele.getAlleleId();

        if (alleleId == null || alleleId.isEmpty()) {
            alleleId = allele.getGeneId();
        }

        String urlPattern = MouseHrefTags.getHrefTag(allele.getSource());
        return String.format(urlPattern, alleleId, formatAllelicCompositionHtml(allele.getAlleleSymbol()));
    }


    /**
     * Returns an HTML formatted allelic composition by replacing all instances
     * of '<' and '>' with '<sup>' and '</sup>'. For example this method returns
     * the input of 'Fgf9<tm1Dor>/Fgf9<tm1Dor>' as
     * 'Fgf9<sup>tm1Dor</sup>/Fgf9<sup>tm1Dor</sup>'.
     *
     * @param allelicComposition
     * @return an HTML formatted allelic composition
     */
    protected static String formatAllelicCompositionHtml(String allelicComposition) {
        //substitute old tags
        allelicComposition = allelicComposition.replace("<", "££").replace(">", "$$");
        //replace tags with html ones
        allelicComposition = allelicComposition.replace("££", "<sup>").replace("$$", "</sup>");
        return allelicComposition;
    }
}
