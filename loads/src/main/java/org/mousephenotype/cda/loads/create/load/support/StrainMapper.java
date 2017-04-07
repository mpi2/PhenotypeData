/*******************************************************************************
 * Copyright © 2015 EMBL - European Bioinformatics Institute
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 ******************************************************************************/

package org.mousephenotype.cda.loads.create.load.support;

import org.apache.commons.codec.digest.DigestUtils;
import org.mousephenotype.cda.db.pojo.DatasourceEntityId;
import org.mousephenotype.cda.db.pojo.OntologyTerm;
import org.mousephenotype.cda.db.pojo.Strain;
import org.mousephenotype.cda.db.pojo.Synonym;
import org.mousephenotype.cda.enumerations.DbIdType;
import org.mousephenotype.cda.loads.common.CdaSqlUtils;
import org.mousephenotype.cda.loads.exceptions.DataLoadException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * StrainNames is a curated set of strains for EuroPhenome data.
 * <p/>
 * Created by mrelac on 06/09/16. Ported from AdminTools. Original creator: Gautier Koscielny, Feb 2012.
 */
public class StrainMapper {
    private CdaSqlUtils cdaSqlUtils;
    private Logger      logger = LoggerFactory.getLogger(this.getClass());

    public static final Map<String, String> STRAIN_MAPPING     = new HashMap<>();
    public static final Map<String, String> BACKGROUND_MAPPING = new HashMap<>();

    public Map<String, Strain> strainsByName = new HashMap<>();
    public Map<String, Strain> strainsBySynonymSymbol = new HashMap<>();
    public Map<String, Strain> strainsByAccessionId = new HashMap<>();


    private OntologyTerm uncharacterizedBackgroundStrain;


    static {

        /**
         * as curated by Karen Pickford Some of the keys map to themselves but
         * there is no assignment in IMSR.
         */

        STRAIN_MAPPING.put("B6NDen;B6N-Snip1<tm1a(EUCOMM)Wtsi>/H", "B6Dnk;B6N-Snip1<tm1a(EUCOMM)Wtsi>/H");
        STRAIN_MAPPING.put("B6NTac;B6N-Tmem167<tm1a(EUCOMM)Hmgu>/H", "B6NTac;B6N-Tmem167<tm1a(EUCOMM)Hmgu>/H");
        STRAIN_MAPPING.put("B6N;B6N-Nsun2<tm1a(EUCOMM)Wtsi>/Wtsi", "B6J;B6N-Tyrc-Brd Nsun2tm1a(EUCOMM)Wtsi/WtsiOulu");
        STRAIN_MAPPING.put("B6N;B6N-Zranb2<tm1a(EUCOMM)Wtsi>/Wtsi", "B6J;B6N-Tyrc-Brd Zranb2tm1a(EUCOMM)Wtsi/WtsiCnbc");
        STRAIN_MAPPING.put("C57BL/6NTac-Ddx27<tm1a(KOMP)Wtsi>/H", "C57BL/6NTac-Ddx27<tm1a(KOMP)Wtsi>/H");
        STRAIN_MAPPING.put("STOCK Vangl2<Lp>/H", "STOCK Vangl2<Lp>/H");
        STRAIN_MAPPING.put("B6N;B6N-Sparc<tm2a(EUCOMM)Wtsi>/Wtsi", "B6J;B6N-Tyrc-Brd Sparctm1a(EUCOMM)Wtsi/WtsiCnbc");
        STRAIN_MAPPING.put("B6NDen;B6N-Fam83g<tm1a(EUCOMM)Wtsi>/H", "B6Dnk;B6N-Fam83g<tm1a(EUCOMM)Wtsi>/H");

        /**
         * As curated by myself but double-checked by Karen.
         */

        STRAIN_MAPPING.put("B6NDen;B6N-Uhrf1tm1a(EUCOMM)Wtsi/Ieg", "B6Dnk;B6N-Uhrf1<tm1a(EUCOMM)Wtsi>/Ieg");
        STRAIN_MAPPING.put("B6NDnk;B6N-Elk4<tm1a(EUCOMM)Wtsi>/H", "B6Dnk;B6N-Elk4<tm1a(EUCOMM)Wtsi>/H");
        STRAIN_MAPPING.put("B6NDen;B6N-Zfp704<tm1a(EUCOMM)Wtsi>/H", "B6Dnk;B6N-Zfp704<tm1a(EUCOMM)Wtsi>/H");
        STRAIN_MAPPING.put("B6N;B6J-Tyr<c-Brd> Arpc4<tm1a(EUCOMM)Wtsi>/Wtsi", "B6J;B6N-Tyr<c-Brd> Arpc4<tm1a(EUCOMM)Wtsi>/WtsiOulu");
        STRAIN_MAPPING.put("B6NDen;B6N-Trnt1<tm1a(EUCOMM)Hmgu>/Ieg", "C57BL/6N-Trnt1<tm1a(EUCOMM)Hmgu>/Ieg");
        STRAIN_MAPPING.put("B6N;B6J-Tyr<c-Brd> Prmt1<tm1a(EUCOMM)Wtsi>/Wtsi", "B6J;B6N-Tyr<c-Brd> Prmt1<tm1a(EUCOMM)Wtsi>/WtsiCnbc");
        STRAIN_MAPPING.put("B6N;B6J-Tyr<c-Brd> Ints12<tm1a(EUCOMM)Wtsi>/Wtsi", "B6J;B6N-Tyr<c-Brd> Ints12<tm1a(EUCOMM)Wtsi>/WtsiCnbc");
        STRAIN_MAPPING.put("C57BL/6NTac-Ube2btm1a(EUCOMM)Wtsi/Ics", "C57BL/6NTac-Ube2b<tm1a(EUCOMM)Wtsi>/Ics");
        STRAIN_MAPPING.put("C57BL/6NTac-B9d1tm1a(EUCOMM)Wtsi/Ics", "C57BL/6NTac-B9d1<tm1a(EUCOMM)Wtsi>/Ics");
        STRAIN_MAPPING.put("B6NDen;B6N-Secisbp2<tm1a(EUCOMM)Wtsi>/H", "B6Dnk;B6N-Secisbp2<tm1a(EUCOMM)Wtsi>/H");
        STRAIN_MAPPING.put("B6N;B6J-Tyr<c-Brd> Cntfr<tm1a(EUCOMM)Wtsi>/Wtsi", "B6J;B6N-Tyr<c-Brd> Cntfr<tm1a(EUCOMM)Wtsi>/WtsiBiat");
        STRAIN_MAPPING.put("B6NDen;B6N-Ercc2<tm1a(EUCOMM)Wtsi>/H", "B6Dnk;B6N-Ercc2<tm1a(EUCOMM)Wtsi>/H");
        STRAIN_MAPPING.put("B6J;B6N-Tyrc-Brd Snf8tm1a(EUCOMM)Wtsi/Wtsi", "B6J;B6N-Tyr<c-Brd> Snf8<tm1a(EUCOMM)Wtsi>/WtsiCnbc");
        STRAIN_MAPPING.put("B6N;B6J-Tyr<c-Brd> Rad18<tm1a(EUCOMM)Wtsi>/Wtsi", "B6J;B6N-Tyr<c-Brd> Rad18<tm1a(EUCOMM)Wtsi>/WtsiCnbc");
        STRAIN_MAPPING.put("B6N;B6J-Tyr<c-Brd> Mthfd1l<tm1a(EUCOMM)Wtsi>/Wtsi", "B6J;B6N-Tyr<c-Brd> A<tm1Brd> Mthfd1l<tm1a(EUCOMM)Wtsi>/WtsiCnbc");
        STRAIN_MAPPING.put("C57BL/6NTac-Sept8tm1a(EUCOMM)Wtsi/Ics", "C57BL/6NTac-Sept8<tm1a(EUCOMM)Wtsi>/Ics");
        STRAIN_MAPPING.put("C3H;B6-Tulp3<hhkr>/H", "C3H.B6-Tulp3<hhkr>/H");

        /**
         * Background mapping
         */

        BACKGROUND_MAPPING.put("B6NTac", "C57BL/6NTac");
        BACKGROUND_MAPPING.put("129/Sv", "129");
        BACKGROUND_MAPPING.put("129S5", "129S5/SvEvBrd");
        BACKGROUND_MAPPING.put("C57BL/6NTacDen", "C57BL/6Dnk");
        BACKGROUND_MAPPING.put("C57BL/6JTyr", "C57BL/6Brd-Tyr<c-Brd>");
        BACKGROUND_MAPPING.put("129P2", "129P2/OlaHsd");
        BACKGROUND_MAPPING.put("B6J.129S2", "129S2");
        BACKGROUND_MAPPING.put("129/SvEv", "129S/SvEv");
        BACKGROUND_MAPPING.put("129SvJ-Iso", "129X1/SvJ");
        BACKGROUND_MAPPING.put("C3H/NHG", "C3H");
        BACKGROUND_MAPPING.put("129/SvPas", "129S2/SvPas");
        BACKGROUND_MAPPING.put("C57BL/6NTac-ICS-USA(ImportedLive)", "C57BL/6NTac");
        BACKGROUND_MAPPING.put("C57BL/6NTac-ICS-Denmark(ImportedLive)", "C57BL/6Dnk");

    }

    @Inject
    public StrainMapper(CdaSqlUtils cdaSqlUtils) throws DataLoadException {
        this.cdaSqlUtils = cdaSqlUtils;
        initialise();
    }

    private void initialise() throws DataLoadException {
        uncharacterizedBackgroundStrain = cdaSqlUtils.getOntologyTermByName("IMPC uncharacterized background strain");

        this.strainsByAccessionId = cdaSqlUtils.getStrainsByNameOrMgiAccessionId();
        for (Strain strain : strainsByAccessionId.values()) {
            strainsByName.put(strain.getName(), strain);
            if (strain.getSynonyms() != null) {
                for (Synonym synonym : strain.getSynonyms()) {
                    strainsBySynonymSymbol.put(synonym.getSymbol(), strain);
                }
            }
        }
    }

    public String createAllelicComposition(String sampleZygosity, String alleleSymbol, String geneSymbol, String sampleGroup) {

        String allelicComposition = null;

        if (sampleGroup.equals("control")) {
            allelicComposition = "";
        } else if (sampleGroup.equals("experimental")) {

            if (sampleZygosity.equals("homozygous") || sampleZygosity.equals("homozygote")) {
                if ( ! alleleSymbol.equals("baseline")) {
                    if ((alleleSymbol.length() > 0) && ( ! alleleSymbol.contains(" "))) {
                        allelicComposition = alleleSymbol + "/" + alleleSymbol;
                    } else {
                        allelicComposition = geneSymbol + "<?>/" + geneSymbol + "<?>";
                    }
                } else {
                    allelicComposition = geneSymbol + "<+>/" + geneSymbol + "<+>";
                }
            } else if ((sampleZygosity.equals("heterozygous")) || (sampleZygosity.equals("heterozygote"))) {
                if ( ! alleleSymbol.equals("baseline")) {
                    if ((alleleSymbol.length() > 0) && ( ! alleleSymbol.contains(" "))) {
                        allelicComposition = alleleSymbol + "/" + geneSymbol + "<+>";
                    } else {
                        allelicComposition = geneSymbol + "<?>/" + geneSymbol + "<+>";
                    }
                }
            } else if (sampleZygosity.equals("hemizygous") || sampleZygosity.equals("hemizygote")) {
                if ( ! alleleSymbol.equals("baseline")) {
                    if ((alleleSymbol.length() > 0) && ( ! alleleSymbol.contains(" "))) {
                        allelicComposition = alleleSymbol + "/0";
                    } else {
                        allelicComposition = geneSymbol + "<?>/0";
                    }
                }
            }
        }
           /*		else if (specimenZygosity.equals("")) {
   			sampleZygosity = "homozygote";
   			// Since the genotype won't be of any help, let's split the allele
   			allelicComposition = geneSymbol + "<+>/" + geneSymbol + "<+>";
   		}	*/

        return allelicComposition;
    }

    /**
     * Return a reformatted representation based on the string representation of the background strain name
     * For special cases, we need to standardise the strain name to IMPC nomenclature
     *
     * e.g.  BALB/c;129S2/SvPas  -> BALB/c * 129S2/SvPas
     *
     * @param backgroundStrainName the raw background strain name, as provided by the dcc
     *
     * @return the genetic background, reformatted
     *
     * @throws DataLoadException
     */
    public String parseMultipleBackgroundStrainNames(String backgroundStrainName) throws DataLoadException {
        List<Strain> strains           = getBackgroundStrains(backgroundStrainName);
        return strains.stream().map(Strain::getName).collect(Collectors.joining(" * "));
    }

    /**
     * Return a list of strains based on the string representation of the genetic background as specified in EuroPhenome
     *
     * @param backgroundStrainName the raw background strain name, as provided by the dcc
     *
     * @throws DataLoadException if a strain is not found
     */
    private List<Strain> getBackgroundStrains(String backgroundStrainName) throws DataLoadException {

        List<Strain> strains     = new ArrayList<>();
        String[]     intermediateBackgrounds;

        // Fail fast
        if (backgroundStrainName == null) {
            throw new DataLoadException("Unknown genetic background. No background strain supplied.");
        }

        Strain strain = lookupBackgroundStrain(backgroundStrainName);
        if (strain != null) {
            strains.add(strain);
        } else {

            // The correct background strain is still not found. Remap against known backgrounds.
            if (backgroundStrainName.contains("_")) {
                intermediateBackgrounds = backgroundStrainName.split("_");
            } else if (backgroundStrainName.contains(";")) {
                intermediateBackgrounds = backgroundStrainName.split(";");
            } else if (backgroundStrainName.equals("Balb/c.129S2")) {
                intermediateBackgrounds = "BALB/c;129S2/SvPas".split(";");
            } else if (backgroundStrainName.equals("B6N.129S2.B6J") || backgroundStrainName.equals("B6J.129S2.B6N") || backgroundStrainName.equals("B6N.B6J.129S2")) {
                intermediateBackgrounds = "C57BL/6N;129S2/SvPas;C57BL/6J".split(";");
            } else if (backgroundStrainName.equals("B6J.B6N")) {
                intermediateBackgrounds = "C57BL/6J;C57BL/6N".split(";");
            } else {
                intermediateBackgrounds = new String[1];
                intermediateBackgrounds[0] = backgroundStrainName;
            }

            // Iterate through the intermediate background pieces to find appropriate strains
            for (String intermediateBackground : intermediateBackgrounds) {
                strain = lookupBackgroundStrain(intermediateBackground);
                if (strain == null) {
                    strain = createBackgroundStrain(intermediateBackground);
                    cdaSqlUtils.insertStrain(strain);
                }
                strains.add(strain);
            }
        }

        if (strains.isEmpty()) {
            throw new DataLoadException("Unknown genetic background. Strains not found for background " + backgroundStrainName);
        }

        return strains;
    }

    /**
     * Returns a {link Strain} instance, after possibly remapping it using the EuroPhenome curation map above, by
     * querying the table columns below using the background strain name (in this order):
     * <ul>
     *     <li>strain.name</li>
     *     <li>synonym.symbol</li>
     *     <li>strain.acc</li>
     * </ul>
     *
     * If no strain is found, null is returned.
     *
     * @param backgroundStrainName
     *
     * @return a {link Strain} instance, if found; null otherwise
     */
    private Strain lookupBackgroundStrain(String backgroundStrainName) {
        Strain strain;

        if (backgroundStrainName == null) {
            return null;
        }

        // If the strain name is in the mapping table, use the strain name from the mapping table.
        if (BACKGROUND_MAPPING.containsKey(backgroundStrainName)) {
            backgroundStrainName = BACKGROUND_MAPPING.get(backgroundStrainName);
        }

        // Use the background strain name as: strain name, then synonym, then accession id to find the strain.
        // If it is not found, return null.
        strain = strainsByName.get(backgroundStrainName);
        if (strain == null) {
            strain = strainsBySynonymSymbol.get(backgroundStrainName);
            if (strain == null) {
                strain = strainsByAccessionId.get(backgroundStrainName);
            }
        }

        return strain;
    }

    /**
     * Creates a background strain {@link Strain} instance from the given background strain and the EuroPhenome maps
     * in this file. This method is only intended to be called after getting {@code null} back from a call to
     * {@code lookupBackgroundStrain()} so that the caller can add this {link Strain} to the database.
     *
     * @param backgroundStrainName
     *
     * @return  a {link Strain} instance, based on available information. This instance probably does not already exist
     * in the database.
     */
    private Strain createBackgroundStrain(String backgroundStrainName) {
        Strain strain;

        if (backgroundStrainName == null) {
            return null;
        }

        // If the strain name is in the mapping table, use the strain name from the mapping table.
        if (BACKGROUND_MAPPING.containsKey(backgroundStrainName)) {
            backgroundStrainName = BACKGROUND_MAPPING.get(backgroundStrainName);
        }

        // Create a strain instance based on the available information.
        String strainAccessionId = "IMPC-CURATE-" + DigestUtils.md5Hex(backgroundStrainName).substring(0, 5).toUpperCase();

        strain = new Strain();
        strain.setBiotype(uncharacterizedBackgroundStrain);
        strain.setId(new DatasourceEntityId(strainAccessionId, DbIdType.IMPC.intValue()));
        strain.setName(backgroundStrainName);
        strain.setSynonyms(new ArrayList<>());

        return strain;
    }
}