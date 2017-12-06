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

import java.util.List;
import uk.ac.ebi.phenodigm.model.Disease;
import uk.ac.ebi.phenodigm.model.DiseaseIdentifier;
import uk.ac.ebi.phenodigm.model.Gene;
import uk.ac.ebi.phenodigm.model.GeneIdentifier;
import uk.ac.ebi.phenodigm.model.PhenotypeTerm;
import uk.ac.ebi.phenodigm.web.DiseaseAssociationSummary;
import uk.ac.ebi.phenodigm.web.DiseaseGeneAssociationDetail;
import uk.ac.ebi.phenodigm.web.GeneAssociationSummary;

/**
 * Data access for the web views 
 * @author Jules Jacobsen <jules.jacobsen@sanger.ac.uk>
 * 
 * @deprecated pdsimplify
 */
public interface PhenoDigmWebDao {
    
    /**
     * Returns a Disease object for a given DiseaseIdentifier.
     * @param diseaseId
     * @return
     */
    public Disease getDisease(DiseaseIdentifier diseaseId);
 
    public List<PhenotypeTerm> getDiseasePhenotypes(DiseaseIdentifier diseaseId);

    public Gene getGene(GeneIdentifier geneIdentifier);

    /**
     * The disease page view. Use the minRawScoreCutoff to set the lower-level limit
     * for the phenodigm score of a gene-disease association. High confidence scores 
     * are generally above 2.0
     * 
     * @param diseaseId
     * @param minRawScoreCutoff
     * @return 
     */
    public List<GeneAssociationSummary> getDiseaseToGeneAssociationSummaries(DiseaseIdentifier diseaseId, double minRawScoreCutoff);

    /**
     * The gene page view. Use the minRawScoreCutoff to set the lower-level limit
     * for the phenodigm score of a gene-disease association. High confidence scores 
     * are generally above 2.0
     * 
     * @param geneId
     * @param minRawScoreCutoff
     * @return 
     */
    public List<DiseaseAssociationSummary> getGeneToDiseaseAssociationSummaries(GeneIdentifier geneId, double minRawScoreCutoff);
    
    /**
     * Returns details of the Disease-Gene association.
     * @param diseaseId
     * @param geneId
     * @return 
     */
    public DiseaseGeneAssociationDetail getDiseaseGeneAssociationDetail(DiseaseIdentifier diseaseId, GeneIdentifier geneId);

}
