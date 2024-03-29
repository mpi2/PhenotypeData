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

package org.mousephenotype.cda.solr.web.dto;

import java.util.List;
import java.util.Objects;

/**
 *
 * @author mrelac
 * 
 * This class encapsulates the code and data necessary to represent a single
 * pubmed paper. Each such paper may have multiple:
 * <ul><li>allele symbols</li>
 * <li>allele ids</li>
 * <li>IMPC gene links</li>
 * <li>MGI allele names</li>
 * <li>grant ids</li>
 * <li>grant agencies</li>
 * <li>references to other papers</li></ul>
 */
public class ReferenceDTO {
    private List<String> alleleSymbols;
    private List<String> alleleAccessionIds;
    private List<String> geneAccessionIds;
    private List<String> impcGeneLinks;
    private List<String> mgiAlleleNames;
    private String       title;
    private String       journal;
    private String       pmid;
    private String       dateOfPublication;
    private List<String> grantIds;
    private List<String> grantAgencies;
    private List<String> paperUrls;
    
    public List<String> getAlleleSymbols() {
        return alleleSymbols;
    }

    public void setAlleleSymbols(List<String> alleleSymbols) {
        this.alleleSymbols = alleleSymbols;
    }

    public List<String> getAlleleAccessionIds() {
        return alleleAccessionIds;
    }

    public void setAlleleAccessionIds(List<String> alleleAccessionIds) {
        this.alleleAccessionIds = alleleAccessionIds;
    }

    public List<String> getGeneAccessionIds() {
        return geneAccessionIds;
    }

    public void setGeneAccessionIds(List<String> geneAccessionIds) {
        this.geneAccessionIds = geneAccessionIds;
    }

    public List<String> getImpcGeneLinks() {
        return impcGeneLinks;
    }

    public void setImpcGeneLinks(List<String> impcGeneLinks) {
        this.impcGeneLinks = impcGeneLinks;
    }

    public List<String> getMgiAlleleNames() {
        return mgiAlleleNames;
    }

    public void setMgiAlleleNames(List<String> mgiAlleleNames) {
        this.mgiAlleleNames = mgiAlleleNames;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getJournal() {
        return journal;
    }

    public void setJournal(String journal) {
        this.journal = journal;
    }

    public String getPmid() {
        return pmid;
    }

    public void setPmid(String pmid) {
        this.pmid = pmid;
    }

    public String getDateOfPublication() {
        return dateOfPublication;
    }

    public void setDateOfPublication(String dateOfPublication) {
        this.dateOfPublication = dateOfPublication;
    }

    public List<String> getGrantIds() {
        return grantIds;
    }

    public void setGrantIds(List<String> grantIds) {
        this.grantIds = grantIds;
    }

    public List<String> getGrantAgencies() {
        return grantAgencies;
    }

    public void setGrantAgencies(List<String> grantAgencies) {
        this.grantAgencies = grantAgencies;
    }

    public List<String> getPaperUrls() {
        return paperUrls;
    }

    public void setPaperUrls(List<String> paperUrls) {
        this.paperUrls = paperUrls;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + Objects.hashCode(this.alleleSymbols);
        hash = 37 * hash + Objects.hashCode(this.alleleAccessionIds);
        hash = 37 * hash + Objects.hashCode(this.geneAccessionIds);
        hash = 37 * hash + Objects.hashCode(this.impcGeneLinks);
        hash = 37 * hash + Objects.hashCode(this.mgiAlleleNames);
        hash = 37 * hash + Objects.hashCode(this.title);
        hash = 37 * hash + Objects.hashCode(this.journal);
        hash = 37 * hash + Objects.hashCode(this.pmid);
        hash = 37 * hash + Objects.hashCode(this.dateOfPublication);
        hash = 37 * hash + Objects.hashCode(this.grantIds);
        hash = 37 * hash + Objects.hashCode(this.grantAgencies);
        hash = 37 * hash + Objects.hashCode(this.paperUrls);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ReferenceDTO other = (ReferenceDTO) obj;
        if ( ! Objects.equals(this.alleleSymbols, other.alleleSymbols)) {
            return false;
        }
        if ( ! Objects.equals(this.alleleAccessionIds, other.alleleAccessionIds)) {
            return false;
        }
        if ( ! Objects.equals(this.geneAccessionIds, other.geneAccessionIds)) {
            return false;
        }
        if ( ! Objects.equals(this.impcGeneLinks, other.impcGeneLinks)) {
            return false;
        }
        if ( ! Objects.equals(this.mgiAlleleNames, other.mgiAlleleNames)) {
            return false;
        }
        if ( ! Objects.equals(this.title, other.title)) {
            return false;
        }
        if ( ! Objects.equals(this.journal, other.journal)) {
            return false;
        }
        if ( ! Objects.equals(this.pmid, other.pmid)) {
            return false;
        }
        if ( ! Objects.equals(this.dateOfPublication, other.dateOfPublication)) {
            return false;
        }
        if ( ! Objects.equals(this.grantIds, other.grantIds)) {
            return false;
        }
        if ( ! Objects.equals(this.grantAgencies, other.grantAgencies)) {
            return false;
        }
        if ( ! Objects.equals(this.paperUrls, other.paperUrls)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ReferenceDTO{" + "alleleSymbols=" + alleleSymbols + ", alleleIds=" + ", geneAccessionIds=" + geneAccessionIds + ", impcGeneLinks=" + impcGeneLinks + ", mgiAlleleNames=" + mgiAlleleNames + ", title=" + title + ", journal=" + journal + ", pmid=" + pmid + ", dateOfPublication=" + dateOfPublication + ", grantIds=" + grantIds + ", grantAgencies=" + grantAgencies + ", paperLinks=" + paperUrls + '}';
    }
    
    
    
    
}
