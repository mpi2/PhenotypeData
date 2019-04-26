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

package org.mousephenotype.cda.ri.extract;

import org.mousephenotype.cda.ri.core.entities.Gene;
import org.mousephenotype.cda.ri.core.entities.ImitsStatus;
import org.mousephenotype.cda.ri.core.exceptions.InterestException;
import org.mousephenotype.cda.ri.core.utils.ParseUtils;
import org.mousephenotype.cda.ri.core.utils.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by mrelac on 09/06/16.
 */
public class GeneProcessor implements ItemProcessor<Gene, Gene> {

    private Map<Integer, Gene>       genesByPk;
    private Map<String, ImitsStatus> imitsStatusByStatus;
    private Set<String>              accessionIds = new HashSet<>();
    private int                      lineNumber   = 0;

    private ParseUtils parseUtils = new ParseUtils();
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public final  Set<String> errMessages = ConcurrentHashMap.newKeySet();       // This is the java 8 way to create a concurrent hash set.


    private final String[] expectedHeadings = new String[]{
              "gene_mgi_accession_id"
            , "gene_marker_symbol"
            , "gene_assignment_status"
            , "gene_assigned_to"
            , "gene_assignment_status_date"
            , "conditional_allele_production_status"
            , "conditional_allele_production_centre"
            , "conditional_allele_status_date"
            , "conditional_allele_production_start_date"
            , "null_allele_production_status"
            , "null_allele_production_centre"
            , "null_allele_status_date"
            , "null_allele_production_start_date"
            , "phenotyping_status"
            , "phenotyping_centre"
            , "phenotyping_status_date"
            , "number_of_significant_phenotypes"
    };


    public GeneProcessor(Map<String, ImitsStatus> imitsStatusByStatus, Map<Integer, Gene> genesByPk) {
        this.imitsStatusByStatus = imitsStatusByStatus;
        this.genesByPk = genesByPk;
    }


    @Override
    public Gene process(Gene gene) throws Exception {

        lineNumber++;

        // Validate the file using the heading names and initialize any collections.
        if (lineNumber == 1) {
            String[] actualHeadings = new String[] {
                    gene.getMgiAccessionId()
                  , gene.getSymbol()
                  , gene.getAssignmentStatus()
                  , gene.getAssignedTo()
                  , gene.getAssignmentStatusDateString()

                  , gene.getConditionalAlleleProductionStatus()
                  , gene.getConditionalAlleleProductionCentre()
                  , gene.getConditionalAlleleProductionStatusDateString()
                  , gene.getConditionalAlleleProductionStartDateString()

                  , gene.getNullAlleleProductionStatus()
                  , gene.getNullAlleleProductionCentre()
                  , gene.getNullAlleleProductionStatusDateString()
                  , gene.getNullAlleleProductionStartDateString()

                  , gene.getPhenotypingStatus()
                  , gene.getPhenotypingCentre()
                  , gene.getPhenotypingStatusDateString()

                  , gene.getNumberOfSignificantPhenotypesString()
            };

            for (int i = 0; i < expectedHeadings.length; i++) {
                if ( ! expectedHeadings[i].equals(actualHeadings[i])) {
                    throw new InterestException("Expected heading '" + expectedHeadings[i] + "' but found '" + actualHeadings[i] + "'.");
                }
            }

            return null;
        }

        // Validate fields. If errors, log them and return null.

        // Required fields
        if ((gene.getMgiAccessionId() == null) || (gene.getMgiAccessionId().trim().isEmpty())) {
            logger.info("Line " + lineNumber + ": Empty MGI accession id");
            return null;
        }
        if ((gene.getSymbol() == null) || (gene.getSymbol().trim().isEmpty())) {
            logger.info("Line " + lineNumber + ": Empty symbol");
            return null;
        }

        // According to Peter Matthews, the mgi accession ids in the iMits input file should all be unique. I found at least one that reappears in the same file. Check for that condition here.
        if (accessionIds.contains(gene.getMgiAccessionId())) {
            logger.info("Line " + lineNumber + ": MGI accession id " + gene.getMgiAccessionId() + " appears earlier in the file.");
            return null;
        } else {
            accessionIds.add(gene.getMgiAccessionId());
        }

        // statuses
        if ((gene.getAssignmentStatus() != null) && ( ! gene.getAssignmentStatus().trim().isEmpty())) {
            if ( ! imitsStatusByStatus.containsKey(gene.getAssignmentStatus())) {
                errMessages.add("Unknown gene assignment status '" + gene.getAssignmentStatus() + '"');
                return null;
            }
        }


        if ((gene.getConditionalAlleleProductionStatus() != null) && ( ! gene.getConditionalAlleleProductionStatus().trim().isEmpty())) {
            if ( ! imitsStatusByStatus.containsKey(gene.getConditionalAlleleProductionStatus())) {
                errMessages.add("Unknown conditional allele production status '" + gene.getConditionalAlleleProductionStatus() + "'");
                return null;
            }
        }
        if ((gene.getNullAlleleProductionStatus() != null) && ( ! gene.getNullAlleleProductionStatus().trim().isEmpty())) {
            if ( ! imitsStatusByStatus.containsKey(gene.getNullAlleleProductionStatus())) {
                errMessages.add("Unknown null allele production status '" + gene.getNullAlleleProductionStatus() + "'");
                return null;
            }
        }
        if ((gene.getPhenotypingStatus() != null) && ( ! gene.getPhenotypingStatus().trim().isEmpty())) {
            if (! imitsStatusByStatus.containsKey(gene.getPhenotypingStatus())) {
                errMessages.add("Unknown phenotyping status '" + gene.getPhenotypingStatus() + "'");
                return null;
            }
        }

        // date
        SimpleDateFormat sdf = new SimpleDateFormat("y-M-d h:m:s");
        if ((gene.getAssignmentStatusDateString() != null) && ( ! gene.getAssignmentStatusDateString().trim().isEmpty())) {
            Date date = parseUtils.tryParseDate(sdf, gene.getAssignmentStatusDateString());
            if (date == null) {
                errMessages.add("Invalid date '" + gene.getAssignmentStatusDate() + "'");
                return null;
            }
            gene.setAssignmentStatusDate(date);
        }
        if ((gene.getConditionalAlleleProductionStatusDateString() != null) && ( ! gene.getConditionalAlleleProductionStatusDateString().trim().isEmpty())) {
            Date date = parseUtils.tryParseDate(sdf, gene.getConditionalAlleleProductionStatusDateString());
            if (date == null) {
                errMessages.add("Invalid date '" + gene.getConditionalAlleleProductionStatusDate() + "'");
                return null;
            }
            gene.setConditionalAlleleProductionStatusDate(date);
        }
        if ((gene.getConditionalAlleleProductionStartDateString() != null) && ( ! gene.getConditionalAlleleProductionStartDateString().trim().isEmpty())) {
            Date date = parseUtils.tryParseDate(sdf, gene.getConditionalAlleleProductionStartDateString());
            if (date == null) {
                errMessages.add("Invalid date '" + gene.getConditionalAlleleProductionStartDate() + "'");
                return null;
            }
            gene.setConditionalAlleleProductionStartDate(date);
        }
        if ((gene.getNullAlleleProductionStatusDateString() != null) && (!gene.getNullAlleleProductionStatusDateString().trim().isEmpty())) {
            Date date = parseUtils.tryParseDate(sdf, gene.getNullAlleleProductionStatusDateString());
            if (date == null) {
                errMessages.add("Invalid date '" + gene.getNullAlleleProductionStatusDate() + "'");
                return null;
            }
            gene.setNullAlleleProductionStatusDate(date);
        }
        if ((gene.getNullAlleleProductionStartDateString() != null) && ( ! gene.getNullAlleleProductionStartDateString().trim().isEmpty())) {
            Date date = parseUtils.tryParseDate(sdf, gene.getNullAlleleProductionStartDateString());
            if (date == null) {
                errMessages.add("Invalid date '" + gene.getNullAlleleProductionStartDate() + "'");
                return null;
            }
            gene.setNullAlleleProductionStartDate(date);
        }
        if ((gene.getPhenotypingStatusDateString() != null) && ( ! gene.getPhenotypingStatusDateString().trim().isEmpty())) {
            Date date = parseUtils.tryParseDate(sdf, gene.getPhenotypingStatusDateString());
            if (date == null) {
                errMessages.add("Invalid date '" + gene.getPhenotypingStatusDate() + "'");
                return null;
            }
            gene.setPhenotypingStatusDate(date);
        }

        // 'number'. Optional. If specified, it must be an integer. Reject it if it is not. If null, set it to 0.
        if (gene.getNumberOfSignificantPhenotypes() == null) {
            gene.setNumberOfSignificantPhenotypes(0);
        } else {
            Integer number = parseUtils.tryParseInt(gene.getNumberOfSignificantPhenotypesString());
            if (number == null) {
                errMessages.add("Invalid number '" + gene.getNumberOfSignificantPhenotypes() + "'");
                return null;
            }
            gene.setNumberOfSignificantPhenotypes(number);
        }

        // Populate the ri status fields based on imits status string. NOTE: geneStatus can be null.
        ImitsStatus imitsStatus = null;

        if ((gene.getAssignmentStatus() != null) && ( ! gene.getAssignmentStatus().trim().isEmpty())) {
             imitsStatus = imitsStatusByStatus.get(gene.getAssignmentStatus());
        }
        gene.setRiAssignmentStatus(imitsStatus == null ? null : imitsStatus.getGeneStatus());

        imitsStatus = null;
        if ((gene.getConditionalAlleleProductionStatus() != null) && ( ! gene.getConditionalAlleleProductionStatus().trim().isEmpty())) {
            imitsStatus = imitsStatusByStatus.get(gene.getConditionalAlleleProductionStatus());
        }
        gene.setRiConditionalAlleleProductionStatus(imitsStatus == null ? null : imitsStatus.getGeneStatus());

        imitsStatus = null;
        if ((gene.getNullAlleleProductionStatus() != null) && ( ! gene.getNullAlleleProductionStatus().trim().isEmpty())) {
            imitsStatus = imitsStatusByStatus.get(gene.getNullAlleleProductionStatus());
        }
        gene.setRiNullAlleleProductionStatus(imitsStatus == null ? null : imitsStatus.getGeneStatus());

        imitsStatus = null;
        if ((gene.getPhenotypingStatus() != null) && ( ! gene.getPhenotypingStatus().trim().isEmpty())) {
            imitsStatus = imitsStatusByStatus.get(gene.getPhenotypingStatus());
        }
        gene.setRiPhenotypingStatus(imitsStatus == null ? null : imitsStatus.getGeneStatus());

        // If the Gene record hasn't changed, skip it (i.e. return null)
        Gene cachedGene = genesByPk.get(gene.getMgiAccessionId());
        if (cachedGene != null) {
            if (gene.equals(cachedGene)) {
                return null;
            }
        }

        // Further iMits validation rules from Peter Matthews
        gene = Validator.validate(gene, errMessages);

        return gene;
    }


    public Set<String> getErrMessages() {
        return errMessages;
    }
}