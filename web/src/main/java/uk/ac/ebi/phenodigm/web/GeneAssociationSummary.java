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

package uk.ac.ebi.phenodigm.web;

import uk.ac.ebi.phenodigm.model.GeneIdentifier;

/**
 *
 * @author Jules Jacobsen <jules.jacobsen@sanger.ac.uk>
 * 
 * @deprecated pdsimplify
 */
public class GeneAssociationSummary {
    
    private final GeneIdentifier hgncGeneIdentifier;
    private final GeneIdentifier modelGeneIdentifier;
    private final AssociationSummary associationSummary;

    public GeneAssociationSummary(GeneIdentifier hgncGeneIdentifier, GeneIdentifier modelGeneIdentifier, AssociationSummary associationSummary) {
        this.hgncGeneIdentifier = hgncGeneIdentifier;
        this.modelGeneIdentifier = modelGeneIdentifier;
        this.associationSummary = associationSummary;
    }

    public GeneIdentifier getHgncGeneIdentifier() {
        return hgncGeneIdentifier;
    }

    public GeneIdentifier getModelGeneIdentifier() {
        return modelGeneIdentifier;
    }

    public AssociationSummary getAssociationSummary() {
        return associationSummary;
    }

    @Override
    public String toString() {
        return String.format("GeneAssociationSummary{%s %s %s}", hgncGeneIdentifier, modelGeneIdentifier, associationSummary);
    }
    
}
